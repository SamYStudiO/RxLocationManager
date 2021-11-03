@file:Suppress("MissingPermission", "DEPRECATION", "unused")

package net.samystudio.rxlocationmanager

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.TargetApi
import android.content.Context
import android.location.*
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.annotation.VisibleForTesting
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import net.samystudio.rxlocationmanager.RxLocationManager.observeGnssStatus
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * A reactive wrapper for [LocationManager] callbacks.
 *
 * Note there is no [LocationManager.addGpsStatusListener] observable, you can use
 * [observeGnssStatus] instead.
 * Note you can achieve [LocationManager.requestSingleUpdate] easily using [Observable.first],
 * [Observable.firstElement] or [Observable.firstOrError]:
 * <pre>RxLocationManager.observeLocationUpdatesState(...).firstOrError().subscribe(...)</pre>
 */
object RxLocationManager {
    /**
     * A reference to android [LocationManager] system service.
     */
    @JvmStatic
    val locationManager by lazy {
        ContextProvider.applicationContext.getSystemService(
            Context.LOCATION_SERVICE
        ) as LocationManager
    }

    /**
     * @see [LocationManager.getLastKnownLocation]
     */
    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    @JvmStatic
    fun getLastKnownLocation(provider: Provider): Maybe<Location> = Maybe.defer {
        locationManager.getLastKnownLocation(provider.name.toLowerCase(Locale.ROOT))
            ?.let { Maybe.just(it) } ?: run {
            @Suppress("RemoveExplicitTypeArguments")
            Maybe.empty<Location>()
        }
    }

    /**
     * @see [LocationManager.addNmeaListener]
     */
    @RequiresPermission(ACCESS_FINE_LOCATION)
    @JvmStatic
    fun observeNmea(handler: Handler? = null): Observable<NmeaEvent> =
        NmeaObservable(locationManager, handler)

    /**
     * @see [LocationManager.registerGnssMeasurementsCallback]
     * @see [GnssMeasurementsEvent]
     * @see [GnssMeasurementsEvent.Callback.onGnssMeasurementsReceived]
     * @see [GnssMeasurementsEvent.Callback.onStatusChanged]
     */
    @RequiresPermission(ACCESS_FINE_LOCATION)
    @RequiresApi(Build.VERSION_CODES.N)
    @JvmStatic
    fun observeGnssMeasurements(handler: Handler? = null): Observable<GnssMeasurementsState> =
        GnssMeasurementsObservable(locationManager, handler)

    /**
     * @see [LocationManager.registerGnssMeasurementsCallback]
     * @see [GnssMeasurementsEvent]
     * @see [GnssMeasurementsEvent.Callback.onGnssMeasurementsReceived]
     */
    @RequiresPermission(ACCESS_FINE_LOCATION)
    @RequiresApi(Build.VERSION_CODES.N)
    @JvmStatic
    fun observeGnssMeasurementsEvent(handler: Handler? = null): Observable<GnssMeasurementsEvent> =
        observeGnssMeasurements(handler)
            .filter { it is GnssMeasurementsState.StateEvent }
            .map { (it as GnssMeasurementsState.StateEvent).event }

    /**
     * @see [LocationManager.registerGnssMeasurementsCallback]
     * @see [GnssMeasurementsEvent]
     * @see [GnssMeasurementsEvent.Callback.onStatusChanged]
     */
    @RequiresPermission(ACCESS_FINE_LOCATION)
    @RequiresApi(Build.VERSION_CODES.N)
    @JvmStatic
    fun observeGnssMeasurementsStatus(handler: Handler? = null): Observable<Int> =
        observeGnssMeasurements(handler)
            .filter { it is GnssMeasurementsState.StateStatus }
            .map { (it as GnssMeasurementsState.StateStatus).status }

    /**
     * @see [LocationManager.registerGnssNavigationMessageCallback]
     * @see [GnssNavigationMessage]
     * @see [GnssNavigationMessage.Callback.onGnssNavigationMessageReceived]
     * @see [GnssNavigationMessage.Callback.onStatusChanged]
     */
    @RequiresPermission(ACCESS_FINE_LOCATION)
    @RequiresApi(Build.VERSION_CODES.N)
    @JvmStatic
    fun observeGnssNavigationMessage(handler: Handler? = null): Observable<GnssNavigationMessageState> =
        GnssNavigationMessageObservable(locationManager, handler)

