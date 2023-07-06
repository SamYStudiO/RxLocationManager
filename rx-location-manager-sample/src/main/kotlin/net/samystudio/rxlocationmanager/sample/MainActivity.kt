package net.samystudio.rxlocationmanager.sample

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.location.LocationRequestCompat
import com.tbruyelle.rxpermissions3.RxPermissions
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import net.samystudio.rxlocationmanager.GnssStatusState
import net.samystudio.rxlocationmanager.LocationUpdatesState
import net.samystudio.rxlocationmanager.Provider
import net.samystudio.rxlocationmanager.RxLocationManager

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
                },
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
                        Log.d("GnssMeasurementsEvent", it.toString())
                    },
            )

            // GnssNavigationMessage ---------------------------------------------------------------
            compositeDisposable.add(
                rxPermissions
                    .request(Manifest.permission.ACCESS_FINE_LOCATION)
                    .filter { it }
                    .flatMap { RxLocationManager.observeGnssNavigationMessage() }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        Log.d("GnssNavMesEvent", it.toString())
                    },
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
                            it.toString(),
                        )
                        is GnssStatusState.StateStopped -> Log.d(
                            "GnssStatusStopped1",
                            it.toString(),
                        )
                        is GnssStatusState.StateFirstFix -> Log.d(
                            "GnssStatusFirstFix1",
                            it.toString(),
                        )
                        is GnssStatusState.StateChanged -> Log.d(
                            "GnssStatusChanged1",
                            it.toString(),
                        )
                    }
                },
        )

        compositeDisposable.add(
            rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .filter { it }
                .flatMap { RxLocationManager.observeGnssStatusOnStarted() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Log.d("GnssStatusStarted2", it.toString())
                },
        )

        compositeDisposable.add(
            rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .filter { it }
                .flatMap { RxLocationManager.observeGnssStatusOnStopped() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Log.d("GnssStatusStopped2", it.toString())
                },
        )

        compositeDisposable.add(
            rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .filter { it }
                .flatMap { RxLocationManager.observeGnssStatusOnFirstFix() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Log.d("GnssStatusFirstFix2", it.toString())
                },
        )

        compositeDisposable.add(
            rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .filter { it }
                .flatMap { RxLocationManager.observeGnssStatusOnChanged() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Log.d("GnssStatusChanged2", it.toString())
                },
        )

        // GnssNavigationMessage -------------------------------------------------------------------
        compositeDisposable.add(
            rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .filter { it }
                .flatMap {
                    RxLocationManager.observeLocationUpdatesState(
                        Provider.GPS,
                        LocationRequestCompat.Builder(1000)
                            .setMinUpdateDistanceMeters(10f)
                            .build(),
                    )
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    when (it) {
                        is LocationUpdatesState.StateLocationChanged -> Log.d(
                            "LocUpdLocationChanged",
                            it.toString(),
                        )
                        is LocationUpdatesState.StateProviderEnabled -> Log.d(
                            "LocUpdProviderEnabled",
                            it.toString(),
                        )
                        is LocationUpdatesState.StateProviderDisabled -> Log.d(
                            "LocUpdProviderDisabled",
                            it.toString(),
                        )
                    }
                },
        )

        compositeDisposable.add(
            rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .filter { it }
                .flatMap {
                    RxLocationManager.observeLocationChanged(
                        Provider.GPS,
                        LocationRequestCompat.Builder(1000)
                            .setMinUpdateDistanceMeters(10f)
                            .build(),
                    )
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Log.d("LocUpdLocationChanged2", it.toString())
                },
        )

        compositeDisposable.add(
            rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .filter { it }
                .flatMap {
                    RxLocationManager.observeLocationProviderEnabled(
                        Provider.GPS,
                        LocationRequestCompat.Builder(1000)
                            .setMinUpdateDistanceMeters(10f)
                            .build(),
                    )
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Log.d("LocUpdProviderEnabled2", it.toString())
                },
        )

        compositeDisposable.add(
            rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .filter { it }
                .flatMap {
                    RxLocationManager.observeLocationProviderDisabled(
                        Provider.GPS,
                        LocationRequestCompat.Builder(1000)
                            .setMinUpdateDistanceMeters(10f)
                            .build(),
                    )
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Log.d("LocUpdProviderDisabled2", it.toString())
                },
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        compositeDisposable.dispose()
    }
}
