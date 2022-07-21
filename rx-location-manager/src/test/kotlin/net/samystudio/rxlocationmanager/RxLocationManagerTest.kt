@file:Suppress("DEPRECATION")

package net.samystudio.rxlocationmanager

import android.location.GnssMeasurementsEvent
import android.location.GnssNavigationMessage
import android.location.Location
import androidx.core.location.GnssStatusCompat
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.reactivex.rxjava3.observers.TestObserver
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RxLocationManagerTest {
    @MockK
    lateinit var gnssMeasurementsEvent: GnssMeasurementsEvent

    @MockK
    lateinit var gnssNavigationMessage: GnssNavigationMessage

    @MockK
    lateinit var gnssStatus: GnssStatusCompat

    @MockK
    lateinit var location: Location

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun observeNmea() {
        val timestamp = System.currentTimeMillis()
        val observer = TestObserver<NmeaEvent>()
        val listener = RxLocationManager.NmeaObservable.Listener(observer, null)
        listener.onNmeaReceived(timestamp, "data")
        assertEquals("data", observer.values()[0].message)
        observer.onComplete()
    }

    @Test
    fun observeGnssMeasurements() {
        val observer = TestObserver<GnssMeasurementsEvent>()
        val listener = RxLocationManager.GnssMeasurementsObservable.Listener(observer, null)
        listener.callback.onGnssMeasurementsReceived(gnssMeasurementsEvent)
        assertEquals(
            gnssMeasurementsEvent,
            observer.values()[0] as GnssMeasurementsEvent
        )
        observer.onComplete()
    }

    @Test
    fun observeGnssNavigationMessage() {
        val observer = TestObserver<GnssNavigationMessage>()
        val listener = RxLocationManager.GnssNavigationMessageObservable.Listener(observer, null)
        listener.callback.onGnssNavigationMessageReceived(gnssNavigationMessage)
        assertEquals(
            gnssNavigationMessage,
            observer.values()[0] as GnssNavigationMessage
        )
        observer.onComplete()
    }

    @Test
    fun observeGnssStatus() {
        val observer = TestObserver<GnssStatusState>()
        val listener = RxLocationManager.GnssStatusObservable.Listener(observer, null)
        listener.callback.onFirstFix(10)
        assertEquals(
            10,
            (observer.values()[0] as GnssStatusState.StateFirstFix).ttffMillis
        )
        listener.callback.onSatelliteStatusChanged(gnssStatus)
        assertEquals(
            gnssStatus,
            (observer.values()[1] as GnssStatusState.StateChanged).status
        )
        listener.callback.onStarted()
        observer.assertValueAt(2, GnssStatusState.StateStarted)
        listener.callback.onStopped()
        observer.assertValueAt(3, GnssStatusState.StateStopped)
        observer.onComplete()
    }

    @Test
    fun observeLocationUpdates() {
        val observer = TestObserver<LocationUpdatesState>()
        val listener = RxLocationManager.LocationUpdatesObservable.Listener(observer, null)
        val provider = Provider.GPS
        val providerName = Provider.GPS.name
        listener.onLocationChanged(location)
        assertEquals(
            location,
            (observer.values()[0] as LocationUpdatesState.StateLocationChanged).location
        )
        listener.onProviderDisabled(providerName)
        assertEquals(
            provider,
            (observer.values()[1] as LocationUpdatesState.StateProviderDisabled).provider
        )
        listener.onProviderEnabled(providerName)
        assertEquals(
            provider,
            (observer.values()[2] as LocationUpdatesState.StateProviderEnabled).provider
        )
        observer.onComplete()
    }
}
