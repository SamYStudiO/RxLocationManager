package net.samystudio.rxlocationmanager.nmea

import java.util.*

open class RMC(message: String, throwIfContentInvalid: Boolean = true) :
    Nmea(message, throwIfContentInvalid) {
    val time: String by lazy { data[1] }
    val status: Status by lazy {
        try {
            return@lazy Status.valueOf(data[2])
        } catch (_: IllegalArgumentException) {
        }
        Status.V
    }
    val latitude: Double? by lazy {
        convertNmeaLocation(
            data[3],
            Cardinal.valueOf(data[4], Cardinal.N)!!,
        )
    }
    val longitude: Double? by lazy {
        convertNmeaLocation(
            data[5],
            Cardinal.valueOf(data[6], Cardinal.E)!!,
        )
    }
    val speed: Double? by lazy { data[7].toDoubleOrNull() }
    val angle: Double? by lazy { data[8].toDoubleOrNull() }
    val date: String by lazy { data[9] }
    val magneticVariation: Double? by lazy { data[10].toDoubleOrNull() }
    val magneticVariationDirection: Cardinal? by lazy {
        try {
            return@lazy Cardinal.valueOf(data[11])
        } catch (_: IllegalArgumentException) {
        }
        null
    }
    val faaMode: FAAMode by lazy {
        try {
            return@lazy FAAMode.valueOf(data[12])
        } catch (_: IllegalArgumentException) {
        }
        FAAMode.N
    }
    val navigationalStatus: NavigationalStatus by lazy {
        try {
            return@lazy NavigationalStatus.valueOf(data[13])
        } catch (_: IllegalArgumentException) {
        }
        NavigationalStatus.V
    }

    constructor(
        type: String,
        time: String = "",
        status: Status = Status.V,
        latitude: Double? = null,
        longitude: Double? = null,
        speed: Double? = null,
        angle: Double? = null,
        date: String = "",
        magneticVariation: Double? = null,
        magneticVariationDirection: Cardinal? = null,
        faaMode: FAAMode = FAAMode.N,
        navigationalStatus: NavigationalStatus = NavigationalStatus.V,
    ) : this(
        "$%sRMC,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s".format(
            type.uppercase(Locale.ROOT),
            time,
            status.name,
            latitude?.let { convertLocationNmea(it) } ?: "",
            latitude?.let { if (it < 0) Cardinal.S.name else Cardinal.N.name }
                ?: "",
            longitude?.let { convertLocationNmea(it) } ?: "",
            longitude?.let { if (it < 0) Cardinal.W.name else Cardinal.E.name }
                ?: "",
            speed ?: "",
            angle ?: "",
            date,
            magneticVariation ?: "",
            magneticVariationDirection?.name ?: "",
            faaMode.name,
            navigationalStatus.name,
        ).let { "%s*%s".format(it, computeChecksum(it)) },
    )

    override fun getTokenValidators(): Array<TokenValidator> {
        val optionalDoubleValidator = DoubleValidator(true)

        return arrayOf(
            // type $__RMC
            TypeValidator("RMC"),
            // UTC time hhmmss(.sss)
            TimeValidator(true),
            // Status
            EnumValidator(Status.values().map { it.name.single() }.toCharArray(), false),
            // latitude ddmm.ssss
            LatitudeValidator(true),
            // N or S
            EnumValidator(
                Cardinal.values()
                    .filter { it.cardinalDirection == CardinalDirection.NORTH_SOUTH }
                    .map { it.name.single() }.toCharArray(),
                true,
            ),
            // longitude ddddmm.ssss
            LongitudeValidator(true),
            // W or E
            EnumValidator(
                Cardinal.values()
                    .filter { it.cardinalDirection == CardinalDirection.WEST_EAST }
                    .map { it.name.single() }.toCharArray(),
                true,
            ),
            // speed
            optionalDoubleValidator,
            // angle
            optionalDoubleValidator,
            // date
            DateValidator(true),
            // magneticVariation
            optionalDoubleValidator,
            // magneticVariationDirection
            EnumValidator(
                Cardinal.values()
                    .filter { it.cardinalDirection == CardinalDirection.WEST_EAST }
                    .map { it.name.single() }.toCharArray(),
                true,
            ),
            // FFA mode
            EnumValidator(FAAMode.values().map { it.name.single() }.toCharArray(), false),
            // navigational status
            EnumValidator(NavigationalStatus.values().map { it.name.single() }.toCharArray(), false),
        )
    }

    /**
     * Navigational Status
     * S=Safe
     * C=Caution
     * U=Unsafe
     * V=Void
     */
    enum class NavigationalStatus {
        S,
        C,
        U,
        V,
    }
}
