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
            if (i == 0 && !typeValidator(token, "GGL")) return i
            // latitude ddmm.ssss
            if (i == 1 && !latitudeValidator(token, true)) return i
            // N or S
            if (i == 2 && !enumValidator(token, arrayOf('N', 'S'), true)) return i
            // longitude ddddmm.ssss
            if (i == 3 && !longitudeValidator(token, true)) return i
            // W or E
            if (i == 4 && !enumValidator(token, arrayOf('W', 'E'), true)) return i
            // UTC time hhmmss(.sss)
            if (i == 5 && !timeValidator(token, true)) return i
            // status A (valid) or V (invalid)
            if (i == 6 && !enumValidator(token, arrayOf('A', 'V'), true)) return i
        }

        return -1
    }

    enum class Status {
        A, V
    }
}
