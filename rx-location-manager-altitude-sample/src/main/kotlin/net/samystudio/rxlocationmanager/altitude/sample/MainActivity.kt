package net.samystudio.rxlocationmanager.altitude.sample

import android.Manifest
import android.annotation.SuppressLint
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.tbruyelle.rxpermissions3.RxPermissions
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import net.samystudio.rxlocationmanager.altitude.GoogleElevationApi
import net.samystudio.rxlocationmanager.altitude.RxLocationManagerAltitude

class MainActivity : AppCompatActivity() {
    private val compositeDisposable = CompositeDisposable()

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rxPermissions = RxPermissions(this)

        compositeDisposable.add(
            rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .filter { it }
                .flatMap { RxLocationManagerAltitude.observeGpsEllipsoidalAltitudeUpdates() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { Log.d("GpsEllipsoidalAltitude", it.toString()) },
        )

        compositeDisposable.add(
            rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .filter { it }
                .flatMap { RxLocationManagerAltitude.observeGpsGeoidalAltitudeUpdates() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { Log.d("GpsGeoidalAltitude", it.toString()) },
        )

        compositeDisposable.add(
            RxLocationManagerAltitude
                .observeBarometricAltitudeUpdates(
                    1000,
                    SensorManager.PRESSURE_STANDARD_ATMOSPHERE,
                ).observeOn(AndroidSchedulers.mainThread())
                .subscribe { Log.d("BarometricAltitude", it.toString()) },
        )

        compositeDisposable.add(
            RxLocationManagerAltitude
                .getRemoteServiceAltitude(
                    GoogleElevationApi("your_google_api_key"),
                    48.866667,
                    2.333333,
                ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Log.d(
                            "RemoteServiceAltitude",
                            it.toString(),
                        )
                    },
                    { Log.d("RemoteServiceAltitude", "Error getting remote altitude") },
                ),
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        compositeDisposable.dispose()
    }
}
