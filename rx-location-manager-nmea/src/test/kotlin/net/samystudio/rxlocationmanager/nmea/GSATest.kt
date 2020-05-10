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
        val expected =
            NmeaException(
                Nmea.getParseErrorMessage(
                    0,
                    TypeValidator::class.java.simpleName,
                    message
                ), 0
            )
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
        val expected =
            NmeaException(
                Nmea.getParseErrorMessage(
                    1,
                    EnumValidator::class.java.simpleName,
                    message
                ), 1
            )
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
        val expected =
            NmeaException(
                Nmea.getParseErrorMessage(
                    2,
                    EnumValidator::class.java.simpleName,
                    message
                ), 2
            )
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
        val expected =
            NmeaException(
                Nmea.getParseErrorMessage(
                    3,
                    IntValidator::class.java.simpleName,
                    message
                ), 3
            )
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
        val expected =
            NmeaException(
                Nmea.getParseErrorMessage(
                    4,
                    IntValidator::class.java.simpleName,
                    message
                ), 4
            )
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
        val expected =
            NmeaException(
                Nmea.getParseErrorMessage(
                    5,
                    IntValidator::class.java.simpleName,
                    message
                ), 5
            )
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
        val expected =
            NmeaException(
                Nmea.getParseErrorMessage(
                    6,
                    IntValidator::class.java.simpleName,
                    message
                ), 6
            )
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
        val expected =
            NmeaException(
                Nmea.getParseErrorMessage(
                    7,
                    IntValidator::class.java.simpleName,
                    message
                ), 7
            )
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
        val expected =
            NmeaException(
                Nmea.getParseErrorMessage(
                    8,
                    IntValidator::class.java.simpleName,
                    message
                ), 8
            )
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
        val expected =
            NmeaException(
                Nmea.getParseErrorMessage(
                    9,
                    IntValidator::class.java.simpleName,
                    message
                ), 9
            )
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
        val expected =
            NmeaException(
                Nmea.getParseErrorMessage(
                    10,
                    IntValidator::class.java.simpleName,
                    message
                ), 10
            )
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
        val expected =
            NmeaException(
                Nmea.getParseErrorMessage(
                    11,
                    IntValidator::class.java.simpleName,
                    message
                ), 11
            )
        var result: NmeaException? = null
        try {
            GSA(message)
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }

    @Test
    fun validateSatellite10Error() {
        val message = "\$GPGSA,M,3,,,22,,,85,,,14,z,,,10.1,12.2,14.4*07"
        val expected =
            NmeaException(
                Nmea.getParseErrorMessage(
                    12,
                    IntValidator::class.java.simpleName,
                    message
                ), 12
            )
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
        val expected =
            NmeaException(
                Nmea.getParseErrorMessage(
                    13,
                    IntValidator::class.java.simpleName,
                    message
                ), 13
            )
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
        val expected =
            NmeaException(
                Nmea.getParseErrorMessage(
                    14,
                    IntValidator::class.java.simpleName,
                    message
                ), 14
            )
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
        val expected =
            NmeaException(
                Nmea.getParseErrorMessage(
                    15,
                    DoubleValidator::class.java.simpleName,
                    message
                ), 15
            )
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
        val expected =
            NmeaException(
                Nmea.getParseErrorMessage(
                    16,
                    DoubleValidator::class.java.simpleName,
                    message
                ), 16
            )
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
        val expected =
            NmeaException(
                Nmea.getParseErrorMessage(
                    17,
                    DoubleValidator::class.java.simpleName,
                    message
                ), 17
            )
        var result: NmeaException? = null
        try {
            GSA(message)
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }

    @Test
    fun validateSatelliteCount() {
        var message = "\$GPGSA,M,3,,,22,,,85,,,14,23,,,10.1,12.2,14.4*07"
        assertEquals(4, GSA(message).satelliteCount)
        message = "\$GPGSA,M,3,10,,22,,,85,,,14,23,,11,10.1,12.2,14.4*06"
        assertEquals(6, GSA(message).satelliteCount)
    }

    companion object {
        private const val PRECISION = 0.0000001
    }
}