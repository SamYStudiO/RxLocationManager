package net.samystudio.rxlocationmanager.nmea

import org.junit.Assert.assertEquals
import org.junit.Test

class GLLTest {
    @Test
    fun validate() {
        val gll = GLL("\$GPGLL,3342.6618,N,11751.3858,W,002153.000,A,A*44")

        assertEquals("GPGLL", gll.type)
        assertEquals(33.71103, gll.latitude!!, PRECISION)
        assertEquals(-117.85643, gll.longitude!!, PRECISION)
        assertEquals("002153.000", gll.time)
        assertEquals(Status.A, gll.status)
        assertEquals(FAAMode.A, gll.faaMode)
        assertEquals("44", gll.checksum)
    }

    @Test
    fun validateAlternateConstructor() {
        val gll = GLL(
            "GP",
            33.71103,
            -117.85643,
            "002153.000",
            Status.A,
            FAAMode.A,
        )

        assertEquals("\$GPGLL,3342.6618,N,11751.3858,W,002153.000,A,A*44", gll.message)
    }

    @Test
    fun validateEmpty() {
        val gll = GLL("\$GPGLL,,,,,,,N,*1E")

        assertEquals("GPGLL", gll.type)
        assertEquals(null, gll.latitude)
        assertEquals(null, gll.longitude)
        assertEquals("", gll.time)
        assertEquals(null, gll.status)
        assertEquals(FAAMode.N, gll.faaMode)
        assertEquals("1E", gll.checksum)
    }

    companion object {
        private const val PRECISION = 0.0000001
    }
}
