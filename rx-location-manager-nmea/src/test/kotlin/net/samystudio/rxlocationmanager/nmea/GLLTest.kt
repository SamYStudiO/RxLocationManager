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

    companion object {
        private const val PRECISION = 0.0000001
    }
}