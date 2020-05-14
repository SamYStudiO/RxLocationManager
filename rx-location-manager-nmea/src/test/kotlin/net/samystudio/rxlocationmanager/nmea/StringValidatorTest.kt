package net.samystudio.rxlocationmanager.nmea

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StringValidatorTest {
    @Test
    fun validate() {
        StringValidator().let {
            assertTrue(it.validate("ABCD"))
            assertTrue(it.validate(""))
        }
    }

    @Test
    fun validateOptional() {
        StringValidator(true).let {
            assertTrue(it.validate("ABCD"))
            assertTrue(it.validate(""))
        }
    }

    @Test
    fun validateLength() {
        StringValidator(false, 2, 5).let {
            assertTrue(it.validate("AB"))
            assertTrue(it.validate("ABCD"))
            assertTrue(it.validate("ABCDE"))
            assertFalse(it.validate("A"))
            assertFalse(it.validate(""))
            assertFalse(it.validate("ABCDEF"))
        }
    }

    @Test
    fun validateLengthOptional() {
        StringValidator(true, 2, 5).let {
            assertTrue(it.validate("AB"))
            assertTrue(it.validate("ABCD"))
            assertTrue(it.validate("ABCDE"))
            assertTrue(it.validate(""))
            assertFalse(it.validate("A"))
            assertFalse(it.validate("ABCDEF"))
        }
    }
}