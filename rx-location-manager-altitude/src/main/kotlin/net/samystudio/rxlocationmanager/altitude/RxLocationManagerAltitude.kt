@file:Suppress("unused", "DEPRECATION")

package net.samystudio.rxlocationmanager.altitude

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Looper
import androidx.annotation.RequiresPermission
import androidx.annotation.VisibleForTesting
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import net.samystudio.rxlocationmanager.ContextProvider
import net.samystudio.rxlocationmanager.RxLocationManager
import net.samystudio.rxlocationmanager.nmea.GGA
import net.samystudio.rxlocationmanager.nmea.NmeaException
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Reactive helpers to get altitude using GPS, barometric sensor or remote service.
 */
object RxLocationManagerAltitude {
    /**
     * Get an [Observable] that emit ellipsoidal altitude, this is the same as [Location.getAltitude],
     * in most case you'll prefer getting the altitude from geoid [observeGpsGeoidalAltitudeUpdates].
     */
    @RequiresPermission(ACCESS_FINE_LOCATION)
    @JvmStatic
    fun observeGpsEllipsoidalAltitudeUpdates(
        looper: Looper = Looper.myLooper() ?: Looper.getMainLooper(),
    ): Observable<Double> =
        RxLocationManager.observeNmea(looper)
            .flatMap {
                try {
                    val gga = GGA(it.message)
                    if (gga.altitude != null && gga.ellipsoidalOffset != null) {
                        return@flatMap Observable.just(GGAWrapper(gga))
                    }
                } catch (_: NmeaException) {
                }
                Observable.just(GGAWrapper(null))
            }
            .filter { it.gga != null }
            .map { it.gga!!.altitude!! + it.gga.ellipsoidalOffset!! }
            .distinctUntilChanged()

    /**
     * Get an [Observable] that emit geoidal altitude (also known as mean sea level altitude).
     */
    @RequiresPermission(ACCESS_FINE_LOCATION)
    @JvmStatic
    fun observeGpsGeoidalAltitudeUpdates(
        looper: Looper = Looper.myLooper() ?: Looper.getMainLooper(),
    ): Observable<Double> =
        RxLocationManager.observeNmea(looper)
            .flatMap {
                try {
                    val gga = GGA(it.message)
                    if (gga.altitude != null) {
                        return@flatMap Observable.just(GGAWrapper(gga))
                    }
                } catch (_: NmeaException) {
                }
                Observable.just(GGAWrapper(null))
            }
            .filter { it.gga != null }
            .map { it.gga!!.altitude!! }
            .distinctUntilChanged()

    /**
     * Get an [Observable] that emit altitude using barometric sensor. If barometric sensor is not
     * present this will emit immediately a [BarometricSensorException]. You can pass an optional
     * [sensorDelay] and [pressureAtSeaLevel]. To get a accurate altitude passing a
     * [pressureAtSeaLevel] is recommended.
     */
    @JvmStatic
    @JvmOverloads
    fun observeBarometricAltitudeUpdates(
        sensorDelay: Int = SensorManager.SENSOR_DELAY_NORMAL,
        pressureAtSeaLevel: Float = SensorManager.PRESSURE_STANDARD_ATMOSPHERE,
        minimumAccuracy: Int = -1,
    ): Observable<Double> =
        observeBarometricAltitudeUpdates(
            sensorDelay,
            Observable.just(pressureAtSeaLevel),
            minimumAccuracy,
        )

    /**
     * Get an [Observable] that emit altitude using barometric sensor. If barometric sensor is not
     * present this will emit immediately a [BarometricSensorException]. You can pass an optional
     * [sensorDelay] and [pressureAtSeaLevelObservable]. To get a accurate altitude passing a
     * [pressureAtSeaLevelObservable] is recommended.
     */
    @JvmStatic
    fun observeBarometricAltitudeUpdates(
        sensorDelay: Int = SensorManager.SENSOR_DELAY_NORMAL,
        pressureAtSeaLevelObservable: Observable<Float> = Observable.just(SensorManager.PRESSURE_STANDARD_ATMOSPHERE),
        minimumAccuracy: Int = -1,
    ): Observable<Double> =
        Observable.combineLatest(
            pressureAtSeaLevelObservable.distinctUntilChanged(),
            BarometricSensorObservable(sensorDelay)
                .filter { it.first != null && it.second >= minimumAccuracy }
                .map { it.first!! },
        ) { t1: Float, t2: Float ->
            SensorManager.getAltitude(t1, t2).toDouble()
        }.distinctUntilChanged()

