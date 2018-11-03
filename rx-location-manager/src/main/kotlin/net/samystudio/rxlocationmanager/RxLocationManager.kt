@file:Suppress("unused", "DEPRECATION", "MissingPermission", "MemberVisibilityCanBePrivate")

package net.samystudio.rxlocationmanager

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.TargetApi
import android.content.Context
import android.location.*
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.annotation.VisibleForTesting
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import net.samystudio.rxlocationmanager.RxLocationManager.observeGnssStatus
import java.util.concurrent.atomic.AtomicBoolean

/**
 * A reactive wrapper for [LocationManager] callbacks.
 * Note there is no [LocationManager.addGpsStatusListener] observable, you can use
 * [observeGnssStatus] instead.
 */
object RxLocationManager {
    @JvmStatic
    val locationManager by lazy {
        ContextProvider.applicationContext.getSystemService(
            Context.LOCATION_SERVICE
        ) as LocationManager
    }

    /**
     * [NmeaEvent]
     * [LocationManager.addNmeaListener]
     */
    @RequiresPermission(ACCESS_FINE_LOCATION)
    @JvmStatic
    fun observeNmea(): Observable<NmeaEvent> = NmeaObservable(locationManager)

    /**
     * [LocationManager.registerGnssMeasurementsCallback]
     * [GnssMeasurementsEvent]
     * [GnssMeasurementsEvent.Callback.onGnssMeasurementsReceived]
     * [GnssMeasurementsEvent.Callback.onStatusChanged]
     */
    @RequiresPermission(ACCESS_FINE_LOCATION)
    @RequiresApi(Build.VERSION_CODES.N)
    @JvmStatic
    fun observeGnssMeasurements(): Observable<GnssMeasurementsState> =
        GnssMeasurementsObservable(locationManager)


    /**
     * [LocationManager.registerGnssMeasurementsCallback]
     * [GnssMeasurementsEvent]
     * [GnssMeasurementsEvent.Callback.onGnssMeasurementsReceived]
     */
    @RequiresPermission(ACCESS_FINE_LOCATION)
    @RequiresApi(Build.VERSION_CODES.N)
    @JvmStatic
    fun observeGnssMeasurementsEvent(): Observable<GnssMeasurementsEvent> =
        observeGnssMeasurements()
            .filter { it is GnssMeasurementsState.StateEvent }
            .map { (it as GnssMeasurementsState.StateEvent).event }

    /**
     * [LocationManager.registerGnssMeasurementsCallback]
     * [GnssMeasurementsEvent]
     * [GnssMeasurementsEvent.Callback.onStatusChanged]
     */
    @RequiresPermission(ACCESS_FINE_LOCATION)
    @RequiresApi(Build.VERSION_CODES.N)
    @JvmStatic
    fun observeGnssMeasurementsStatus(): Observable<Int> =
        observeGnssMeasurements()
            .filter { it is GnssMeasurementsState.StateStatus }
            .map { (it as GnssMeasurementsState.StateStatus).status }

    /**
     * [LocationManager.registerGnssNavigationMessageCallback]
     * [GnssNavigationMessage]
     * [GnssNavigationMessage.Callback.onGnssNavigationMessageReceived]
     * [GnssNavigationMessage.Callback.onStatusChanged]
     */
    @RequiresPermission(ACCESS_FINE_LOCATION)
    @RequiresApi(Build.VERSION_CODES.N)
    @JvmStatic
    fun observeGnssNavigationMessage(): Observable<GnssNavigationMessageState> =
        GnssNavigationMessageObservable(locationManager)

    /**
     * [LocationManager.registerGnssNavigationMessageCallback]
     * [GnssNavigationMessage]
     * [GnssNavigationMessage.Callback.onGnssNavigationMessageReceived]
     */
    @RequiresPermission(ACCESS_FINE_LOCATION)
    @RequiresApi(Build.VERSION_CODES.N)
    @JvmStatic
    fun observeGnssNavigationMessageEvent(): Observable<GnssNavigationMessage> =
        observeGnssNavigationMessage()
            .filter { it is GnssNavigationMessageState.StateEvent }
            .map { (it as GnssNavigationMessageState.StateEvent).event }

    /**
     * [LocationManager.registerGnssNavigationMessageCallback]
     * [GnssNavigationMessage]
     * [GnssNavigationMessage.Callback.onStatusChanged]
     */
    @RequiresPermission(ACCESS_FINE_LOCATION)
    @RequiresApi(Build.VERSION_CODES.N)
    @JvmStatic
    fun observeGnssNavigationMessageStatus(): Observable<Int> =
        observeGnssNavigationMessage()
            .filter { it is GnssNavigationMessageState.StateStatus }
            .map { (it as GnssNavigationMessageState.StateStatus).status }

