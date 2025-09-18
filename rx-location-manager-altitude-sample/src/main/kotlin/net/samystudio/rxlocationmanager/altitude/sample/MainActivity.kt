package net.samystudio.rxlocationmanager.altitude.sample

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.disposables.CompositeDisposable

class MainActivity : AppCompatActivity() {
    private val compositeDisposable = CompositeDisposable()

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*val rxPermissions = RxPermissions(this)

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
        )*/
    }

    override fun onDestroy() {
        super.onDestroy()

        compositeDisposable.dispose()
    }
}
