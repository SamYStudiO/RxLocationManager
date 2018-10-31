package net.samystudio.rxlocationmanager.altitude

import android.hardware.SensorEvent
import io.reactivex.observers.TestObserver
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class RxLocationManagerAltitudeTest {

    @Mock
    lateinit var sensorEvent: SensorEvent

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun parseNmeaAltitude() {
        var nmea = "\$GPGGA,101010,10.10,N,10.10,E,2,3,10,250.5,M,50.5,M,40,10#,12456789"
        assertEquals(250.5, RxLocationManagerAltitude.parseNmeaAltitude(nmea, false))
        assertEquals(301.0, RxLocationManagerAltitude.parseNmeaAltitude(nmea, true))
        nmea = "\$AAAAA,101010,10.10,N,10.10,E,2,3,10,250.5,M,50.5,M,40,10#,12456789"
        assertEquals(null, RxLocationManagerAltitude.parseNmeaAltitude(nmea, false))
        assertEquals(null, RxLocationManagerAltitude.parseNmeaAltitude(nmea, true))
        nmea = "\$AAAAA,101010,10.10,N,10.10,E,2,3,10,AAAA,M,50.5,M,40,10#,12456789"
        assertEquals(null, RxLocationManagerAltitude.parseNmeaAltitude(nmea, false))
        assertEquals(null, RxLocationManagerAltitude.parseNmeaAltitude(nmea, true))
        nmea = ""
        assertEquals(null, RxLocationManagerAltitude.parseNmeaAltitude(nmea, false))
        assertEquals(null, RxLocationManagerAltitude.parseNmeaAltitude(nmea, true))
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