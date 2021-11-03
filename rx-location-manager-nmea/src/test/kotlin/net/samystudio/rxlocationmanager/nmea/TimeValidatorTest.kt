package net.samystudio.rxlocationmanager.nmea

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TimeValidatorTest {
    @Test
    fun validate() {
        TimeValidator().let {
            assertTrue(it.validate("000000"))
            assertTrue(it.validate("000000.00"))
            assertTrue(it.validate("000000.0000"))
            assertFalse(it.validate("0000000"))
            assertFalse(it.validate("000000.0"))
            assertFalse(it.validate("000000.00000"))
            assertFalse(it.validate("A000000.00000"))
            assertFalse(it.validate("000000.00000A"))
            assertFalse(it.validate("A00000.0000"))
            assertFalse(it.validate("000000.0000A"))
        }
    }

    @Test
    fun validateOptional() {
        TimeValidator(true).let {
            assertTrue(it.validate(""))
            assertFalse(it.validate("A"))
        }
    }
}
