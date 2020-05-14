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

    override fun getTokenValidators(): Array<TokenValidator> {
        return arrayOf(
            // type $__GLL
            TypeValidator("GLL"),
            // latitude ddmm.ssss
            LatitudeValidator(true),
            // N or S
            EnumValidator(charArrayOf('N', 'S'), true),
            // longitude ddddmm.ssss
            LongitudeValidator(true),
            // W or E
            EnumValidator(charArrayOf('W', 'E'), true),
            // UTC time hhmmss(.sss)
            TimeValidator(true),
            // status A (valid) or V (invalid)
            EnumValidator(charArrayOf('A', 'V'), true)
        )
    }

    enum class Status {
        A, V
    }
}
