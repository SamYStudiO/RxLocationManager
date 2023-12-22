@file:Suppress("MissingPermission", "DEPRECATION", "unused")

package net.samystudio.rxlocationmanager

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.TargetApi
import android.location.GnssMeasurementsEvent
import android.location.GnssNavigationMessage
import android.location.GpsStatus
import android.location.Location
import android.location.LocationManager
import android.location.OnNmeaMessageListener
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.annotation.VisibleForTesting
import androidx.core.content.getSystemService
import androidx.core.location.GnssStatusCompat
import androidx.core.location.LocationListenerCompat
import androidx.core.location.LocationManagerCompat
import androidx.core.location.LocationRequestCompat
import androidx.core.os.CancellationSignal
import androidx.core.os.ExecutorCompat
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
    val locationManager: LocationManager by lazy {
        ContextProvider.applicationContext.getSystemService()!!
    }

    /**
     * @see [LocationManager.getCurrentLocation]
     */
    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    @JvmStatic
    fun getCurrentLocation(
        provider: Provider,
        cancellationSignal: CancellationSignal? = null,
        looper: Looper = Looper.myLooper() ?: Looper.getMainLooper(),
    ): Maybe<Location> = Maybe.create { emitter ->
        cancellationSignal?.setOnCancelListener {
            emitter.onComplete()
        }

        try {
            LocationManagerCompat.getCurrentLocation(
                locationManager,
                provider.name.lowercase(),
                cancellationSignal,
                ExecutorCompat.create(Handler(looper)),
            ) {
                when {
                    cancellationSignal?.isCanceled == true -> emitter.onComplete()
                    it == null -> emitter.onComplete()
                    else -> emitter.onSuccess(it)
                }
            }
        } catch (e: Exception) {
            emitter.tryOnError(e)
        }
    }

    /**
     * @see [LocationManager.addNmeaListener]
     */
    @RequiresPermission(ACCESS_FINE_LOCATION)
    @JvmStatic
    fun observeNmea(
        looper: Looper = Looper.myLooper() ?: Looper.getMainLooper(),
    ): Observable<NmeaEvent> =
        NmeaObservable(locationManager, looper)

    /**
     * @see [LocationManager.registerGnssMeasurementsCallback]
     * @see [GnssMeasurementsEvent]
     * @see [GnssMeasurementsEvent.Callback.onGnssMeasurementsReceived]
     */
    @RequiresPermission(ACCESS_FINE_LOCATION)
    @RequiresApi(Build.VERSION_CODES.N)
    @JvmStatic
    fun observeGnssMeasurements(
        looper: Looper = Looper.myLooper() ?: Looper.getMainLooper(),
    ): Observable<GnssMeasurementsEvent> =
        GnssMeasurementsObservable(locationManager, looper)

    /**
     * @see [LocationManager.registerGnssNavigationMessageCallback]
     * @see [GnssNavigationMessage]
     * @see [GnssNavigationMessage.Callback.onGnssNavigationMessageReceived]
     */
    @RequiresPermission(ACCESS_FINE_LOCATION)
    @RequiresApi(Build.VERSION_CODES.N)
    @JvmStatic
    fun observeGnssNavigationMessage(
        looper: Looper = Looper.myLooper() ?: Looper.getMainLooper(),
    ): Observable<GnssNavigationMessage> =
        GnssNavigationMessageObservable(locationManager, looper)

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
    fun observeGnssStatus(
        looper: Looper = Looper.myLooper() ?: Looper.getMainLooper(),
    ): Observable<GnssStatusState> =
        GnssStatusObservable(locationManager, looper)

    /**
     * Prior to [Build.VERSION_CODES.N] [GnssStatusState.StateChanged.status] will always be null.
     *
     * @see [LocationManager.registerGnssStatusCallback]
     * @see [GnssStatusState]
     * @see [GnssStatusState.StateChanged]
     */
    @RequiresPermission(ACCESS_FINE_LOCATION)
    @JvmStatic
    fun observeGnssStatusOnChanged(
        looper: Looper = Looper.myLooper() ?: Looper.getMainLooper(),
    ): Observable<GnssStatusState.StateChanged> =
        observeGnssStatus(looper)
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
    fun observeGnssStatusOnFirstFix(
        looper: Looper = Looper.myLooper() ?: Looper.getMainLooper(),
    ): Observable<GnssStatusState.StateFirstFix> =
        observeGnssStatus(looper)
            .filter { it is GnssStatusState.StateFirstFix }
            .map { it as GnssStatusState.StateFirstFix }

    /**
     * @see [LocationManager.registerGnssStatusCallback]
     * @see [GnssStatusState]
     * @see [GnssStatusState.StateStarted]
     */
    @RequiresPermission(ACCESS_FINE_LOCATION)
    @JvmStatic
    fun observeGnssStatusOnStarted(
        looper: Looper = Looper.myLooper() ?: Looper.getMainLooper(),
    ): Observable<Unit> =
        observeGnssStatus(looper)
            .filter { t -> t is GnssStatusState.StateStarted }
            .map { }

    /**
     * @see [LocationManager.registerGnssStatusCallback]
     * @see [GnssStatusState]
     * @see [GnssStatusState.StateStopped]
     */
    @RequiresPermission(ACCESS_FINE_LOCATION)
    @JvmStatic
    fun observeGnssStatusOnStopped(
        looper: Looper = Looper.myLooper() ?: Looper.getMainLooper(),
    ): Observable<Unit> =
        observeGnssStatus(looper)
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
        locationRequestCompat: LocationRequestCompat,
        looper: Looper = Looper.myLooper() ?: Looper.getMainLooper(),
    ): Observable<LocationUpdatesState> =
        LocationUpdatesObservable(
            locationManager,
            provider,
            locationRequestCompat,
            looper,
        )

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
        locationRequestCompat: LocationRequestCompat,
        looper: Looper = Looper.myLooper() ?: Looper.getMainLooper(),
    ): Observable<Location> =
        observeLocationUpdatesState(
            provider,
            locationRequestCompat,
            looper,
        ).filter { t -> t is LocationUpdatesState.StateLocationChanged }
            .map { t -> (t as LocationUpdatesState.StateLocationChanged).location }

    /**
     * @see [LocationManager.requestLocationUpdates] (String, Long, Float)
     * @see [LocationUpdatesState.StateProviderEnabled]
     */
    @RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
    @JvmStatic
    fun observeLocationProviderEnabled(
        provider: Provider,
        locationRequestCompat: LocationRequestCompat,
        looper: Looper = Looper.myLooper() ?: Looper.getMainLooper(),
    ): Observable<LocationUpdatesState.StateProviderEnabled> =
        observeLocationUpdatesState(
            provider,
            locationRequestCompat,
            looper,
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
        locationRequestCompat: LocationRequestCompat,
        looper: Looper = Looper.myLooper() ?: Looper.getMainLooper(),
    ): Observable<LocationUpdatesState.StateProviderDisabled> =
        observeLocationUpdatesState(
            provider,
            locationRequestCompat,
            looper,
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
        private val looper: Looper,
    ) : Observable<NmeaEvent>() {
        override fun subscribeActual(observer: Observer<in NmeaEvent>) {
            synchronized(this) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    val listener = NListener(observer, locationManager)
                    observer.onSubscribe(listener)
                    locationManager?.addNmeaListener(listener, Handler(looper))
                } else {
                    val listener = Listener(observer, locationManager)
                    observer.onSubscribe(listener)
                    Handler(looper).post {
                        locationManager?.addNmeaListener(listener)
                    }
                }
            }
        }

        class Listener(
            private val observer: Observer<in NmeaEvent>,
            private val locationManager: LocationManager?,
        ) : AtomicDisposable(), GpsStatus.NmeaListener {
            override fun onDispose() {
                locationManager?.removeNmeaListener(this)
            }

            @Deprecated("Deprecated in Java")
            override fun onNmeaReceived(timestamp: Long, nmea: String) {
                observer.onNext(NmeaEvent(nmea, timestamp))
            }
        }

        @TargetApi(Build.VERSION_CODES.N)
        class NListener(
            private val observer: Observer<in NmeaEvent>,
            private val locationManager: LocationManager?,
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
        private val looper: Looper,
    ) : Observable<GnssMeasurementsEvent>() {
        override fun subscribeActual(observer: Observer<in GnssMeasurementsEvent>) {
            val listener = Listener(observer, locationManager)
            observer.onSubscribe(listener)
            locationManager?.registerGnssMeasurementsCallback(listener.callback, Handler(looper))
        }

        class Listener(
            private val observer: Observer<in GnssMeasurementsEvent>,
            private val locationManager: LocationManager?,
        ) : AtomicDisposable() {
            val callback = object : GnssMeasurementsEvent.Callback() {
                override fun onGnssMeasurementsReceived(eventArgs: GnssMeasurementsEvent) {
                    observer.onNext(eventArgs)
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
        private val looper: Looper,
    ) : Observable<GnssNavigationMessage>() {
        override fun subscribeActual(observer: Observer<in GnssNavigationMessage>) {
            val listener = Listener(observer, locationManager)
            observer.onSubscribe(listener)
            locationManager?.registerGnssNavigationMessageCallback(
                listener.callback,
                Handler(looper),
            )
        }

        class Listener(
            private val observer: Observer<in GnssNavigationMessage>,
            private val locationManager: LocationManager?,
        ) : AtomicDisposable() {
            val callback = object : GnssNavigationMessage.Callback() {
                override fun onGnssNavigationMessageReceived(event: GnssNavigationMessage) {
                    observer.onNext(event)
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
        private val looper: Looper,
    ) : Observable<GnssStatusState>() {
        override fun subscribeActual(observer: Observer<in GnssStatusState>) {
            locationManager?.let {
                val listener = Listener(observer, locationManager)
                LocationManagerCompat.registerGnssStatusCallback(
                    it,
                    listener.callback,
                    Handler(looper),
                )
                observer.onSubscribe(listener)
            }
        }

        class Listener(
            private val observer: Observer<in GnssStatusState>,
            private val locationManager: LocationManager?,
        ) : AtomicDisposable() {
            val callback = object : GnssStatusCompat.Callback() {
                override fun onSatelliteStatusChanged(status: GnssStatusCompat) {
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
                locationManager?.let {
                    LocationManagerCompat.unregisterGnssStatusCallback(it, callback)
                }
            }
        }
    }

    @VisibleForTesting
    internal class LocationUpdatesObservable constructor(
        private val locationManager: LocationManager?,
        private val provider: Provider,
        private val locationRequestCompat: LocationRequestCompat,
        private val looper: Looper,
    ) : Observable<LocationUpdatesState>() {
        override fun subscribeActual(observer: Observer<in LocationUpdatesState>) {
            val listener = Listener(observer, locationManager)
            observer.onSubscribe(listener)

            try {
                locationManager?.let {
                    LocationManagerCompat.requestLocationUpdates(
                        it,
                        provider.name.lowercase(),
                        locationRequestCompat,
                        listener,
                        looper,
                    )
                }
            } catch (e: Exception) {
                observer.onError(e)
            }
        }

        class Listener(
            private val observer: Observer<in LocationUpdatesState>,
            private val locationManager: LocationManager?,
        ) : AtomicDisposable(), LocationListenerCompat {

            override fun onDispose() {
                locationManager?.removeUpdates(this)
            }

            override fun onLocationChanged(location: Location) {
                observer.onNext(LocationUpdatesState.StateLocationChanged(location))
            }

            override fun onProviderEnabled(provider: String) {
                observer.onNext(
                    LocationUpdatesState.StateProviderEnabled(
                        Provider.valueOf(
                            provider.toUpperCase(
                                Locale.getDefault(),
                            ),
                        ),
                    ),
                )
            }

            override fun onProviderDisabled(provider: String) {
                observer.onNext(
                    LocationUpdatesState.StateProviderDisabled(
                        Provider.valueOf(
                            provider.toUpperCase(
                                Locale.getDefault(),
                            ),
                        ),
                    ),
                )
            }
        }
    }
}