    /**
     * [LocationManager.registerGnssStatusCallback]
     * [GnssStatusState]
     * [GnssStatusState.StateChanged]
     * [GnssStatusState.StateFirstFix]
     * [GnssStatusState.StateStarted]
     * [GnssStatusState.StateStopped]
     */
    @RequiresPermission(ACCESS_FINE_LOCATION)
    @JvmStatic
    fun observeGnssStatus(): Observable<GnssStatusState> = GnssStatusObservable(locationManager)

    /**
     * Prior to [Build.VERSION_CODES.N] [GnssStatusState.StateChanged.status] will always be null.
     *
     * [LocationManager.registerGnssStatusCallback]
     * [GnssStatusState]
     * [GnssStatusState.StateChanged]
     */
    @RequiresPermission(ACCESS_FINE_LOCATION)
    @JvmStatic
    fun observeGnssStatusChanged(): Observable<GnssStatus> =
        observeGnssStatus()
            .filter { t -> t is GnssStatusState.StateChanged }
            .map { t -> (t as GnssStatusState.StateChanged).status }

    /**
     * Prior to [Build.VERSION_CODES.N] [GnssStatusState.StateFirstFix.ttffMillis] will always be
     * null.
     *
     * [LocationManager.registerGnssStatusCallback]
     * [GnssStatusState]
     * [GnssStatusState.StateFirstFix]
     */
    @RequiresPermission(ACCESS_FINE_LOCATION)
    @JvmStatic
    fun observeGnssStatusOnFirstFix(): Observable<Int> =
        observeGnssStatus()
            .filter { t -> t is GnssStatusState.StateFirstFix }
            .map { t -> (t as GnssStatusState.StateFirstFix).ttffMillis }

    /**
     * [LocationManager.registerGnssStatusCallback]
     * [GnssStatusState]
     * [GnssStatusState.StateStarted]
     */
    @RequiresPermission(ACCESS_FINE_LOCATION)
    @JvmStatic
    fun observeGnssStatusOnStarted(): Observable<Unit> =
        observeGnssStatus()
            .filter { t -> t is GnssStatusState.StateStarted }
            .map { }

    /**
     * [LocationManager.registerGnssStatusCallback]
     * [GnssStatusState]
     * [GnssStatusState.StateStopped]
     */
    @RequiresPermission(ACCESS_FINE_LOCATION)
    @JvmStatic
    fun observeGnssStatusOnStopped(): Observable<Unit> =
        observeGnssStatus()
            .filter { t -> t is GnssStatusState.StateStopped }
            .map { }

    /**
     * You  should consider using [LocationServices](https://developers.google.com/android/reference/com/google/android/gms/location/LocationServices)
     * to fetch a [Location]
     *
     * [LocationManager.requestLocationUpdates] (String, Long, Float)
     * [LocationUpdatesState]
     */
    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    @JvmStatic
    fun observeLocationUpdatesState(
        provider: Provider,
        minTime: Long,
        minDistance: Float
    ): Observable<LocationUpdatesState> =
        LocationUpdatesObservable(locationManager, provider, minTime, minDistance)

    /**
     * You  should consider using [LocationServices](https://developers.google.com/android/reference/com/google/android/gms/location/LocationServices)
     * to fetch a [Location]
     *
     * [LocationManager.requestLocationUpdates] (Criteria, Long, Float)
     * [LocationUpdatesState]
     */
    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    @JvmStatic
    fun observeLocationUpdatesState(
        criteria: Criteria,
        minTime: Long,
        minDistance: Float
    ): Observable<LocationUpdatesState> =
        LocationUpdatesObservable(locationManager, criteria, minTime, minDistance)

    /**
     * You  should consider using [LocationServices](https://developers.google.com/android/reference/com/google/android/gms/location/LocationServices)
     * to fetch a [Location]
     *
     * [LocationManager.requestLocationUpdates] (String, Long, Float)
     * [LocationUpdatesState.StateLocationChanged]
     */
    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    @JvmStatic
    fun observeLocationChanged(
        provider: Provider,
        minTime: Long,
        minDistance: Float
    ): Observable<Location> =
        observeLocationUpdatesState(
            provider,
            minTime,
            minDistance
        ).filter { t -> t is LocationUpdatesState.StateLocationChanged }
            .map { t -> (t as LocationUpdatesState.StateLocationChanged).location }

