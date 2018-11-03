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
        val observer = TestObserver<RxLocationManager.NmeaEvent>()
        val listener = RxLocationManager.NmeaObservable.Listener(observer, null)
        listener.onNmeaReceived(timestamp, "data")
        assertEquals("data", observer.values()[0].message)
        observer.onComplete()
    }

    @Test
    fun observeGnssMeasurements() {
        val observer = TestObserver<RxLocationManager.GnssMeasurementsState>()
        val listener = RxLocationManager.GnssMeasurementsObservable.Listener(observer, null)
        listener.callback.onGnssMeasurementsReceived(gnssMeasurementsEvent)
        assertEquals(
            gnssMeasurementsEvent,
            (observer.values()[0] as RxLocationManager.GnssMeasurementsState.StateEvent).event
        )
        listener.callback.onStatusChanged(10)
        assertEquals(
            10,
            (observer.values()[1] as RxLocationManager.GnssMeasurementsState.StateStatus).status
        )
        observer.onComplete()
    }

    @Test
    fun observeGnssNavigationMessage() {
        val observer = TestObserver<RxLocationManager.GnssNavigationMessageState>()
        val listener = RxLocationManager.GnssNavigationMessageObservable.Listener(observer, null)
        listener.callback.onGnssNavigationMessageReceived(gnssNavigationMessage)
        assertEquals(
            gnssNavigationMessage,
            (observer.values()[0] as RxLocationManager.GnssNavigationMessageState.StateEvent).event
        )
        listener.callback.onStatusChanged(10)
        assertEquals(
            10,
            (observer.values()[1] as RxLocationManager.GnssNavigationMessageState.StateStatus).status
        )
        observer.onComplete()
    }

    @Test
    fun observeGnssStatus() {
        val observer = TestObserver<RxLocationManager.GnssStatusState>()
        val listener = RxLocationManager.GnssStatusObservable.Listener(observer, null)
        listener.onGpsStatusChanged(GpsStatus.GPS_EVENT_FIRST_FIX)
        observer.assertValueAt(0, RxLocationManager.GnssStatusState.StateFirstFix(null))
        listener.onGpsStatusChanged(GpsStatus.GPS_EVENT_SATELLITE_STATUS)
        observer.assertValueAt(1, RxLocationManager.GnssStatusState.StateChanged(null))
        listener.onGpsStatusChanged(GpsStatus.GPS_EVENT_STARTED)
        observer.assertValueAt(2, RxLocationManager.GnssStatusState.StateStarted)
        listener.onGpsStatusChanged(GpsStatus.GPS_EVENT_STOPPED)
        observer.assertValueAt(3, RxLocationManager.GnssStatusState.StateStopped)
        observer.onComplete()
    }

    @Test
    fun observeGnssStatusN() {
        val observer = TestObserver<RxLocationManager.GnssStatusState>()
        val listener = RxLocationManager.GnssStatusObservable.NListener(observer, null)
        listener.callback.onFirstFix(10)
        assertEquals(
            10,
            (observer.values()[0] as RxLocationManager.GnssStatusState.StateFirstFix).ttffMillis
        )
        listener.callback.onSatelliteStatusChanged(gnssStatus)
        assertEquals(
            gnssStatus,
            (observer.values()[1] as RxLocationManager.GnssStatusState.StateChanged).status
        )
        listener.callback.onStarted()
        observer.assertValueAt(2, RxLocationManager.GnssStatusState.StateStarted)
        listener.callback.onStopped()
        observer.assertValueAt(3, RxLocationManager.GnssStatusState.StateStopped)
        observer.onComplete()
    }

    @Test
    fun observeLocationUpdates() {
        val observer = TestObserver<RxLocationManager.LocationUpdatesState>()
        val listener = RxLocationManager.LocationUpdatesObservable.Listener(observer, null)
        listener.onLocationChanged(location)
        assertEquals(
            location,
            (observer.values()[0] as RxLocationManager.LocationUpdatesState.StateLocationChanged).location
        )
        listener.onProviderDisabled("providerX")
        assertEquals(
            "providerX",
            (observer.values()[1] as RxLocationManager.LocationUpdatesState.StateProviderDisabled).provider
        )
        listener.onProviderEnabled("providerY")
        assertEquals(
            "providerY",
            (observer.values()[2] as RxLocationManager.LocationUpdatesState.StateProviderEnabled).provider
        )
        listener.onStatusChanged("providerZ", 10, bundle)
        assertEquals(
            "providerZ",
            (observer.values()[3] as RxLocationManager.LocationUpdatesState.StateStatusChanged).provider
        )
        assertEquals(
            10,
            (observer.values()[3] as RxLocationManager.LocationUpdatesState.StateStatusChanged).status
        )
        assertEquals(
            bundle,
            (observer.values()[3] as RxLocationManager.LocationUpdatesState.StateStatusChanged).extras
        )
        observer.onComplete()
    }
}