    /**
     * @see [LocationManager.registerGnssNavigationMessageCallback]
     * @see [GnssNavigationMessage]
     * @see [GnssNavigationMessage.Callback.onGnssNavigationMessageReceived]
     */
    @RequiresPermission(ACCESS_FINE_LOCATION)
    @RequiresApi(Build.VERSION_CODES.N)
    @JvmStatic
    fun observeGnssNavigationMessageEvent(handler: Handler? = null): Observable<GnssNavigationMessage> =
        observeGnssNavigationMessage(handler)
            .filter { it is GnssNavigationMessageState.StateEvent }
            .map { (it as GnssNavigationMessageState.StateEvent).event }

    /**
     * @see [LocationManager.registerGnssNavigationMessageCallback]
     * @see [GnssNavigationMessage]
     * @see [GnssNavigationMessage.Callback.onStatusChanged]
     */
    @RequiresPermission(ACCESS_FINE_LOCATION)
    @RequiresApi(Build.VERSION_CODES.N)
    @JvmStatic
    fun observeGnssNavigationMessageStatus(handler: Handler? = null): Observable<Int> =
        observeGnssNavigationMessage(handler)
            .filter { it is GnssNavigationMessageState.StateStatus }
            .map { (it as GnssNavigationMessageState.StateStatus).status }

    /**
     * @see [LocationManager.registerGnssStatusCallback]
     * @see [GnssStatusState]
     * @see [GnssStatusState.StateChanged]
     * @see [GnssStatusState.StateFirstFix]
     * @see [GnssStatusState.StateStarted]
     * @see [GnssStatusState.StateStopped]
     */
    @RequiresPermission(ACCESS_FINE_LOCATION)
    @JvmStatic
    fun observeGnssStatus(handler: Handler? = null): Observable<GnssStatusState> =
        GnssStatusObservable(locationManager, handler)

    /**
     * Prior to [Build.VERSION_CODES.N] [GnssStatusState.StateChanged.status] will always be null.
     *
     * @see [LocationManager.registerGnssStatusCallback]
     * @see [GnssStatusState]
     * @see [GnssStatusState.StateChanged]
     */
    @RequiresPermission(ACCESS_FINE_LOCATION)
    @JvmStatic
    fun observeGnssStatusOnChanged(handler: Handler? = null): Observable<GnssStatusState.StateChanged> =
        observeGnssStatus(handler)
            .filter { it is GnssStatusState.StateChanged }
            .map { it as GnssStatusState.StateChanged }

    /**
     * Prior to [Build.VERSION_CODES.N] [GnssStatusState.StateFirstFix.ttffMillis] will always be
     * null.
     *
     * @see [LocationManager.registerGnssStatusCallback]
     * @see [GnssStatusState]
     * @see [GnssStatusState.StateFirstFix]
     */
    @RequiresPermission(ACCESS_FINE_LOCATION)
    @JvmStatic
    fun observeGnssStatusOnFirstFix(handler: Handler? = null): Observable<GnssStatusState.StateFirstFix> =
        observeGnssStatus(handler)
            .filter { it is GnssStatusState.StateFirstFix }
            .map { it as GnssStatusState.StateFirstFix }

    /**
     * @see [LocationManager.registerGnssStatusCallback]
     * @see [GnssStatusState]
     * @see [GnssStatusState.StateStarted]
     */
    @RequiresPermission(ACCESS_FINE_LOCATION)
    @JvmStatic
    fun observeGnssStatusOnStarted(handler: Handler? = null): Observable<Unit> =
        observeGnssStatus(handler)
            .filter { t -> t is GnssStatusState.StateStarted }
            .map { }

    /**
     * @see [LocationManager.registerGnssStatusCallback]
     * @see [GnssStatusState]
     * @see [GnssStatusState.StateStopped]
     */
    @RequiresPermission(ACCESS_FINE_LOCATION)
    @JvmStatic
    fun observeGnssStatusOnStopped(handler: Handler? = null): Observable<Unit> =
        observeGnssStatus(handler)
            .filter { t -> t is GnssStatusState.StateStopped }
            .map { }

    /**
     * @see [LocationManager.requestLocationUpdates] (String, Long, Float)
     * @see [LocationUpdatesState]
     */
    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    @JvmStatic
    fun observeLocationUpdatesState(
        provider: Provider,
        minTime: Long,
        minDistance: Float,
        looper: Looper? = null
    ): Observable<LocationUpdatesState> =
        LocationUpdatesObservable(locationManager, provider, minTime, minDistance, looper)

    /**
     * You  should consider using [LocationServices](https://developers.google.com/android/reference/com/google/android/gms/location/LocationServices)
     * to fetch a [Location].
     *
     * @see [LocationManager.requestLocationUpdates] (Criteria, Long, Float)
     * @see [LocationUpdatesState]
     */
    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    @JvmStatic
    fun observeLocationUpdatesState(
        criteria: Criteria,
        minTime: Long,
        minDistance: Float,
        looper: Looper? = null
    ): Observable<LocationUpdatesState> =
        LocationUpdatesObservable(locationManager, criteria, minTime, minDistance, looper)