    /**
     * You  should consider using [LocationServices](https://developers.google.com/android/reference/com/google/android/gms/location/LocationServices)
     * to fetch a [Location]
     *
     * [LocationManager.requestLocationUpdates] (Criteria, Long, Float)
     * [LocationUpdatesState.StateLocationChanged]
     */
    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    @JvmStatic
    fun observeLocationChanged(
        criteria: Criteria,
        minTime: Long,
        minDistance: Float
    ): Observable<Location> =
        observeLocationUpdatesState(
            criteria,
            minTime,
            minDistance
        ).filter { t -> t is LocationUpdatesState.StateLocationChanged }
            .map { t -> (t as LocationUpdatesState.StateLocationChanged).location }

    /**
     * [LocationManager.requestLocationUpdates] (String, Long, Float)
     * [LocationUpdatesState.StateStatusChanged]
     */
    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    @JvmStatic
    fun observeLocationStatusChanged(
        provider: Provider,
        minTime: Long,
        minDistance: Float
    ): Observable<LocationUpdatesState.StateStatusChanged> =
        observeLocationUpdatesState(
            provider,
            minTime,
            minDistance
        ).filter { t -> t is LocationUpdatesState.StateStatusChanged }
            .map { t -> (t as LocationUpdatesState.StateStatusChanged) }

    /**
     * [LocationManager.requestLocationUpdates] (Criteria, Long, Float)
     * [LocationUpdatesState.StateStatusChanged]
     */
    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    @JvmStatic
    fun observeLocationStatusChanged(
        criteria: Criteria,
        minTime: Long,
        minDistance: Float
    ): Observable<LocationUpdatesState.StateStatusChanged> = observeLocationUpdatesState(
        criteria,
        minTime,
        minDistance
    ).filter { t -> t is LocationUpdatesState.StateStatusChanged }
        .map { t -> (t as LocationUpdatesState.StateStatusChanged) }

    /**
     * [LocationManager.requestLocationUpdates] (String, Long, Float)
     * [LocationUpdatesState.StateProviderEnabled]
     */
    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    @JvmStatic
    fun observeLocationProviderEnabled(
        provider: Provider,
        minTime: Long,
        minDistance: Float
    ): Observable<LocationUpdatesState.StateProviderEnabled> =
        observeLocationUpdatesState(
            provider,
            minTime,
            minDistance
        ).filter { t -> t is LocationUpdatesState.StateProviderEnabled }
            .map { t -> (t as LocationUpdatesState.StateProviderEnabled) }

    /**
     * [LocationManager.requestLocationUpdates] (Criteria, Long, Float)
     * [LocationUpdatesState.StateProviderEnabled]
     */
    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    @JvmStatic
    fun observeLocationProviderEnabled(
        criteria: Criteria,
        minTime: Long,
        minDistance: Float
    ): Observable<LocationUpdatesState.StateProviderEnabled> =
        observeLocationUpdatesState(
            criteria,
            minTime,
            minDistance
        ).filter { t -> t is LocationUpdatesState.StateProviderEnabled }
            .map { t -> (t as LocationUpdatesState.StateProviderEnabled) }

    /**
     * [LocationManager.requestLocationUpdates] (String, Long, Float)
     * [LocationUpdatesState.StateProviderDisabled]
     */
    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    @JvmStatic
    fun observeLocationProviderDisabled(
        provider: Provider,
        minTime: Long,
        minDistance: Float
    ): Observable<LocationUpdatesState.StateProviderDisabled> =
        observeLocationUpdatesState(
            provider,
            minTime,
            minDistance
        ).filter { t -> t is LocationUpdatesState.StateProviderDisabled }
            .map { t -> (t as LocationUpdatesState.StateProviderDisabled) }

    /**
     * [LocationManager.requestLocationUpdates] (Criteria, Long, Float)
     * [LocationUpdatesState.StateProviderDisabled]
     */
    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    @JvmStatic
    fun observeLocationProviderDisabled(
        criteria: Criteria,
        minTime: Long,
        minDistance: Float
    ): Observable<LocationUpdatesState.StateProviderDisabled> =
        observeLocationUpdatesState(
            criteria,
            minTime,
            minDistance
        ).filter { t -> t is LocationUpdatesState.StateProviderDisabled }
            .map { t -> (t as LocationUpdatesState.StateProviderDisabled) }

    /**
     * [LocationManager.NETWORK_PROVIDER]
     * [LocationManager.GPS_PROVIDER]
     * [LocationManager.PASSIVE_PROVIDER]
     */
    enum class Provider {
        NETWORK, GPS, PASSIVE
    }

