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
        if (message.isBlank()) throw NmeaException("Message is blank")
        if (!message.startsWith("$")) throw NmeaException("Message should stat with $", 0)
        val index = validate()
        if (index >= 0) throw NmeaException(
            "Cannot parse message at index $index from source $message",
            index
        )
        if (!message.contains("*")) throw NmeaException("Cannot find checksum from message")
        if (computeChecksum() != checksum) throw NmeaException("Message checksum $checksum doesn't match ${computeChecksum()}")
    }

    /**
     * Validate Nmea [data] array.
     * Return -1 if validation is successful or a array index where validation failed.
     */
    protected abstract fun validate(): Int

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

    fun intValidator(
        token: String,
        optional: Boolean = false,
        min: Int = Int.MIN_VALUE,
        max: Int = Int.MAX_VALUE
    ): Boolean {
        if (token.isBlank() && optional) return true
        val i: Int = token.toIntOrNull() ?: return false
        return i in min..max
    }

    fun doubleValidator(
        token: String,
        optional: Boolean = false,
        min: Double = Double.MIN_VALUE,
        max: Double = Double.MAX_VALUE
    ): Boolean {
        if (token.isBlank() && optional) return true
        val i: Double = token.toDoubleOrNull() ?: return false
        return i in min..max
    }

    fun typeValidator(token: String, type: String): Boolean {
        return token.matches(("[A-Z]{2}${type.toUpperCase()}").toRegex())
    }

    fun enumValidator(token: String, chars: Array<Char>, optional: Boolean = false): Boolean {
        if (token.isBlank() && optional) return true
        val s = chars.joinToString(",")
        return token.matches(("[$s]").toRegex())
    }

    fun latitudeValidator(token: String, optional: Boolean = false): Boolean {
        if (token.isBlank() && optional) return true
        return token.matches("^\\d{4}\\.\\d{4}$".toRegex())
    }

    fun longitudeValidator(token: String, optional: Boolean = false): Boolean {
        if (token.isBlank() && optional) return true
        return token.matches("^\\d{5}\\.\\d{4}$".toRegex())
    }

    fun timeValidator(token: String, optional: Boolean = false): Boolean {
        if (token.isBlank() && optional) return true
        return token.matches("^\\d{6}(\\.\\d{2,4})?$".toRegex())
    }

    fun stringValidator(
        token: String,
        optional: Boolean = false,
        min: Int = Int.MIN_VALUE,
        max: Int = Int.MAX_VALUE
    ): Boolean {
        if (token.isBlank() && optional) return true
        return token.length in min..max
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
}