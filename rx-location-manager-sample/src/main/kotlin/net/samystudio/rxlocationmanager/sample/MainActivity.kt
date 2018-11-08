package net.samystudio.rxlocationmanager.sample

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import net.samystudio.rxlocationmanager.*

class MainActivity : AppCompatActivity() {
    private val compositeDisposable = CompositeDisposable()

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rxPermissions = RxPermissions(this)

        // Nmea ------------------------------------------------------------------------------------
        compositeDisposable.add(
            rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .filter { it }
                .flatMap { RxLocationManager.observeNmea() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Log.d("Nmea", it.message)
                }
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // GnssMeasurement ---------------------------------------------------------------------
            compositeDisposable.add(
                rxPermissions
                    .request(Manifest.permission.ACCESS_FINE_LOCATION)
                    .filter { it }
                    .flatMap { RxLocationManager.observeGnssMeasurements() }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        when (it) {
                            is GnssMeasurementsState.StateEvent -> Log.d(
                                "GnssMeasurementsEvent1",
                                it.event.toString()
                            )
                            is GnssMeasurementsState.StateStatus -> Log.d(
                                "GnssMeasurementsStatus1",
                                it.status.toString()
                            )
                        }
                    }
            )

            compositeDisposable.add(
                rxPermissions
                    .request(Manifest.permission.ACCESS_FINE_LOCATION)
                    .filter { it }
                    .flatMap { RxLocationManager.observeGnssMeasurementsEvent() }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        Log.d("GnssMeasurementsEvent2", it.toString())
                    }
            )

            compositeDisposable.add(
                rxPermissions
                    .request(Manifest.permission.ACCESS_FINE_LOCATION)
                    .filter { it }
                    .flatMap { RxLocationManager.observeGnssMeasurementsStatus() }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        Log.d("GnssMeasurementsStatus2", it.toString())
                    }
            )

            // GnssNavigationMessage ---------------------------------------------------------------
            compositeDisposable.add(
                rxPermissions
                    .request(Manifest.permission.ACCESS_FINE_LOCATION)
                    .filter { it }
                    .flatMap { RxLocationManager.observeGnssNavigationMessage() }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        when (it) {
                            is GnssNavigationMessageState.StateEvent -> Log.d(
                                "GnssNavMessageEvent1",
                                it.event.toString()
                            )
                            is GnssNavigationMessageState.StateStatus -> Log.d(
                                "GnssNavMessageStatus1",
                                it.status.toString()
                            )
                        }
                    }
            )

            compositeDisposable.add(
                rxPermissions
                    .request(Manifest.permission.ACCESS_FINE_LOCATION)
                    .filter { it }
                    .flatMap { RxLocationManager.observeGnssNavigationMessageEvent() }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        Log.d("GnssNavMesEvent2", it.toString())
                    }
            )

            compositeDisposable.add(
                rxPermissions
                    .request(Manifest.permission.ACCESS_FINE_LOCATION)
                    .filter { it }
                    .flatMap { RxLocationManager.observeGnssNavigationMessageStatus() }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        Log.d("GnssNavMesStatus2", it.toString())
                    }
            )
        }

        // GnssNavigationMessage -------------------------------------------------------------------
        compositeDisposable.add(
            rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .filter { it }
                .flatMap { RxLocationManager.observeGnssStatus() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    when (it) {
                        is GnssStatusState.StateStarted -> Log.d(
                            "GnssStatusStarted1",
                            it.toString()
                        )
                        is GnssStatusState.StateStopped -> Log.d(
                            "GnssStatusStopped1",
                            it.toString()
                        )
                        is GnssStatusState.StateFirstFix -> Log.d(
                            "GnssStatusFirstFix1",
                            it.toString()
                        )
                        is GnssStatusState.StateChanged -> Log.d(
                            "GnssStatusChanged1",
                            it.toString()
                        )
                    }
                }
        )

        compositeDisposable.add(
            rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .filter { it }
                .flatMap { RxLocationManager.observeGnssStatusOnStarted() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Log.d("GnssStatusStarted2", it.toString())
                }
        )

        compositeDisposable.add(
            rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .filter { it }
                .flatMap { RxLocationManager.observeGnssStatusOnStopped() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Log.d("GnssStatusStopped2", it.toString())
                }
        )

        compositeDisposable.add(
            rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .filter { it }
                .flatMap { RxLocationManager.observeGnssStatusOnFirstFix() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Log.d("GnssStatusFirstFix2", it.toString())
                }
        )

        compositeDisposable.add(
            rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .filter { it }
                .flatMap { RxLocationManager.observeGnssStatusChanged() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Log.d("GnssStatusChanged2", it.toString())
                }
        )

        // GnssNavigationMessage -------------------------------------------------------------------
        compositeDisposable.add(
            rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .filter { it }
                .flatMap { RxLocationManager.observeLocationUpdatesState(Provider.GPS, 1000, 10f) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    when (it) {
                        is LocationUpdatesState.StateLocationChanged -> Log.d(
                            "LocUpdLocationChanged1",
                            it.toString()
                        )
                        is LocationUpdatesState.StateStatusChanged -> Log.d(
                            "LocUpdStatusChanged1",
                            it.toString()
                        )
                        is LocationUpdatesState.StateProviderEnabled -> Log.d(
                            "LocUpdProviderEnabled1",
                            it.toString()
                        )
                        is LocationUpdatesState.StateProviderDisabled -> Log.d(
                            "LocUpdProviderDisabled1",
                            it.toString()
                        )
                    }
                }
        )

        compositeDisposable.add(
            rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .filter { it }
                .flatMap { RxLocationManager.observeLocationChanged(Provider.GPS, 1000, 10f) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Log.d("LocUpdLocationChanged2", it.toString())
                }
        )

        compositeDisposable.add(
            rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .filter { it }
                .flatMap { RxLocationManager.observeLocationStatusChanged(Provider.GPS, 1000, 10f) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Log.d("LocUpdStatusChanged2", it.toString())
                }
        )

        compositeDisposable.add(
            rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .filter { it }
                .flatMap {
                    RxLocationManager.observeLocationProviderEnabled(
                        Provider.GPS,
                        1000,
                        10f
                    )
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Log.d("LocUpdProviderEnabled2", it.toString())
                }
        )

        compositeDisposable.add(
            rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .filter { it }
                .flatMap {
                    RxLocationManager.observeLocationProviderDisabled(
                        Provider.GPS,
                        1000,
                        10f
                    )
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Log.d("LocUpdProviderDisabled2", it.toString())
                }
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        compositeDisposable.dispose()
    }
}
