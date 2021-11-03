package net.samystudio.rxlocationmanager.nmea

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LatitudeLongitudeValidatorTest {
    @Test
    fun validateLatitude() {
        LatitudeValidator().let {
            assertTrue(it.validate("0000.0000"))
            assertTrue(it.validate("0000.00000"))
            assertFalse(it.validate("000.0000"))
            assertFalse(it.validate("0000.000"))
            assertFalse(it.validate("0000"))
            assertFalse(it.validate("0A00.0000"))
            assertFalse(it.validate("0000.00A0"))
            assertFalse(it.validate("A0000.0000"))
            assertFalse(it.validate("0000.0000A"))
        }
    }

    @Test
    fun validateLongitude() {
        LongitudeValidator().let {
            assertTrue(it.validate("00000.0000"))
            assertTrue(it.validate("00000.00000"))
            assertFalse(it.validate("0000.0000"))
            assertFalse(it.validate("00000.000"))
            assertFalse(it.validate("00000"))
            assertFalse(it.validate("0A000.0000"))
            assertFalse(it.validate("00000.00A0"))
            assertFalse(it.validate("A00000.0000"))
            assertFalse(it.validate("00000.0000A"))
        }
    }

    @Test
    fun validateLatitudeOptional() {
        LatitudeValidator(true).let {
            assertTrue(it.validate(""))
            assertFalse(it.validate("A"))
        }
    }

    @Test
    fun validateLongitudeOptional() {
        LongitudeValidator(true).let {
            assertTrue(it.validate(""))
            assertFalse(it.validate("A"))
        }
    }
}
