@file:Suppress("LeakingThis")

package net.samystudio.rxlocationmanager.nmea

/**
 * @param message A valid Nmea message starting with "$" followed by message data delimit with "," and
 * ending with checksum "*XX".
 */
abstract class Nmea constructor(val message: String) {
    val data: List<String> by lazy { message.substring(1).split("*")[0].split(",") }
    val type: String by lazy { data[0] }
    val checksum: String by lazy { message.split("*")[1].trimEnd() }

    init {
        if (message.isBlank()) throw NmeaException(BLANK_ERROR_MESSAGE)
        if (!message.startsWith("$")) throw NmeaException(MISSING_DOLLAR_ERROR_MESSAGE, 0)
        val validators = getTokenValidators()
        validators.forEachIndexed { index, tokenValidator ->
            if (index > data.size - 1 || !tokenValidator.validate(
                    data[index]
                )
            ) throw NmeaException(
                getParseErrorMessage(index, tokenValidator::class.java.simpleName, message),
                index
            )
        }
        if (!message.contains("*")) throw NmeaException(CANNOT_FIND_CHECKSUM_ERROR_MESSAGE)
        if (computeChecksum() != checksum) throw NmeaException(
            getChecksumErrorMessage(
                checksum,
                computeChecksum()
            )
        )
    }

    /**
     * Get [TokenValidator] for each [message] tokens. Returned array size should match message data
     * count. Note checksum doesn't count as a token, there is no need to validate it, this is done
     * internally.
     */
    protected abstract fun getTokenValidators(): Array<TokenValidator>

    internal fun computeChecksum() = computeChecksum(message)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Nmea

        if (message != other.message) return false

        return true
    }

    override fun hashCode() = message.hashCode()

    override fun toString() = message

    companion object {
        private const val CANNOT_PARSE_INDEX_ERROR_MESSAGE =
            "Cannot parse message at index %s with validator %s from source %s"
        private const val CHECKSUM_ERROR_MESSAGE = "Message checksum %s doesn't match %s"
        internal const val BLANK_ERROR_MESSAGE = "Message is blank"
        internal const val MISSING_DOLLAR_ERROR_MESSAGE = "Message should stat with $"
        internal const val CANNOT_FIND_CHECKSUM_ERROR_MESSAGE = "Cannot find checksum from message"

        internal fun computeChecksum(message: String): String {
            val data = message.split("*")[0]
            var checksum = 0
            for (char in data) {
                if (char == '$') continue
                if (char == '*') break
                checksum = checksum.xor(char.code)
            }

            return String.format("%02X", checksum)
        }

        internal fun getParseErrorMessage(
            index: Int,
            validatorClassName: String,
            message: String
        ) = CANNOT_PARSE_INDEX_ERROR_MESSAGE.format(index, validatorClassName, message)

        internal fun getChecksumErrorMessage(
            expectedChecksum: String,
            actualChecksum: String
        ) = CHECKSUM_ERROR_MESSAGE.format(expectedChecksum, actualChecksum)
    }
}
