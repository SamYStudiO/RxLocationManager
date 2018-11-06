@file:Suppress("LeakingThis", "unused", "MemberVisibilityCanBePrivate")

package net.samystudio.rxlocationmanager.nmea

/**
 * [message] A valid Nmea message starting with "$" followed by message data delimit with "," and
 * ending with checksum "*XX".
 */
abstract class Nmea constructor(val message: String) {
    val data: List<String> by lazy { message.substring(1).split("*")[0].split(",") }
    val type: String by lazy { data[0] }
    val checksum: String by lazy { message.split("*")[1] }

    init {
        if (message.isBlank()) throw NmeaException(BLANK_ERROR_MESSAGE)
        if (!message.startsWith("$")) throw NmeaException(MISSING_DOLLAR_ERROR_MESSAGE, 0)
        val validators = getTokenValidators()
        validators.forEachIndexed { index, tokenValidator ->
            if (index > data.size - 1 || !tokenValidator.validate(
                    data[index]
                )
            ) throw NmeaException(
                getParseErrorMessage(index, tokenValidator::class.java, message),
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
     * Validate Nmea [data] array.
     * Return -1 if validation is successful or a array index where validation failed.
     */
    protected abstract fun getTokenValidators(): Array<TokenValidator>

    internal fun computeChecksum(): String {
        val data = message.split("*")[0]
        var checksum = 0
        for (char in data) {
            if (char == '$') continue
            if (char == '*') break
            checksum = checksum.xor(char.toInt())
        }

        return String.format("%02X", checksum)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Nmea

        if (message != other.message) return false

        return true
    }

    override fun hashCode(): Int {
        return message.hashCode()
    }

    override fun toString(): String {
        return message
    }

    companion object {

        internal const val BLANK_ERROR_MESSAGE = "Message is blank"
        internal const val MISSING_DOLLAR_ERROR_MESSAGE = "Message should stat with $"
        internal const val CANNOT_FIND_CHECKSUM_ERROR_MESSAGE = "Cannot find checksum from message"
        internal fun getParseErrorMessage(
            index: Int,
            validatorClass: Class<out TokenValidator>,
            message: String
        ) =
            "Cannot parse message at index $index with validator ${validatorClass.simpleName} from source $message"

        internal fun getChecksumErrorMessage(
            expectedChecksum: String,
            actualChecksum: String
        ) = "Message checksum $expectedChecksum doesn't match $actualChecksum"
    }
}