    @VisibleForTesting
    internal class NmeaObservable(private val locationManager: LocationManager?) :
        Observable<NmeaEvent>() {
        override fun subscribeActual(observer: Observer<in NmeaEvent>) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val listener = NListener(observer, locationManager)
                observer.onSubscribe(listener)
                locationManager?.addNmeaListener(listener)
            } else {
                val listener = Listener(observer, locationManager)
                observer.onSubscribe(listener)
                locationManager?.addNmeaListener(listener)
            }
        }

        class Listener(
            private val observer: Observer<in NmeaEvent>,
            private val locationManager: LocationManager?
        ) : AtomicDisposable(), GpsStatus.NmeaListener {
            override fun onDispose() {
                locationManager?.removeNmeaListener(this)
            }

            override fun onNmeaReceived(timestamp: Long, nmea: String) {
                observer.onNext(NmeaEvent(nmea, timestamp))
            }
        }

        @TargetApi(Build.VERSION_CODES.N)
        class NListener(
            private val observer: Observer<in NmeaEvent>,
            private val locationManager: LocationManager?
        ) : AtomicDisposable(), OnNmeaMessageListener {
            override fun onDispose() {
                locationManager?.removeNmeaListener(this)
            }

            override fun onNmeaMessage(message: String, timestamp: Long) {
                observer.onNext(NmeaEvent(message, timestamp))
            }
        }
    }

    @VisibleForTesting
    @TargetApi(Build.VERSION_CODES.N)
    internal class GnssMeasurementsObservable(private val locationManager: LocationManager?) :
        Observable<GnssMeasurementsState>() {
        override fun subscribeActual(observer: Observer<in GnssMeasurementsState>) {
            val listener = Listener(observer, locationManager)
            observer.onSubscribe(listener)
            locationManager?.registerGnssMeasurementsCallback(listener.callback)
        }

        class Listener(
            private val observer: Observer<in GnssMeasurementsState>,
            private val locationManager: LocationManager?
        ) : AtomicDisposable() {
            val callback = object : GnssMeasurementsEvent.Callback() {
                override fun onGnssMeasurementsReceived(eventArgs: GnssMeasurementsEvent) {
                    observer.onNext(GnssMeasurementsState.StateEvent(eventArgs))
                }

                override fun onStatusChanged(status: Int) {
                    observer.onNext(GnssMeasurementsState.StateStatus(status))
                }
            }

            override fun onDispose() {
                locationManager?.unregisterGnssMeasurementsCallback(callback)
            }
        }
    }

    @VisibleForTesting
    @TargetApi(Build.VERSION_CODES.N)
    internal class GnssNavigationMessageObservable(private val locationManager: LocationManager?) :
        Observable<GnssNavigationMessageState>() {
        override fun subscribeActual(observer: Observer<in GnssNavigationMessageState>) {
            val listener = Listener(observer, locationManager)
            observer.onSubscribe(listener)
            locationManager?.registerGnssNavigationMessageCallback(listener.callback)
        }

        class Listener(
            private val observer: Observer<in GnssNavigationMessageState>,
            private val locationManager: LocationManager?
        ) : AtomicDisposable() {
            val callback = object : GnssNavigationMessage.Callback() {
                override fun onGnssNavigationMessageReceived(event: GnssNavigationMessage) {
                    observer.onNext(GnssNavigationMessageState.StateEvent(event))
                }

                override fun onStatusChanged(status: Int) {
                    observer.onNext(GnssNavigationMessageState.StateStatus(status))
                }
            }

            override fun onDispose() {
                locationManager?.unregisterGnssNavigationMessageCallback(callback)
            }
        }
    }

    @VisibleForTesting
    internal class GnssStatusObservable(private val locationManager: LocationManager?) :
        Observable<GnssStatusState>() {
        override fun subscribeActual(observer: Observer<in GnssStatusState>) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val listener = NListener(observer, locationManager)
                observer.onSubscribe(listener)
                locationManager?.registerGnssStatusCallback(listener.callback)
            } else {
                val listener = Listener(observer, locationManager)
                observer.onSubscribe(listener)
                locationManager?.addGpsStatusListener(listener)
            }
        }

        class Listener(
            private val observer: Observer<in GnssStatusState>,
            private val locationManager: LocationManager?
        ) : AtomicDisposable(), GpsStatus.Listener {
            override fun onGpsStatusChanged(event: Int) {
                GpsStatus.GPS_EVENT_SATELLITE_STATUS
                when (event) {
                    GpsStatus.GPS_EVENT_SATELLITE_STATUS -> observer.onNext(GnssStatusState.StateChanged())
                    GpsStatus.GPS_EVENT_STARTED -> observer.onNext(GnssStatusState.StateStarted)
                    GpsStatus.GPS_EVENT_FIRST_FIX -> observer.onNext(GnssStatusState.StateFirstFix())
                    GpsStatus.GPS_EVENT_STOPPED -> observer.onNext(GnssStatusState.StateStopped)
                }
            }

            override fun onDispose() {
                locationManager?.removeGpsStatusListener(this)
            }
        }

        @TargetApi(Build.VERSION_CODES.N)
        class NListener(
            private val observer: Observer<in GnssStatusState>,
            private val locationManager: LocationManager?
        ) : AtomicDisposable() {
            val callback = object : GnssStatus.Callback() {
                override fun onSatelliteStatusChanged(status: GnssStatus) {
                    observer.onNext(GnssStatusState.StateChanged(status))
                }

                override fun onStarted() {
                    observer.onNext(GnssStatusState.StateStarted)
                }

                override fun onFirstFix(ttffMillis: Int) {
                    observer.onNext(GnssStatusState.StateFirstFix(ttffMillis))
                }

                override fun onStopped() {
                    observer.onNext(GnssStatusState.StateStopped)
                }
            }

            override fun onDispose() {
                locationManager?.unregisterGnssStatusCallback(callback)
            }
        }
    }

    @VisibleForTesting
    internal class LocationUpdatesObservable private constructor(
        private val locationManager: LocationManager?,
        private val minTime: Long,
        private val minDistance: Float,
        private val provider: Provider? = null,
        private val criteria: Criteria? = null
    ) : Observable<LocationUpdatesState>() {
        constructor(
            locationManager: LocationManager?,
            criteria: Criteria,
            minTime: Long,
            minDistance: Float
        ) : this(locationManager, minTime, minDistance, criteria = criteria)

        constructor(
            locationManager: LocationManager?,
            provider: Provider,
            minTime: Long,
            minDistance: Float
        ) : this(locationManager, minTime, minDistance, provider = provider)

        override fun subscribeActual(observer: Observer<in LocationUpdatesState>) {
            val listener = Listener(observer, locationManager)
            observer.onSubscribe(listener)

            provider?.let {
                locationManager?.requestLocationUpdates(
                    it.name.toLowerCase(),
                    minTime,
                    minDistance,
                    listener
                )
            } ?: criteria?.let {
                locationManager?.requestLocationUpdates(
                    minTime,
                    minDistance,
                    it,
                    listener,
                    null
                )
            }
        }

        class Listener(
            private val observer: Observer<in LocationUpdatesState>,
            private val locationManager: LocationManager?
        ) : AtomicDisposable(), LocationListener {

            override fun onDispose() {
                locationManager?.removeUpdates(this)
            }

            override fun onLocationChanged(location: Location) {
                observer.onNext(LocationUpdatesState.StateLocationChanged(location))
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle?) {
                observer.onNext(
                    LocationUpdatesState.StateStatusChanged(
                        provider,
                        status,
                        extras
                    )
                )
            }

            override fun onProviderEnabled(provider: String) {
                observer.onNext(LocationUpdatesState.StateProviderEnabled(provider))
            }

            override fun onProviderDisabled(provider: String) {
                observer.onNext(LocationUpdatesState.StateProviderDisabled(provider))
            }
        }
    }

    internal abstract class AtomicDisposable : Disposable {
        private val disposed = AtomicBoolean()
        override fun isDisposed() = disposed.get()

        override fun dispose() {
            if (disposed.compareAndSet(false, true)) {
                onDispose()
            }
        }

        abstract fun onDispose()
    }

    data class NmeaEvent(val message: String, val timestamp: Long)

    sealed class GnssMeasurementsState {
        data class StateEvent(val event: GnssMeasurementsEvent) : GnssMeasurementsState()
        data class StateStatus(val status: Int) : GnssMeasurementsState()
    }

    sealed class GnssNavigationMessageState {
        data class StateEvent(val event: GnssNavigationMessage) : GnssNavigationMessageState()
        data class StateStatus(val status: Int) : GnssNavigationMessageState()
    }

    sealed class GnssStatusState {
        object StateStarted : GnssStatusState()
        object StateStopped : GnssStatusState()
        data class StateFirstFix(val ttffMillis: Int? = null) : GnssStatusState()
        data class StateChanged(val status: GnssStatus? = null) : GnssStatusState()
    }

    sealed class LocationUpdatesState {
        data class StateProviderEnabled(val provider: String) : LocationUpdatesState()
        data class StateProviderDisabled(val provider: String) : LocationUpdatesState()
        data class StateStatusChanged(val provider: String, val status: Int, val extras: Bundle?) :
            LocationUpdatesState()

        data class StateLocationChanged(val location: Location) : LocationUpdatesState()
    }
}