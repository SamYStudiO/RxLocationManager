package net.samystudio.rxlocationmanager.nmea

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class GGATest {
    @Test
    fun validate() {
        val gga =
            GGA("\$GPGGA,002153.000,3342.6618,N,11751.3858,W,1,10,1.2,27.0,M,-34.2,M,,0000*5E")

        assertEquals("GPGGA", gga.type)
        assertEquals("002153.000", gga.time)
        assertEquals(33.71103, gga.latitude!!, PRECISION)
        assertEquals(-117.85643, gga.longitude!!, PRECISION)
        assertEquals(GGA.Quality.GPS_FIX, gga.quality)
        assertEquals(10, gga.satelliteCount)
        assertEquals(1.2, gga.horizontalDilutionOfPrecision!!, PRECISION)
        assertEquals(27.0, gga.altitude!!, PRECISION)
        assertEquals(-34.2, gga.ellipsoidalOffset!!, PRECISION)
        assertEquals(null, gga.differentialGpsAge)
        assertEquals("0000", gga.differentialGpsStationId)
        assertEquals("5E", gga.checksum)
    }

    @Test
    fun validateAlternateConstructor() {
        val gga = GGA(
            "GP",
            "002153.000",
            33.71103,
            -117.85643,
            GGA.Quality.GPS_FIX,
            10,
            1.2,
            27.0,
            -34.2,
            null,
            "0000"
        )

        assertEquals(
            "\$GPGGA,002153.000,3342.6618,N,11751.3858,W,1,10,1.2,27.0,M,-34.2,M,,0000*5E",
            gga.message
        )
    }

    @Test
    fun validateEmpty() {
        val gga = GGA("\$GPGGA,,,,,,0,,,,,,,,*66")

        assertEquals("GPGGA", gga.type)
        assertEquals("", gga.time)
        assertEquals(null, gga.latitude)
        assertEquals(null, gga.longitude)
        assertEquals(GGA.Quality.FIX_NOT_AVAILABLE, gga.quality)
        assertEquals(0, gga.satelliteCount)
        assertEquals(null, gga.horizontalDilutionOfPrecision)
        assertEquals(null, gga.altitude)
        assertEquals(null, gga.ellipsoidalOffset)
        assertEquals(null, gga.differentialGpsAge)
        assertEquals("", gga.differentialGpsStationId)
        assertEquals("66", gga.checksum)
    }

    companion object {
        private const val PRECISION = 0.0000001
    }
}