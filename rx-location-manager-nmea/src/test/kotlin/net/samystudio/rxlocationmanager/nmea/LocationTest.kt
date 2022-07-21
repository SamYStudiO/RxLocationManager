package net.samystudio.rxlocationmanager.nmea

import org.junit.Assert.assertEquals
import org.junit.Test

class LocationTest {
    @Test
    fun convert() {
        assertEquals(
            48.864716,
            convertNmeaLocation("4851.88296", Cardinal.N)!!,
            PRECISION
        )

        assertEquals(
            2.349014,
            convertNmeaLocation("00220.94084", Cardinal.E)!!,
            PRECISION
        )

        assertEquals(
            -48.864716,
            convertNmeaLocation("4851.88296", Cardinal.S)!!,
            PRECISION
        )

        assertEquals(
            -2.349014,
            convertNmeaLocation("00220.94084", Cardinal.W)!!,
            PRECISION
        )

        assertEquals(null, convertNmeaLocation("", Cardinal.W))
        assertEquals(null, convertNmeaLocation("00a08.25-750", Cardinal.W))
    }

    @Test
    fun locationDirectionValueOf() {
        assertEquals(Cardinal.N, Cardinal.valueOf("N", Cardinal.S))
        assertEquals(Cardinal.S, Cardinal.valueOf("S", Cardinal.S))
        assertEquals(Cardinal.E, Cardinal.valueOf("E", Cardinal.S))
        assertEquals(Cardinal.W, Cardinal.valueOf("W", Cardinal.S))
        assertEquals(Cardinal.N, Cardinal.valueOf("Z", Cardinal.N))
    }

    companion object {
        private const val PRECISION = 0.0000001
    }
}
