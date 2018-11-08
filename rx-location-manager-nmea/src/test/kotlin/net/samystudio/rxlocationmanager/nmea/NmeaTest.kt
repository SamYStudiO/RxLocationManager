package net.samystudio.rxlocationmanager.nmea

import org.junit.Assert.assertEquals
import org.junit.Test

class NmeaTest {
    @Test
    fun computeChecksum() {
        val nmea = object :
            Nmea("\$GPGGA,002153.000,3342.6618,N,11751.3858,W,1,10,1.2,27.0,M,-34.2,M,,0000*5E") {
            override fun getTokenValidators(): Array<TokenValidator> {
                return arrayOf(object : TokenValidator {
                    override fun validate(token: String): Boolean {
                        return true
                    }
                })
            }
        }
        assertEquals("5E", nmea.computeChecksum())
    }

    @Test
    fun blankError() {
        val expected = NmeaException(Nmea.BLANK_ERROR_MESSAGE)
        var result: NmeaException? = null
        try {
            object :
                Nmea(" \n\t") {
                override fun getTokenValidators(): Array<TokenValidator> {
                    return arrayOf(object : TokenValidator {
                        override fun validate(token: String): Boolean {
                            return true
                        }
                    })
                }
            }
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }

    @Test
    fun dollarMissingError() {
        val expected = NmeaException(Nmea.MISSING_DOLLAR_ERROR_MESSAGE, 0)
        var result: NmeaException? = null
        try {
            object :
                Nmea("GPGGA,002153.000,3342.6618,N,11751.3858,W,1,10,1.2,27.0,M,-34.2,M,,00005E") {
                override fun getTokenValidators(): Array<TokenValidator> {
                    return arrayOf(object : TokenValidator {
                        override fun validate(token: String): Boolean {
                            return true
                        }
                    })
                }
            }
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }

    @Test
    fun checksumMissingError() {
        val expected = NmeaException(Nmea.CANNOT_FIND_CHECKSUM_ERROR_MESSAGE)
        var result: NmeaException? = null
        try {
            object :
                Nmea("\$GPGGA,002153.000,3342.6618,N,11751.3858,W,1,10,1.2,27.0,M,-34.2,M,,0000") {
                override fun getTokenValidators(): Array<TokenValidator> {
                    return arrayOf(object : TokenValidator {
                        override fun validate(token: String): Boolean {
                            return true
                        }
                    })
                }
            }
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }

    @Test
    fun checksumDoesNotNotMatchError() {
        val expected = NmeaException(Nmea.getChecksumErrorMessage("55", "5E"))
        var result: NmeaException? = null
        try {
            object :
                Nmea("\$GPGGA,002153.000,3342.6618,N,11751.3858,W,1,10,1.2,27.0,M,-34.2,M,,0000*55") {
                override fun getTokenValidators(): Array<TokenValidator> {
                    return arrayOf(object : TokenValidator {
                        override fun validate(token: String): Boolean {
                            return true
                        }
                    })
                }
            }
        } catch (e: NmeaException) {
            result = e
        }
        assertEquals(expected, result)
    }
}