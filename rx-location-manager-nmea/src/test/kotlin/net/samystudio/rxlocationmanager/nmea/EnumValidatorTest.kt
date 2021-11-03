package net.samystudio.rxlocationmanager.nmea

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EnumValidatorTest {
    @Test
    fun validate() {
        EnumValidator(charArrayOf('A', 'B', 'C')).let {
            assertTrue(it.validate("A"))
            assertTrue(it.validate("B"))
            assertTrue(it.validate("C"))
            assertFalse(it.validate("D"))
            assertFalse(it.validate("a"))
            assertFalse(it.validate(""))
        }
    }

    @Test
    fun validateOptional() {
        EnumValidator(charArrayOf('A'), true).let {
            assertTrue(it.validate(""))
            assertFalse(it.validate("B"))
        }
    }
}
