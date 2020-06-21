package net.samystudio.rxlocationmanager.nmea

import java.util.*

class GLL(message: String) : Nmea(message) {
    val latitude: Double? by lazy {
        convertNmeaLocation(
            data[1],
            LocationDirection.valueOf(data[2], LocationDirection.N)!!
        )
    }
    val longitude: Double? by lazy {
        convertNmeaLocation(
            data[3],
            LocationDirection.valueOf(data[4], LocationDirection.E)!!
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

    constructor(
        type: String,
        latitude: Double? = null,
        longitude: Double? = null,
        time: String = "",
        status: Status? = null
    ) : this(
        "$%sGLL,%s,%s,%s,%s,%s,%s".format(
            type.toUpperCase(Locale.ROOT),
            latitude?.let { convertLocationNmea(it) } ?: "",
            latitude?.let { if (it < 0) LocationDirection.S.name else LocationDirection.N.name }
                ?: "",
            longitude?.let { convertLocationNmea(it) } ?: "",
            longitude?.let { if (it < 0) LocationDirection.W.name else LocationDirection.E.name }
                ?: "",
            time,
            status?.name ?: ""
        ).let { "%s*%s".format(it, computeChecksum(it)) }
    )

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
            EnumValidator(Status.values().map { it.name.single() }.toCharArray(), true)
        )
    }

    enum class Status {
        A, V
    }
}
