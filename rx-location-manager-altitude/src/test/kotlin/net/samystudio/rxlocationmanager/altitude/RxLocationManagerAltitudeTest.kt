package net.samystudio.rxlocationmanager.altitude

import android.hardware.SensorEvent
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.reactivex.rxjava3.observers.TestObserver
import org.junit.Before
import org.junit.Test

class RxLocationManagerAltitudeTest {
    @MockK
    lateinit var sensorEvent: SensorEvent

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun observeBarometricAltitudeUpdates() {
        val observer = TestObserver<Float>()
        val barometricListener =
            RxLocationManagerAltitude.BarometricSensorObservable.Listener(observer, null)

        try {
            val valuesField = SensorEvent::class.java.getField("values")
            valuesField.isAccessible = true
            val sensorValue = floatArrayOf(10.0f)
            try {
                valuesField.set(sensorEvent, sensorValue)
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }

        barometricListener.onSensorChanged(sensorEvent)
        observer.assertValueAt(0, 10.0f)
        observer.onComplete()
    }
}
