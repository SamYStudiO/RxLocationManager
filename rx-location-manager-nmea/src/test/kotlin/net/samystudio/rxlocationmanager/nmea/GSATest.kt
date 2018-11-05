package net.samystudio.rxlocationmanager.nmea

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class GSATest {
    @Test
    fun validate() {
        val gsa = GSA("\$GPGSA,M,3,,,22,,,85,,,14,23,,,10.1,12.2,14.4*07")

        assertEquals("GPGSA", gsa.type)
        assertEquals(GSA.Mode1.M, gsa.mode1)
        assertEquals(GSA.Mode2.FIX_3D, gsa.mode2)
        assertEquals(null, gsa.satellite1)
        assertEquals(null, gsa.satellite2)
        assertEquals(22, gsa.satellite3)
        assertEquals(null, gsa.satellite4)
        assertEquals(null, gsa.satellite5)
        assertEquals(85, gsa.satellite6)
        assertEquals(null, gsa.satellite7)
        assertEquals(null, gsa.satellite8)
        assertEquals(14, gsa.satellite9)
        assertEquals(23, gsa.satellite10)
        assertEquals(null, gsa.satellite11)
        assertEquals(null, gsa.satellite12)
        assertEquals(10.1, gsa.positionDilutionOfPrecision!!, PRECISION)
        assertEquals(12.2, gsa.horizontalDilutionOfPrecision!!, PRECISION)
        assertEquals(14.4, gsa.verticalDilutionOfPrecision!!, PRECISION)
        assertEquals("07", gsa.checksum)
    }

    @Test
    fun validateEmpty() {
        val gsa = GSA("\$GPGSA,,,,,,,,,,,,,,,,,*6E")

        assertEquals("GPGSA", gsa.type)
        assertEquals(null, gsa.mode1)
        assertEquals(null, gsa.mode2)
        assertEquals(null, gsa.satellite1)
        assertEquals(null, gsa.satellite2)
        assertEquals(null, gsa.satellite3)
        assertEquals(null, gsa.satellite4)
        assertEquals(null, gsa.satellite5)
        assertEquals(null, gsa.satellite6)
        assertEquals(null, gsa.satellite7)
        assertEquals(null, gsa.satellite8)
        assertEquals(null, gsa.satellite9)
        assertEquals(null, gsa.satellite10)
        assertEquals(null, gsa.satellite11)
        assertEquals(null, gsa.satellite12)
        assertEquals(null, gsa.positionDilutionOfPrecision)
        assertEquals(null, gsa.horizontalDilutionOfPrecision)
        assertEquals(null, gsa.verticalDilutionOfPrecision)
        assertEquals("6E", gsa.checksum)
    }

    @Test
    fun validateTypeError() {
        val message = "\$GPGA,M,3,,,22,,,85,,,14,23,,,10.1,12.2,14.4*07"
        val expected = NmeaException("Cannot parse message at index 0 from source $message", 0)
        var result: NmeaException? = null
        try {
            GSA(message)
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }

    @Test
    fun validateMode1Error() {
        val message = "\$GPGSA,z,3,,,22,,,85,,,14,23,,,10.1,12.2,14.4*07"
        val expected = NmeaException("Cannot parse message at index 1 from source $message", 1)
        var result: NmeaException? = null
        try {
            GSA(message)
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }

    @Test
    fun validateMode2Error() {
        val message = "\$GPGSA,M,z,,,22,,,85,,,14,23,,,10.1,12.2,14.4*07"
        val expected = NmeaException("Cannot parse message at index 2 from source $message", 2)
        var result: NmeaException? = null
        try {
            GSA(message)
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }

    @Test
    fun validateSatellite1Error() {
        val message = "\$GPGSA,M,3,z,,22,,,85,,,14,23,,,10.1,12.2,14.4*07"
        val expected = NmeaException("Cannot parse message at index 3 from source $message", 3)
        var result: NmeaException? = null
        try {
            GSA(message)
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }

    @Test
    fun validateSatellite2Error() {
        val message = "\$GPGSA,M,3,,z,22,,,85,,,14,23,,,10.1,12.2,14.4*07"
        val expected = NmeaException("Cannot parse message at index 4 from source $message", 4)
        var result: NmeaException? = null
        try {
            GSA(message)
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }

    @Test
    fun validateSatellite3Error() {
        val message = "\$GPGSA,M,3,,,z,,,85,,,14,23,,,10.1,12.2,14.4*07"
        val expected = NmeaException("Cannot parse message at index 5 from source $message", 5)
        var result: NmeaException? = null
        try {
            GSA(message)
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }

    @Test
    fun validateSatellite4Error() {
        val message = "\$GPGSA,M,3,,,22,z,,85,,,14,23,,,10.1,12.2,14.4*07"
        val expected = NmeaException("Cannot parse message at index 6 from source $message", 6)
        var result: NmeaException? = null
        try {
            GSA(message)
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }

    @Test
    fun validateSatellite5Error() {
        val message = "\$GPGSA,M,3,,,22,,z,85,,,14,23,,,10.1,12.2,14.4*07"
        val expected = NmeaException("Cannot parse message at index 7 from source $message", 7)
        var result: NmeaException? = null
        try {
            GSA(message)
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }

    @Test
    fun validateSatellite6Error() {
        val message = "\$GPGSA,M,3,,,22,,,z,,,14,23,,,10.1,12.2,14.4*07"
        val expected = NmeaException("Cannot parse message at index 8 from source $message", 8)
        var result: NmeaException? = null
        try {
            GSA(message)
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }

    @Test
    fun validateSatellite7Error() {
        val message = "\$GPGSA,M,3,,,22,,,85,z,,14,23,,,10.1,12.2,14.4*07"
        val expected = NmeaException("Cannot parse message at index 9 from source $message", 9)
        var result: NmeaException? = null
        try {
            GSA(message)
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }

    @Test
    fun validateSatellite8Error() {
        val message = "\$GPGSA,M,3,,,22,,,85,,z,14,23,,,10.1,12.2,14.4*07"
        val expected = NmeaException("Cannot parse message at index 10 from source $message", 10)
        var result: NmeaException? = null
        try {
            GSA(message)
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }

    @Test
    fun validateSatellite9Error() {
        val message = "\$GPGSA,M,3,,,22,,,85,,,z,23,,,10.1,12.2,14.4*07"
        val expected = NmeaException("Cannot parse message at index 11 from source $message", 11)
        var result: NmeaException? = null
        try {
            GSA(message)
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }

    @Test
    fun validateSatellite11Error() {
        val message = "\$GPGSA,M,3,,,22,,,85,,,14,23,z,,10.1,12.2,14.4*07"
        val expected = NmeaException("Cannot parse message at index 13 from source $message", 13)
        var result: NmeaException? = null
        try {
            GSA(message)
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }

    @Test
    fun validateSatellite12Error() {
        val message = "\$GPGSA,M,3,,,22,,,85,,,14,23,,z,10.1,12.2,14.4*07"
        val expected = NmeaException("Cannot parse message at index 14 from source $message", 14)
        var result: NmeaException? = null
        try {
            GSA(message)
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }

    @Test
    fun validatePositionDilutionOfPrecisionError() {
        val message = "\$GPGSA,M,3,,,22,,,85,,,14,23,,,10z1,12.2,14.4*07"
        val expected = NmeaException("Cannot parse message at index 15 from source $message", 15)
        var result: NmeaException? = null
        try {
            GSA(message)
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }

    @Test
    fun validateHorizontalDilutionOfPrecisionError() {
        val message = "\$GPGSA,M,3,,,22,,,85,,,14,23,,,10.1,12z2,14.4*07"
        val expected = NmeaException("Cannot parse message at index 16 from source $message", 16)
        var result: NmeaException? = null
        try {
            GSA(message)
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }

    @Test
    fun validateVerticalDilutionOfPrecisionError() {
        val message = "\$GPGSA,M,3,,,22,,,85,,,14,23,,,10.1,12.2,14z4*07"
        val expected = NmeaException("Cannot parse message at index 17 from source $message", 17)
        var result: NmeaException? = null
        try {
            GSA(message)
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }

    companion object {
        private const val PRECISION = 0.0000001
    }
}