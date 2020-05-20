package net.samystudio.rxlocationmanager.nmea

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class RMCNavigationalTest {
    @Test
    fun validate() {
        val rmc =
            RMCNavigational("\$GNRMC,191613.43,A,3342.6618,N,11751.3858,W,5.0,10.2,100520,9.3,W,A,S*6E")

        assertEquals("GNRMC", rmc.type)
        assertEquals("191613.43", rmc.time)
        assertEquals(RMCTiming.Status.A, rmc.status)
        assertEquals(33.71103, rmc.latitude!!, PRECISION)
        assertEquals(-117.85643, rmc.longitude!!, PRECISION)
        assertEquals(5.0, rmc.speed!!, PRECISION)
        assertEquals(10.2, rmc.angle!!, PRECISION)
        assertEquals("100520", rmc.date)
        assertEquals(9.3, rmc.magneticVariation!!, PRECISION)
        assertEquals(LocationDirection.W, rmc.magneticVariationDirection)
        assertEquals(RMCTiming.Mode.A, rmc.mode)
        assertEquals(RMCNavigational.NavigationalStatus.S, rmc.navigationalStatus)
        assertEquals("6E", rmc.checksum)
    }

    @Test
    fun validateAlternateConstructor() {
        val rmc = RMCNavigational(
            "GN",
            "191613.43",
            RMCTiming.Status.A,
            33.71103,
            -117.85643,
            5.0,
            10.2,
            "100520",
            9.3,
            LocationDirection.W,
            RMCTiming.Mode.A,
            RMCNavigational.NavigationalStatus.S
        )

        assertEquals(
            "\$GNRMC,191613.43,A,3342.6618,N,11751.3858,W,5.0,10.2,100520,9.3,W,A,S*6E",
            rmc.message
        )
    }

    @Test
    fun validateEmpty() {
        val rmc = RMCNavigational("\$GPRMC,,V,,,,,,,,,,N,V*29")

        assertEquals("GPRMC", rmc.type)
        assertEquals("", rmc.time)
        assertEquals(RMCTiming.Status.V, rmc.status)
        assertEquals(null, rmc.latitude)
        assertEquals(null, rmc.longitude)
        assertEquals(null, rmc.speed)
        assertEquals(null, rmc.angle)
        assertEquals("", rmc.date)
        assertEquals(null, rmc.magneticVariation)
        assertEquals(null, rmc.magneticVariationDirection)
        assertEquals(RMCTiming.Mode.N, rmc.mode)
        assertEquals(RMCNavigational.NavigationalStatus.V, rmc.navigationalStatus)
        assertEquals("29", rmc.checksum)
    }

    companion object {
        private const val PRECISION = 0.0000001
    }
}