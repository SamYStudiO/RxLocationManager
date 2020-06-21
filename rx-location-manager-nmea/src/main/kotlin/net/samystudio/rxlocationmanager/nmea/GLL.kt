package net.samystudio.rxlocationmanager.nmea

import java.util.*

class GLL(message: String) : Nmea(message) {
    val latitude by lazy {
        convertNmeaLocation(
            data[1],
            Cardinal.valueOf(data[2], Cardinal.N)!!
        )
    }
    val longitude by lazy {
        convertNmeaLocation(
            data[3],
            Cardinal.valueOf(data[4], Cardinal.E)!!
        )
    }
    val time by lazy { data[5] }
    val status by lazy {
        try {
            Status.valueOf(data[6])
        } catch (e: IllegalArgumentException) {
            null
        }
    }
    val faaMode by lazy {
        try {
            return@lazy FAAMode.valueOf(data[7])
        } catch (e: IllegalArgumentException) {
        }
        FAAMode.N
    }

    constructor(
        type: String,
        latitude: Double? = null,
        longitude: Double? = null,
        time: String = "",
        status: Status? = null,
        faaMode: FAAMode = FAAMode.N
    ) : this(
        "$%sGLL,%s,%s,%s,%s,%s,%s,%s".format(
            type.toUpperCase(Locale.ROOT),
            latitude?.let { convertLocationNmea(it) } ?: "",
            latitude?.let { if (it < 0) Cardinal.S.name else Cardinal.N.name }
                ?: "",
            longitude?.let { convertLocationNmea(it) } ?: "",
            longitude?.let { if (it < 0) Cardinal.W.name else Cardinal.E.name }
                ?: "",
            time,
            status?.name ?: "",
            faaMode.name
        ).let { "%s*%s".format(it, computeChecksum(it)) }
    )

    override fun getTokenValidators(): Array<TokenValidator> {
        return arrayOf(
            // type $__GLL
            TypeValidator("GLL"),
            // latitude ddmm.ssss
            LatitudeValidator(true),
            // N or S
            EnumValidator(
                Cardinal.values()
                    .filter { it.cardinalDirection == CardinalDirection.NORTH_SOUTH }
                    .map { it.name.single() }.toCharArray(),
                true
            ),
            // longitude ddddmm.ssss
            LongitudeValidator(true),
            // W or E
            EnumValidator(
                Cardinal.values()
                    .filter { it.cardinalDirection == CardinalDirection.WEST_EAST }
                    .map { it.name.single() }.toCharArray(),
                true
            ),
            // UTC time hhmmss(.sss)
            TimeValidator(true),
            // status
            EnumValidator(Status.values().map { it.name.single() }.toCharArray(), true),
            // FFA mode
            EnumValidator(FAAMode.values().map { it.name.single() }.toCharArray(), false)
        )
    }
}
