package net.samystudio.rxlocationmanager.nmea

import org.junit.Assert.assertEquals
import org.junit.Test

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
    fun validateWithSpecialDate() {
        val gga =
            GGA("\$GPGGA,103852.0,3342.6618,N,11751.3858,W,1,10,1.2,27.0,M,-34.2,M,,0000*56")

        assertEquals("GPGGA", gga.type)
        assertEquals("103852.0", gga.time)
        assertEquals(33.71103, gga.latitude!!, PRECISION)
        assertEquals(-117.85643, gga.longitude!!, PRECISION)
        assertEquals(GGA.Quality.GPS_FIX, gga.quality)
        assertEquals(10, gga.satelliteCount)
        assertEquals(1.2, gga.horizontalDilutionOfPrecision!!, PRECISION)
        assertEquals(27.0, gga.altitude!!, PRECISION)
        assertEquals(-34.2, gga.ellipsoidalOffset!!, PRECISION)
        assertEquals(null, gga.differentialGpsAge)
        assertEquals("0000", gga.differentialGpsStationId)
        assertEquals("56", gga.checksum)
    }

    @Test
    fun validateAlternateConstructor() {
        val gga =
            GGA(
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
                "0000",
            )

        assertEquals(
            "\$GPGGA,002153.000,3342.6618,N,11751.3858,W,1,10,1.2,27.0,M,-34.2,M,,0000*5E",
            gga.message,
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

    @Test
    fun validateStation() {
        val gga =
            GGA("\$GNGGA,121813.000,4402.7838,N,00443.1020,E,2,23,0.58,108.4,M,49.2,M,0.0,0*6F")

        assertEquals("GNGGA", gga.type)
        assertEquals("121813.000", gga.time)
        assertEquals(44.046396666666666, gga.latitude!!, PRECISION)
        assertEquals(4.718366666666666, gga.longitude!!, PRECISION)
        assertEquals(GGA.Quality.DIFFERENTIAL_GPS_FIX, gga.quality)
        assertEquals(23, gga.satelliteCount)
        assertEquals(0.58, gga.horizontalDilutionOfPrecision!!, PRECISION)
        assertEquals(108.4, gga.altitude!!, PRECISION)
        assertEquals(49.2, gga.ellipsoidalOffset!!, PRECISION)
        assertEquals(0.0, gga.differentialGpsAge)
        assertEquals("0", gga.differentialGpsStationId)
        assertEquals("6F", gga.checksum)
    }

    @Test
    fun validateWithoutThrowIfContentInvalid() {
        val gga =
            GGA("\$GNGGA,121813.000,4402.7838,,00443.1020,E,,23,0.58,108.4,M,,M,aze,0*52", false)

        assertEquals("GNGGA", gga.type)
        assertEquals("121813.000", gga.time)
        assertEquals(44.046396666666666, gga.latitude!!, PRECISION)
        assertEquals(4.718366666666666, gga.longitude!!, PRECISION)
        assertEquals(GGA.Quality.FIX_NOT_AVAILABLE, gga.quality)
        assertEquals(23, gga.satelliteCount)
        assertEquals(0.58, gga.horizontalDilutionOfPrecision!!, PRECISION)
        assertEquals(108.4, gga.altitude!!, PRECISION)
        assertEquals(null, gga.ellipsoidalOffset)
        assertEquals(null, gga.differentialGpsAge)
        assertEquals("0", gga.differentialGpsStationId)
        assertEquals("52", gga.checksum)
    }

    companion object {
        private const val PRECISION = 0.0000001
    }
}
