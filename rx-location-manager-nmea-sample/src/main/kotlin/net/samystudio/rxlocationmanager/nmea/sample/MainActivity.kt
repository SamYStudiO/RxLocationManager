package net.samystudio.rxlocationmanager.nmea.sample

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import net.samystudio.rxlocationmanager.RxLocationManager
import net.samystudio.rxlocationmanager.nmea.GGA
import net.samystudio.rxlocationmanager.nmea.NmeaException

class MainActivity : AppCompatActivity() {
    private val compositeDisposable = CompositeDisposable()

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        compositeDisposable.add(
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
                }
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        compositeDisposable.dispose()
    }
}