    /**
     * Get an [Observable] that emit altitude along with accuracy using barometric sensor.
     * If barometric sensor is not present this will emit immediately a [BarometricSensorException].
     * You can pass an optional [sensorDelay] and [pressureAtSeaLevel]. To get a accurate altitude
     * passing a [pressureAtSeaLevel] is recommended.
     */
    @JvmStatic
    @JvmOverloads
    fun observeBarometricAltitudeAndAccuracyUpdates(
        sensorDelay: Int = SensorManager.SENSOR_DELAY_NORMAL,
        pressureAtSeaLevel: Float = SensorManager.PRESSURE_STANDARD_ATMOSPHERE,
    ): Observable<Pair<Double?, Int>> =
        observeBarometricAltitudeAndAccuracyUpdates(
            sensorDelay,
            Observable.just(pressureAtSeaLevel),
        )

    /**
     * Get an [Observable] that emit altitude along with accuracy using barometric sensor. If
     * barometric sensor is not present this will emit immediately a [BarometricSensorException].
     * You can pass an optional [sensorDelay] and [pressureAtSeaLevelObservable]. To get a accurate
     * altitude passing a [pressureAtSeaLevelObservable] is recommended.
     */
    @JvmStatic
    fun observeBarometricAltitudeAndAccuracyUpdates(
        sensorDelay: Int = SensorManager.SENSOR_DELAY_NORMAL,
        pressureAtSeaLevelObservable: Observable<Float> = Observable.just(SensorManager.PRESSURE_STANDARD_ATMOSPHERE),
    ): Observable<Pair<Double?, Int>> =
        Observable.combineLatest(
            pressureAtSeaLevelObservable.distinctUntilChanged(),
            BarometricSensorObservable(sensorDelay),
        ) { t1: Float, t2: Pair<Float?, Int> ->
            t2.first?.let { SensorManager.getAltitude(t1, it).toDouble() } to t2.second
        }.distinctUntilChanged()

    /**
     * Get an [Single] that emit altitude from a [RemoteServiceAltitude] at the specified
     * [latitude] and [longitude].
     */
    @JvmStatic
    fun getRemoteServiceAltitude(
        remoteServiceAltitude: RemoteServiceAltitude,
        latitude: Double,
        longitude: Double,
    ): Single<Double> =
        Single.create { emitter ->
            try {
                val httpConnection =
                    remoteServiceAltitude.getHttpURLConnection(latitude, longitude)
                val input = BufferedReader(InputStreamReader(httpConnection.inputStream))
                val response = StringBuffer()
                input.lineSequence().forEach { response.append(it) }
                input.close()

                emitter.onSuccess(remoteServiceAltitude.parseAltitude(response.toString()))
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }

    private class GGAWrapper(val gga: GGA?)

    @VisibleForTesting
    internal class BarometricSensorObservable(private val sensorDelay: Int) :
        Observable<Pair<Float?, Int>>() {
        private val sensorManager =
            ContextProvider.applicationContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        private val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)

        override fun subscribeActual(observer: Observer<in Pair<Float?, Int>>) {
            val listener = Listener(observer, sensorManager)
            observer.onSubscribe(listener)

            if (sensor == null) {
                observer.onError(BarometricSensorException("Barometric sensor is not available"))
            } else {
                sensorManager.registerListener(listener, sensor, sensorDelay)
            }
        }

        class Listener(
            private val observer: Observer<in Pair<Float?, Int>>,
            private val sensorManager: SensorManager? = null,
        ) : Disposable, SensorEventListener {
            private val unSubscribed = AtomicBoolean()

            override fun isDisposed() = unSubscribed.get()

            override fun dispose() {
                if (unSubscribed.compareAndSet(false, true)) {
                    sensorManager?.unregisterListener(this)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                observer.onNext(null to accuracy)
            }

            override fun onSensorChanged(event: SensorEvent) {
                observer.onNext(event.values[0] to event.accuracy)
            }
        }
    }
}
