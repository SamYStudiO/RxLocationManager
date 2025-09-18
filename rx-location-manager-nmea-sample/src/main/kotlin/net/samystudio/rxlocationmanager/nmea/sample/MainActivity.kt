package net.samystudio.rxlocationmanager.nmea.sample

//import com.tbruyelle.rxpermissions3.RxPermissions
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.disposables.CompositeDisposable

class MainActivity : AppCompatActivity() {
    private val compositeDisposable = CompositeDisposable()

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*compositeDisposable.add(
            RxPermissions(this)
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .filter { it }
                .flatMap { RxLocationManager.observeNmea() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    try {
                        val gga = GGA(it.message)
                        Log.d("observeNmea", gga.altitude.toString())
                    } catch (e: NmeaException) {
                        Log.w("observeNmea", e.message ?: "")
                    }
                },
        )*/
    }

    override fun onDestroy() {
        super.onDestroy()

        compositeDisposable.dispose()
    }
}
