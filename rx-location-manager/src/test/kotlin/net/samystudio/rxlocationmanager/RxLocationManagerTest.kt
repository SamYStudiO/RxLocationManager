@file:Suppress("DEPRECATION")

package net.samystudio.rxlocationmanager

import android.location.*
import android.os.Bundle
import io.reactivex.observers.TestObserver
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class RxLocationManagerTest {

    @Mock
    lateinit var gnssMeasurementsEvent: GnssMeasurementsEvent

    @Mock
    lateinit var gnssNavigationMessage: GnssNavigationMessage

    @Mock
    lateinit var gnssStatus: GnssStatus

    @Mock
    lateinit var location: Location

    @Mock
    lateinit var bundle: Bundle

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
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
        val observer = TestObserver<GnssMeasurementsState>()
        val listener = RxLocationManager.GnssMeasurementsObservable.Listener(observer, null)
        listener.callback.onGnssMeasurementsReceived(gnssMeasurementsEvent)
        assertEquals(
            gnssMeasurementsEvent,
            (observer.values()[0] as GnssMeasurementsState.StateEvent).event
        )
        listener.callback.onStatusChanged(10)
        assertEquals(
            10,
            (observer.values()[1] as GnssMeasurementsState.StateStatus).status
        )
        observer.onComplete()
    }

    @Test
    fun observeGnssNavigationMessage() {
        val observer = TestObserver<GnssNavigationMessageState>()
        val listener = RxLocationManager.GnssNavigationMessageObservable.Listener(observer, null)
        listener.callback.onGnssNavigationMessageReceived(gnssNavigationMessage)
        assertEquals(
            gnssNavigationMessage,
            (observer.values()[0] as GnssNavigationMessageState.StateEvent).event
        )
        listener.callback.onStatusChanged(10)
        assertEquals(
            10,
            (observer.values()[1] as GnssNavigationMessageState.StateStatus).status
        )
        observer.onComplete()
    }

    @Test
    fun observeGnssStatus() {
        val observer = TestObserver<GnssStatusState>()
        val listener = RxLocationManager.GnssStatusObservable.Listener(observer, null)
        listener.onGpsStatusChanged(GpsStatus.GPS_EVENT_FIRST_FIX)
        observer.assertValueAt(0, GnssStatusState.StateFirstFix(null))
        listener.onGpsStatusChanged(GpsStatus.GPS_EVENT_SATELLITE_STATUS)
        observer.assertValueAt(1, GnssStatusState.StateChanged(null))
        listener.onGpsStatusChanged(GpsStatus.GPS_EVENT_STARTED)
        observer.assertValueAt(2, GnssStatusState.StateStarted)
        listener.onGpsStatusChanged(GpsStatus.GPS_EVENT_STOPPED)
        observer.assertValueAt(3, GnssStatusState.StateStopped)
        observer.onComplete()
    }

    @Test
    fun observeGnssStatusN() {
        val observer = TestObserver<GnssStatusState>()
        val listener = RxLocationManager.GnssStatusObservable.NListener(observer, null)
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
        listener.onStatusChanged(providerName, 10, bundle)
        assertEquals(
            provider,
            (observer.values()[3] as LocationUpdatesState.StateStatusChanged).provider
        )
        assertEquals(
            10,
            (observer.values()[3] as LocationUpdatesState.StateStatusChanged).status
        )
        assertEquals(
            bundle,
            (observer.values()[3] as LocationUpdatesState.StateStatusChanged).extras
        )
        observer.onComplete()
    }
}