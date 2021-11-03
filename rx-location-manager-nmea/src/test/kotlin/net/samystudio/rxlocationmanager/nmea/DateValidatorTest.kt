package net.samystudio.rxlocationmanager.nmea

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DateValidatorTest {
    @Test
    fun validate() {
        DateValidator().let {
            assertTrue(it.validate("000000"))
            assertFalse(it.validate("0000000"))
            assertFalse(it.validate("A000000"))
            assertFalse(it.validate("000000A"))
            assertFalse(it.validate("A00000"))
        }
    }

    @Test
    fun validateOptional() {
        DateValidator(true).let {
            assertTrue(it.validate(""))
            assertFalse(it.validate("A"))
        }
    }
}
