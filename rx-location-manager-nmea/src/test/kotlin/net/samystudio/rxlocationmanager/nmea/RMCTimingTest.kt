package net.samystudio.rxlocationmanager.nmea

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class RMCTimingTest {
    @Test
    fun validate() {
        val rmc =
            RMCTiming("\$GNRMC,191613.43,A,3342.6618,N,11751.3858,W,5.0,10.2,100520,9.3,W,A*11")

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
        assertEquals("11", rmc.checksum)
    }

    @Test
    fun validateAlternateConstructor() {
        val rmc = RMCTiming(
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
            RMCTiming.Mode.A
        )

        assertEquals(
            "\$GNRMC,191613.43,A,3342.6618,N,11751.3858,W,5.0,10.2,100520,9.3,W,A*11",
            rmc.message
        )
    }

    @Test
    fun validateEmpty() {
        val rmc = RMCTiming("\$GPRMC,,V,,,,,,,,,,N*53")

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
        assertEquals("53", rmc.checksum)
    }

    companion object {
        private const val PRECISION = 0.0000001
    }
}