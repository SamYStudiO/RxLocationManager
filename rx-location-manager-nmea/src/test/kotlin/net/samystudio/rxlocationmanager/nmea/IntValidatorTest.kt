package net.samystudio.rxlocationmanager.nmea

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IntValidatorTest {
    @Test
    fun validate() {
        IntValidator().let {
            assertTrue(it.validate("10"))
            assertFalse(it.validate(""))
            assertFalse(it.validate("A"))
        }
    }

    @Test
    fun validateOptional() {
        IntValidator(true).let {
            assertTrue(it.validate("10"))
            assertTrue(it.validate(""))
            assertFalse(it.validate("A"))
        }
    }

    @Test
    fun validateRange() {
        IntValidator(false, -10, 10).let {
            assertTrue(it.validate("10"))
            assertTrue(it.validate("-5"))
            assertFalse(it.validate("12"))
            assertFalse(it.validate("-12"))
        }
    }
}