@file:Suppress("unused", "DEPRECATION")

package net.samystudio.rxlocationmanager.altitude

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import androidx.annotation.RequiresPermission
import androidx.annotation.VisibleForTesting
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import net.samystudio.rxlocationmanager.ContextProvider
import net.samystudio.rxlocationmanager.RxLocationManager
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Reactive helpers to get altitude using GPS, barometric sensor or remote service.
 */
object RxLocationManagerAltitude {
    /**
     * Get an [Observable] that emmit ellipsoidal altitude, this is the same as [Location.getAltitude],
     * in most case you'll prefer getting the altitude from geoid [observeGpsGeoidalAltitudeUpdates].
     */
    @RequiresPermission(ACCESS_FINE_LOCATION)
    @JvmStatic
    fun observeGpsEllipsoidalAltitudeUpdates(): Observable<Double> =
        RxLocationManager.observeNmea().filter {
            parseNmeaAltitude(
                it.message,
                true
            ) != null
        }.map { parseNmeaAltitude(it.message, true)!! }.distinctUntilChanged()


    /**
     * Get an [Observable] that emmit geoidal altitude (also known as mean sea level altitude).
     */
    @RequiresPermission(ACCESS_FINE_LOCATION)
    @JvmStatic
    fun observeGpsGeoidalAltitudeUpdates(): Observable<Double> =
        RxLocationManager.observeNmea().filter {
            parseNmeaAltitude(
                it.message,
                false
            ) != null
        }.map { parseNmeaAltitude(it.message, true)!! }.distinctUntilChanged()

    /**
     * Get an [Observable] that emmit altitude using barometric sensor. If barometric sensor is not
     * present this will emit immediately a [BarometricSensorException]. You can pass an optional
     * [sensorDelay] and [pressureAtSeaLevel]. To get a accurate altitude passing a
     * [pressureAtSeaLevel] is recommended.
     */
    @JvmStatic
    @JvmOverloads
    fun observeBarometricAltitudeUpdates(
        sensorDelay: Int = SensorManager.SENSOR_DELAY_NORMAL,
        pressureAtSeaLevel: Float = SensorManager.PRESSURE_STANDARD_ATMOSPHERE
    ): Observable<Double> =
        observeBarometricAltitudeUpdates(sensorDelay, Observable.just(pressureAtSeaLevel))

    /**
     * Get an [Observable] that emmit altitude using barometric sensor. If barometric sensor is not
     * present this will emit immediately a [BarometricSensorException]. You can pass an optional
     * [sensorDelay] and [pressureAtSeaLevelObservable]. To get a accurate altitude passing a
     * [pressureAtSeaLevelObservable] is recommended.
     */
    @JvmStatic
    fun observeBarometricAltitudeUpdates(
        sensorDelay: Int = SensorManager.SENSOR_DELAY_NORMAL,
        pressureAtSeaLevelObservable: Observable<Float> = Observable.just(SensorManager.PRESSURE_STANDARD_ATMOSPHERE)
    ): Observable<Double> =
        Observable.combineLatest(
            pressureAtSeaLevelObservable.distinctUntilChanged(),
            BarometricSensorObservable(sensorDelay),
            BiFunction { t1: Float, t2: Float ->
                SensorManager.getAltitude(t1, t2).toDouble()
            }).distinctUntilChanged()


    /**
     * Get an [Single] that emmit altitude from a [RemoteServiceAltitude] at the specified
     * [latitude] and [longitude].
     */
    @JvmStatic
    fun getRemoteServiceAltitude(
        remoteServiceAltitude: RemoteServiceAltitude,
        latitude: Double,
        longitude: Double
    ): Single<Double> {
        return Single.create { emitter ->
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
    }

    @VisibleForTesting
    internal fun parseNmeaAltitude(nmea: String, ellipsoidal: Boolean): Double? {
        if (nmea.startsWith("$")) {
            val tokens = nmea.split(",")
            val type = tokens[0]

            if (type.startsWith("\$GPGGA") && tokens.size >= 10) {
                try {
                    val altitude = tokens[9].toDouble()
                    return if (!ellipsoidal) altitude else {
                        if (tokens.size >= 12) {
                            val ellipsoidalOffset: Double = tokens[11].toDouble()
                            return altitude + ellipsoidalOffset
                        }
                        return null
                    }
                } catch (e: NumberFormatException) {

                }
            }
        }
        return null
    }

    @VisibleForTesting
    internal class BarometricSensorObservable(private val sensorDelay: Int) : Observable<Float>() {
        private val sensorManager =
            ContextProvider.applicationContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        private val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)

        override fun subscribeActual(observer: Observer<in Float>) {
            val listener = Listener(observer, sensorManager)
            observer.onSubscribe(listener)
            sensorManager.registerListener(listener, sensor, sensorDelay)

            if (sensor == null) observer.onError(
                BarometricSensorException(
                    "Barometric sensor is not available"
                )
            )
        }

        class Listener(
            private val observer: Observer<in Float>,
            private val sensorManager: SensorManager? = null
        ) : Disposable, SensorEventListener {
            private val unSubscribed = AtomicBoolean()

            override fun isDisposed() = unSubscribed.get()

            override fun dispose() {
                if (unSubscribed.compareAndSet(false, true)) {
                    sensorManager?.unregisterListener(this)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

            override fun onSensorChanged(event: SensorEvent) {
                observer.onNext(event.values[0])
            }
        }
    }
}