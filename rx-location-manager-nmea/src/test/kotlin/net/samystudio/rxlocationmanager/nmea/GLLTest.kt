package net.samystudio.rxlocationmanager.nmea

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class GLLTest {
    @Test
    fun validate() {
        val gll = GLL("\$GPGLL,3342.6618,N,11751.3858,W,002153.000,A*29")

        assertEquals("GPGLL", gll.type)
        assertEquals(33.71103, gll.latitude!!, PRECISION)
        assertEquals(-117.85643, gll.longitude!!, PRECISION)
        assertEquals("002153.000", gll.time)
        assertEquals(GLL.Status.A, gll.status)
        assertEquals("29", gll.checksum)
    }

    @Test
    fun validateEmpty() {
        val gll = GLL("\$GPGLL,,,,,,*50")

        assertEquals("GPGLL", gll.type)
        assertEquals(null, gll.latitude)
        assertEquals(null, gll.longitude)
        assertEquals("", gll.time)
        assertEquals(null, gll.status)
        assertEquals("50", gll.checksum)
    }

    @Test
    fun validateTypeError() {
        val message = "\$GPGL,3342.6618,N,11751.3858,W,002153.000,A*29"
        val expected =
            NmeaException(Nmea.getParseErrorMessage(0, TypeValidator::class.java, message), 0)
        var result: NmeaException? = null
        try {
            GLL(message)
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }

    @Test
    fun validateLatitudeError() {
        val message = "\$GPGLL,33426618,N,11751.3858,W,002153.000,A*29"
        val expected =
            NmeaException(Nmea.getParseErrorMessage(1, LatitudeValidator::class.java, message), 1)
        var result: NmeaException? = null
        try {
            GLL(message)
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }

    @Test
    fun validateLatitudeDirectionError() {
        val message = "\$GPGLL,3342.6618,z,11751.3858,W,002153.000,A*29"
        val expected =
            NmeaException(Nmea.getParseErrorMessage(2, EnumValidator::class.java, message), 2)
        var result: NmeaException? = null
        try {
            GLL(message)
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }

    @Test
    fun validateLongitudeError() {
        val message = "\$GPGLL,3342.6618,N,117513858,W,002153.000,A*29"
        val expected =
            NmeaException(Nmea.getParseErrorMessage(3, LongitudeValidator::class.java, message), 3)
        var result: NmeaException? = null
        try {
            GLL(message)
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }

    @Test
    fun validateLongitudeDirectionError() {
        val message = "\$GPGLL,3342.6618,N,11751.3858,z,002153.000,A*29"
        val expected =
            NmeaException(Nmea.getParseErrorMessage(4, EnumValidator::class.java, message), 4)
        var result: NmeaException? = null
        try {
            GLL(message)
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }

    @Test
    fun validateTimeError() {
        val message = "\$GPGLL,3342.6618,N,11751.3858,W,002153000,A*29"
        val expected =
            NmeaException(Nmea.getParseErrorMessage(5, TimeValidator::class.java, message), 5)
        var result: NmeaException? = null
        try {
            GLL(message)
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }


    @Test
    fun validateStatusError() {
        val message = "\$GPGLL,3342.6618,N,11751.3858,W,002153.000,z*29"
        val expected =
            NmeaException(Nmea.getParseErrorMessage(6, EnumValidator::class.java, message), 6)
        var result: NmeaException? = null
        try {
            GLL(message)
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }

    companion object {
        private const val PRECISION = 0.0000001
    }
}