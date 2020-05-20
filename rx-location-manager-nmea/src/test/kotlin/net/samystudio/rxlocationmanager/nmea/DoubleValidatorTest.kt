package net.samystudio.rxlocationmanager.nmea

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DoubleValidatorTest {
    @Test
    fun validate() {
        DoubleValidator().let {
            assertTrue(it.validate("10.0"))
            assertTrue(it.validate("10"))
            assertFalse(it.validate(""))
            assertFalse(it.validate("A"))
        }
    }

    @Test
    fun validateOptional() {
        DoubleValidator(true).let {
            assertTrue(it.validate("10.0"))
            assertTrue(it.validate("10"))
            assertTrue(it.validate(""))
            assertFalse(it.validate("A"))
        }
    }

    @Test
    fun validateRange() {
        DoubleValidator(false, -10.0, 10.0).let {
            assertTrue(it.validate("10.0"))
            assertTrue(it.validate("10"))
            assertTrue(it.validate("-5.0"))
            assertFalse(it.validate("12.0"))
            assertFalse(it.validate("-12.0"))
        }
    }
}