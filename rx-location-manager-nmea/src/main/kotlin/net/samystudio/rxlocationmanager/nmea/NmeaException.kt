package net.samystudio.rxlocationmanager.nmea

class NmeaException(message: String, val index: Int = -1) : Exception(message) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NmeaException

        if (message != other.message) return false
        if (index != other.index) return false

        return true
    }

    override fun hashCode(): Int {
        var result = message.hashCode()
        result = 31 * result + index
        return result
    }
}