package net.samystudio.rxlocationmanager.nmea

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class LocationTest {
    @Test
    fun convert() {
        assertEquals(
            48.864716,
            convertNmeaLocation("4851.88296", LocationDirection.N)!!,
            PRECISION
        )

        assertEquals(
            2.349014,
            convertNmeaLocation("00220.94084", LocationDirection.E)!!,
            PRECISION
        )

        assertEquals(
            -48.864716,
            convertNmeaLocation("4851.88296", LocationDirection.S)!!,
            PRECISION
        )

        assertEquals(
            -2.349014,
            convertNmeaLocation("00220.94084", LocationDirection.W)!!,
            PRECISION
        )

        assertEquals(null, convertNmeaLocation("", LocationDirection.W))
        assertEquals(null, convertNmeaLocation("00a08.25-750", LocationDirection.W))
    }

    @Test
    fun locationDirectionValueOf() {
        assertEquals(LocationDirection.N, LocationDirection.valueOf("N", LocationDirection.S))
        assertEquals(LocationDirection.S, LocationDirection.valueOf("S", LocationDirection.S))
        assertEquals(LocationDirection.E, LocationDirection.valueOf("E", LocationDirection.S))
        assertEquals(LocationDirection.W, LocationDirection.valueOf("W", LocationDirection.S))
        assertEquals(LocationDirection.N, LocationDirection.valueOf("Z", LocationDirection.N))
    }

    companion object {
        private const val PRECISION = 0.0000001
    }
}