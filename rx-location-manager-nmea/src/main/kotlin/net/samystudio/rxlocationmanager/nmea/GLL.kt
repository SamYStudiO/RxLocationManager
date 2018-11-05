@file:Suppress("unused")

package net.samystudio.rxlocationmanager.nmea

class GLL(message: String) : Nmea(message) {
    val latitude: Double? by lazy {
        convertNmeaLocation(
            data[1],
            LocationDirection.valueOf(data[2], LocationDirection.N)
        )
    }
    val longitude: Double? by lazy {
        convertNmeaLocation(
            data[3],
            LocationDirection.valueOf(data[4], LocationDirection.E)
        )
    }
    val time: String by lazy { data[5] }
    val status: Status? by lazy {
        try {
            Status.valueOf(data[6])
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    override fun validate(): Int {
        for (i in 0..6) {
            if (data.size < i + 1) return i
            val token = data[i]
            // type $__GLL
            if (i == 0 && !token.matches("[A-Z]{2}GLL".toRegex())) return i
            // latitude
            if (i == 1 && !token.matches("(\\d{4}\\.\\d{4})?".toRegex())) return i
            // N or S
            if (i == 2 && !token.matches("([NS])?".toRegex())) return i
            // longitude
            if (i == 3 && !token.matches("(\\d{5}\\.\\d{4})?".toRegex())) return i
            // W or E
            if (i == 4 && !token.matches("([WE])?".toRegex())) return i
            // UTC time hhmmss(.sss)
            if (i == 5 && !token.matches("(\\d{6}(\\.\\d{3})?)?".toRegex())) return i
            // status
            if (i == 6 && !token.matches("[AV]?".toRegex())) return i
        }

        return -1
    }

    enum class Status {
        A, V
    }
}
