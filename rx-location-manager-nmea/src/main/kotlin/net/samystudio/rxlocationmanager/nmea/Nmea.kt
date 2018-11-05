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