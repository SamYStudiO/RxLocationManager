package net.samystudio.rxlocationmanager.nmea

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class NmeaTest {
    @Test
    fun computeChecksum() {
        val nmea =
            object :
                Nmea("\$GPGGA,002153.000,3342.6618,N,11751.3858,W,1,10,1.2,27.0,M,-34.2,M,,0000*5E") {
                override fun getTokenValidators(): Array<TokenValidator> =
                    arrayOf(
                        object : TokenValidator {
                            override fun validate(token: String): Boolean = true
                        },
                    )
            }
        assertEquals("5E", nmea.computeChecksum())
    }

    @Test
    fun blankError() {
        val message = " \n\t"
        val errorMessage = Nmea.BLANK_ERROR_MESSAGE

        assertThrows(errorMessage, NmeaException::class.java) {
            object : Nmea(message) {
                override fun getTokenValidators(): Array<TokenValidator> =
                    arrayOf(
                        object : TokenValidator {
                            override fun validate(token: String): Boolean = true
                        },
                    )
            }
        }
    }

    @Test
    fun dollarMissingError() {
        val message = "\$GPGGA,002153.000,3342.6618,N,11751.3858,W,1,10,1.2,27.0,M,-34.2,M,,00005E"
        val errorMessage = Nmea.getMissingDollarErrorMessage(message)

        assertThrows(errorMessage, NmeaException::class.java) {
            object :
                Nmea("GPGGA,002153.000,3342.6618,N,11751.3858,W,1,10,1.2,27.0,M,-34.2,M,,00005E") {
                override fun getTokenValidators(): Array<TokenValidator> =
                    arrayOf(
                        object : TokenValidator {
                            override fun validate(token: String): Boolean = true
                        },
                    )
            }
        }
    }

    @Test
    fun checksumMissingError() {
        val message = "\$GPGGA,002153.000,3342.6618,N,11751.3858,W,1,10,1.2,27.0,M,-34.2,M,,0000"
        val errorMessage = Nmea.getCannotFindChecksumErrorMessage(message)

        assertThrows(errorMessage, NmeaException::class.java) {
            object :
                Nmea("\$GPGGA,002153.000,3342.6618,N,11751.3858,W,1,10,1.2,27.0,M,-34.2,M,,0000") {
                override fun getTokenValidators(): Array<TokenValidator> =
                    arrayOf(
                        object : TokenValidator {
                            override fun validate(token: String): Boolean = true
                        },
                    )
            }
        }
    }

    @Test
    fun checksumDoesNotNotMatchError() {
        val message = "\$GPGGA,002153.000,3342.6618,N,11751.3858,W,1,10,1.2,27.0,M,-34.2,M,,0000*55"
        val errorMessage = Nmea.getChecksumErrorMessage("55", "5E", message)

        assertThrows(errorMessage, NmeaException::class.java) {
            object : Nmea(message) {
                override fun getTokenValidators(): Array<TokenValidator> =
                    arrayOf(
                        object : TokenValidator {
                            override fun validate(token: String): Boolean = true
                        },
                    )
            }
        }
    }
}
