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
        val expected = NmeaException("Cannot parse message at index 0 from source $message", 0)
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
        val expected = NmeaException("Cannot parse message at index 1 from source $message", 1)
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
        val expected = NmeaException("Cannot parse message at index 2 from source $message", 2)
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
        val expected = NmeaException("Cannot parse message at index 3 from source $message", 3)
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
        val expected = NmeaException("Cannot parse message at index 4 from source $message", 4)
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
        val expected = NmeaException("Cannot parse message at index 5 from source $message", 5)
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
        val expected = NmeaException("Cannot parse message at index 6 from source $message", 6)
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