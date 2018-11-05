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
        assertEquals(GGA.Quality.FIX, gga.quality)
        assertEquals(10, gga.satelliteCount)
        assertEquals(1.2, gga.horizontalDilutionOfPrecision!!, PRECISION)
        assertEquals(27.0, gga.altitude!!, PRECISION)
        assertEquals(-34.2, gga.ellipsoidalOffset!!, PRECISION)
        assertEquals(null, gga.differentialGpsAge)
        assertEquals("0000", gga.differentialGpsStationId)
        assertEquals("5E", gga.checksum)
    }

    @Test
    fun validateEmpty() {
        val gga = GGA("\$GPGGA,,,,,,,,,,,,,,*56")

        assertEquals("GPGGA", gga.type)
        assertEquals("", gga.time)
        assertEquals(null, gga.latitude)
        assertEquals(null, gga.longitude)
        assertEquals(GGA.Quality.NO_FIX, gga.quality)
        assertEquals(0, gga.satelliteCount)
        assertEquals(null, gga.horizontalDilutionOfPrecision)
        assertEquals(null, gga.altitude)
        assertEquals(null, gga.ellipsoidalOffset)
        assertEquals(null, gga.differentialGpsAge)
        assertEquals("", gga.differentialGpsStationId)
        assertEquals("56", gga.checksum)
    }

    @Test
    fun validateTypeError() {
        val message = "\$GPGG,002153.000,3342.6618,N,11751.3858,W,1,10,1.2,27.0,M,-34.2,M,,0000*5E"
        val expected = NmeaException("Cannot parse message at index 0 from source $message", 0)
        var result: NmeaException? = null
        try {
            GGA(message)
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }

    @Test
    fun validateTimeError() {
        val message = "\$GPGGA,002153000,3342.6618,N,11751.3858,W,1,10,1.2,27.0,M,-34.2,M,,0000*5E"
        val expected = NmeaException("Cannot parse message at index 1 from source $message", 1)
        var result: NmeaException? = null
        try {
            GGA(message)
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }

    @Test
    fun validateLatitudeError() {
        val message = "\$GPGGA,002153.000,33426618,N,11751.3858,W,1,10,1.2,27.0,M,-34.2,M,,0000*5E"
        val expected = NmeaException("Cannot parse message at index 2 from source $message", 2)
        var result: NmeaException? = null
        try {
            GGA(message)
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }

    @Test
    fun validateLatitudeDirectionError() {
        val message = "\$GPGGA,002153.000,3342.6618,Z,11751.3858,W,1,10,1.2,27.0,M,-34.2,M,,0000*5E"
        val expected = NmeaException("Cannot parse message at index 3 from source $message", 3)
        var result: NmeaException? = null
        try {
            GGA(message)
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }

    @Test
    fun validateLongitudeError() {
        val message = "\$GPGGA,002153.000,3342.6618,N,117513858,W,1,10,1.2,27.0,M,-34.2,M,,0000*5E"
        val expected = NmeaException("Cannot parse message at index 4 from source $message", 4)
        var result: NmeaException? = null
        try {
            GGA(message)
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }

    @Test
    fun validateLongitudeDirectionError() {
        val message = "\$GPGGA,002153.000,3342.6618,N,11751.3858,z,1,10,1.2,27.0,M,-34.2,M,,0000*5E"
        val expected = NmeaException("Cannot parse message at index 5 from source $message", 5)
        var result: NmeaException? = null
        try {
            GGA(message)
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }


    @Test
    fun validateQualityError() {
        val message =
            "\$GPGGA,002153.000,3342.6618,N,11751.3858,W,10,10,1.2,27.0,M,-34.2,M,,0000*5E"
        val expected = NmeaException("Cannot parse message at index 6 from source $message", 6)
        var result: NmeaException? = null
        try {
            GGA(message)
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }

    @Test
    fun validateSatelliteCountError() {
        val message =
            "\$GPGGA,002153.000,3342.6618,N,11751.3858,W,1,10z,1.2,27.0,M,-34.2,M,,0000*5E"
        val expected = NmeaException("Cannot parse message at index 7 from source $message", 7)
        var result: NmeaException? = null
        try {
            GGA(message)
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }

    @Test
    fun validateHorizontalDilutionOfPrecisionError() {
        val message =
            "\$GPGGA,002153.000,3342.6618,N,11751.3858,W,1,10,1.z2,27.0,M,-34.2,M,,0000*5E"
        val expected = NmeaException("Cannot parse message at index 8 from source $message", 8)
        var result: NmeaException? = null
        try {
            GGA(message)
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }

    @Test
    fun validateAltitudeError() {
        val message =
            "\$GPGGA,002153.000,3342.6618,N,11751.3858,W,1,10,1.2,27z.0,M,-34.2,M,,0000*5E"
        val expected = NmeaException("Cannot parse message at index 9 from source $message", 9)
        var result: NmeaException? = null
        try {
            GGA(message)
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }

    @Test
    fun validateAltitudeUnitError() {
        val message = "\$GPGGA,002153.000,3342.6618,N,11751.3858,W,1,10,1.2,27.0,z,-34.2,M,,0000*5E"
        val expected = NmeaException("Cannot parse message at index 10 from source $message", 10)
        var result: NmeaException? = null
        try {
            GGA(message)
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }

    @Test
    fun validateEllipsoidOffsetError() {
        val message =
            "\$GPGGA,002153.000,3342.6618,N,11751.3858,W,1,10,1.2,27.0,M,-34z.2,M,,0000*5E"
        val expected = NmeaException("Cannot parse message at index 11 from source $message", 11)
        var result: NmeaException? = null
        try {
            GGA(message)
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }

    @Test
    fun validateEllipsoidOffsetUnitError() {
        val message = "\$GPGGA,002153.000,3342.6618,N,11751.3858,W,1,10,1.2,27.0,M,-34.2,z,,0000*5E"
        val expected = NmeaException("Cannot parse message at index 12 from source $message", 12)
        var result: NmeaException? = null
        try {
            GGA(message)
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }

    @Test
    fun validateDifferentialAgeError() {
        val message =
            "\$GPGGA,002153.000,3342.6618,N,11751.3858,W,1,10,1.2,27.0,M,-34.2,M,z,0000*5E"
        val expected = NmeaException("Cannot parse message at index 13 from source $message", 13)
        var result: NmeaException? = null
        try {
            GGA(message)
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }

    @Test
    fun validateStationIdError() {
        val message = "\$GPGGA,002153.000,3342.6618,N,11751.3858,W,1,10,1.2,27.0,M,-34.2,M,,2000*5E"
        val expected = NmeaException("Cannot parse message at index 14 from source $message", 14)
        var result: NmeaException? = null
        try {
            GGA(message)
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }

    companion object {
        private const val PRECISION = 0.0000001
    }
}