    /**
     * You  should consider using [LocationServices](https://developers.google.com/android/reference/com/google/android/gms/location/LocationServices)
     * to fetch a [Location].
     *
     * @see [LocationManager.requestLocationUpdates] (String, Long, Float)
     * @see [LocationUpdatesState.StateLocationChanged]
     */
    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    @JvmStatic
    fun observeLocationChanged(
        provider: Provider,
        minTime: Long,
        minDistance: Float,
        looper: Looper? = null
    ): Observable<Location> =
        observeLocationUpdatesState(
            provider,
            minTime,
            minDistance,
            looper
        ).filter { t -> t is LocationUpdatesState.StateLocationChanged }
            .map { t -> (t as LocationUpdatesState.StateLocationChanged).location }

    /**
     * You  should consider using [LocationServices](https://developers.google.com/android/reference/com/google/android/gms/location/LocationServices)
     * to fetch a [Location].
     *
     * @see [LocationManager.requestLocationUpdates] (Criteria, Long, Float)
     * @see [LocationUpdatesState.StateLocationChanged]
     */
    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    @JvmStatic
    fun observeLocationChanged(
        criteria: Criteria,
        minTime: Long,
        minDistance: Float,
        looper: Looper? = null
    ): Observable<Location> =
        observeLocationUpdatesState(
            criteria,
            minTime,
            minDistance,
            looper
        ).filter { t -> t is LocationUpdatesState.StateLocationChanged }
            .map { t -> (t as LocationUpdatesState.StateLocationChanged).location }

    /**
     * @see [LocationManager.requestLocationUpdates] (String, Long, Float)
     * @see [LocationUpdatesState.StateStatusChanged]
     */
    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    @JvmStatic
    fun observeLocationStatusChanged(
        provider: Provider,
        minTime: Long,
        minDistance: Float,
        looper: Looper? = null
    ): Observable<LocationUpdatesState.StateStatusChanged> =
        observeLocationUpdatesState(
            provider,
            minTime,
            minDistance,
            looper
        ).filter { t -> t is LocationUpdatesState.StateStatusChanged }
            .map { t -> (t as LocationUpdatesState.StateStatusChanged) }

    /**
     * @see [LocationManager.requestLocationUpdates] (Criteria, Long, Float)
     * @see [LocationUpdatesState.StateStatusChanged]
     */
    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    @JvmStatic
    fun observeLocationStatusChanged(
        criteria: Criteria,
        minTime: Long,
        minDistance: Float,
        looper: Looper? = null
    ): Observable<LocationUpdatesState.StateStatusChanged> = observeLocationUpdatesState(
        criteria,
        minTime,
        minDistance,
        looper
    ).filter { t -> t is LocationUpdatesState.StateStatusChanged }
        .map { t -> (t as LocationUpdatesState.StateStatusChanged) }

    /**
     * @see [LocationManager.requestLocationUpdates] (String, Long, Float)
     * @see [LocationUpdatesState.StateProviderEnabled]
     */
    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    @JvmStatic
    fun observeLocationProviderEnabled(
        provider: Provider,
        minTime: Long,
        minDistance: Float,
        looper: Looper? = null
    ): Observable<LocationUpdatesState.StateProviderEnabled> =
        observeLocationUpdatesState(
            provider,
            minTime,
            minDistance,
            looper
        ).filter { t -> t is LocationUpdatesState.StateProviderEnabled }
            .map { t -> (t as LocationUpdatesState.StateProviderEnabled) }

    /**
     * @see [LocationManager.requestLocationUpdates] (Criteria, Long, Float)
     * @see [LocationUpdatesState.StateProviderEnabled]
     */
    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    @JvmStatic
    fun observeLocationProviderEnabled(
        criteria: Criteria,
        minTime: Long,
        minDistance: Float,
        looper: Looper? = null
    ): Observable<LocationUpdatesState.StateProviderEnabled> =
        observeLocationUpdatesState(
            criteria,
            minTime,
            minDistance,
            looper
        ).filter { t -> t is LocationUpdatesState.StateProviderEnabled }
            .map { t -> (t as LocationUpdatesState.StateProviderEnabled) }

