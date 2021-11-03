package net.samystudio.rxlocationmanager.nmea

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TypeValidatorTest {
    @Test
    fun validate() {
        assertTrue(TypeValidator("GGA").validate("GPGGA"))
        assertFalse(TypeValidator("GGA").validate("GPGG"))
    }
}
