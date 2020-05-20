package net.samystudio.rxlocationmanager.nmea

import org.junit.Assert.assertEquals
import org.junit.Test

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
        assertEquals(4, gsa.satelliteCount)
        assertEquals(10.1, gsa.positionDilutionOfPrecision!!, PRECISION)
        assertEquals(12.2, gsa.horizontalDilutionOfPrecision!!, PRECISION)
        assertEquals(14.4, gsa.verticalDilutionOfPrecision!!, PRECISION)
        assertEquals("07", gsa.checksum)
    }

    @Test
    fun validateAlternateConstructor() {
        val gsa = GSA(
            "GP",
            GSA.Mode1.M,
            GSA.Mode2.FIX_3D,
            null,
            null,
            22,
            null,
            null,
            85,
            null,
            null,
            14,
            23,
            null,
            null,
            10.1,
            12.2,
            14.4
        )

        assertEquals("\$GPGSA,M,3,,,22,,,85,,,14,23,,,10.1,12.2,14.4*07", gsa.message)
    }

    @Test
    fun validateEmpty() {
        val gsa = GSA("\$GPGSA,A,1,,,,,,,,,,,,,,,*1E")

        assertEquals("GPGSA", gsa.type)
        assertEquals(GSA.Mode1.A, gsa.mode1)
        assertEquals(GSA.Mode2.FIX_NOT_AVAILABLE, gsa.mode2)
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
        assertEquals(0, gsa.satelliteCount)
        assertEquals(null, gsa.positionDilutionOfPrecision)
        assertEquals(null, gsa.horizontalDilutionOfPrecision)
        assertEquals(null, gsa.verticalDilutionOfPrecision)
        assertEquals("1E", gsa.checksum)
    }

    companion object {
        private const val PRECISION = 0.0000001
    }
}