    /**
     * @see [LocationManager.requestLocationUpdates] (String, Long, Float)
     * @see [LocationUpdatesState.StateProviderDisabled]
     */
    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    @JvmStatic
    fun observeLocationProviderDisabled(
        provider: Provider,
        minTime: Long,
        minDistance: Float,
        looper: Looper? = null
    ): Observable<LocationUpdatesState.StateProviderDisabled> =
        observeLocationUpdatesState(
            provider,
            minTime,
            minDistance,
            looper
        ).filter { t -> t is LocationUpdatesState.StateProviderDisabled }
            .map { t -> (t as LocationUpdatesState.StateProviderDisabled) }

    /**
     * @see [LocationManager.requestLocationUpdates] (Criteria, Long, Float)
     * @see [LocationUpdatesState.StateProviderDisabled]
     */
    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    @JvmStatic
    fun observeLocationProviderDisabled(
        criteria: Criteria,
        minTime: Long,
        minDistance: Float,
        looper: Looper? = null
    ): Observable<LocationUpdatesState.StateProviderDisabled> =
        observeLocationUpdatesState(
            criteria,
            minTime,
            minDistance,
            looper
        ).filter { t -> t is LocationUpdatesState.StateProviderDisabled }
            .map { t -> (t as LocationUpdatesState.StateProviderDisabled) }

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

    @VisibleForTesting
    internal class NmeaObservable(
        private val locationManager: LocationManager?,
        private val handler: Handler? = null
    ) : Observable<NmeaEvent>() {
        override fun subscribeActual(observer: Observer<in NmeaEvent>) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val listener = NListener(observer, locationManager)
                observer.onSubscribe(listener)
                locationManager?.addNmeaListener(listener, handler)
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
    internal class GnssMeasurementsObservable(
        private val locationManager: LocationManager?,
        private val handler: Handler? = null
    ) : Observable<GnssMeasurementsState>() {
        override fun subscribeActual(observer: Observer<in GnssMeasurementsState>) {
            val listener = Listener(observer, locationManager)
            observer.onSubscribe(listener)
            locationManager?.registerGnssMeasurementsCallback(listener.callback, handler)
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
    internal class GnssNavigationMessageObservable(
        private val locationManager: LocationManager?,
        private val handler: Handler? = null
    ) : Observable<GnssNavigationMessageState>() {
        override fun subscribeActual(observer: Observer<in GnssNavigationMessageState>) {
            val listener = Listener(observer, locationManager)
            observer.onSubscribe(listener)
            locationManager?.registerGnssNavigationMessageCallback(listener.callback, handler)
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
    internal class GnssStatusObservable(
        private val locationManager: LocationManager?,
        private val handler: Handler? = null
    ) : Observable<GnssStatusState>() {
        override fun subscribeActual(observer: Observer<in GnssStatusState>) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val listener = NListener(observer, locationManager)
                observer.onSubscribe(listener)
                locationManager?.registerGnssStatusCallback(listener.callback, handler)
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
        private val criteria: Criteria? = null,
        private val looper: Looper? = null
    ) : Observable<LocationUpdatesState>() {
        constructor(
            locationManager: LocationManager?,
            criteria: Criteria,
            minTime: Long,
            minDistance: Float,
            looper: Looper? = null
        ) : this(locationManager, minTime, minDistance, criteria = criteria, looper = looper)

        constructor(
            locationManager: LocationManager?,
            provider: Provider,
            minTime: Long,
            minDistance: Float,
            looper: Looper? = null
        ) : this(locationManager, minTime, minDistance, provider = provider, looper = looper)

        override fun subscribeActual(observer: Observer<in LocationUpdatesState>) {
            val listener = Listener(observer, locationManager)
            observer.onSubscribe(listener)

            provider?.let {
                locationManager?.requestLocationUpdates(
                    it.name.toLowerCase(Locale.getDefault()),
                    minTime,
                    minDistance,
                    listener,
                    looper
                )
            } ?: criteria?.let {
                locationManager?.requestLocationUpdates(
                    minTime,
                    minDistance,
                    it,
                    listener,
                    looper
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
                        Provider.valueOf(provider.toUpperCase(Locale.getDefault())),
                        status,
                        extras
                    )
                )
            }

            override fun onProviderEnabled(provider: String) {
                observer.onNext(
                    LocationUpdatesState.StateProviderEnabled(
                        Provider.valueOf(
                            provider.toUpperCase(
                                Locale.getDefault()
                            )
                        )
                    )
                )
            }

            override fun onProviderDisabled(provider: String) {
                observer.onNext(
                    LocationUpdatesState.StateProviderDisabled(
                        Provider.valueOf(
                            provider.toUpperCase(
                                Locale.getDefault()
                            )
                        )
                    )
                )
            }
        }
    }
}
