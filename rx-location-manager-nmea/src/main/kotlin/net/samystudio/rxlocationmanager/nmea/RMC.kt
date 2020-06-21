package net.samystudio.rxlocationmanager.nmea

import java.util.*

open class RMC(message: String) : Nmea(message) {
    val time: String by lazy { data[1] }
    val status: Status by lazy {
        try {
            return@lazy Status.valueOf(data[2])
        } catch (e: IllegalArgumentException) {
        }
        Status.V
    }
    val latitude: Double? by lazy {
        convertNmeaLocation(
            data[3],
            LocationDirection.valueOf(data[4], LocationDirection.N)!!
        )
    }
    val longitude: Double? by lazy {
        convertNmeaLocation(
            data[5],
            LocationDirection.valueOf(data[6], LocationDirection.E)!!
        )
    }
    val speed: Double? by lazy { data[7].toDoubleOrNull() }
    val angle: Double? by lazy { data[8].toDoubleOrNull() }
    val date: String by lazy { data[9] }
    val magneticVariation: Double? by lazy { data[10].toDoubleOrNull() }
    val magneticVariationDirection: LocationDirection? by lazy {
        try {
            return@lazy LocationDirection.valueOf(data[11])
        } catch (e: IllegalArgumentException) {
        }
        null
    }
    val mode: Mode by lazy {
        try {
            return@lazy Mode.valueOf(data[12])
        } catch (e: IllegalArgumentException) {
        }
        Mode.N
    }
    val navigationalStatus: NavigationalStatus by lazy {
        try {
            return@lazy NavigationalStatus.valueOf(data[13])
        } catch (e: IllegalArgumentException) {
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
        magneticVariationDirection: LocationDirection? = null,
        mode: Mode = Mode.N,
        navigationalStatus: NavigationalStatus = NavigationalStatus.V
    ) : this(
        "$%sRMC,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s".format(
            type.toUpperCase(Locale.ROOT),
            time,
            status.name,
            latitude?.let { convertLocationNmea(it) } ?: "",
            latitude?.let { if (it < 0) LocationDirection.S.name else LocationDirection.N.name }
                ?: "",
            longitude?.let { convertLocationNmea(it) } ?: "",
            longitude?.let { if (it < 0) LocationDirection.W.name else LocationDirection.E.name }
                ?: "",
            speed ?: "",
            angle ?: "",
            date,
            magneticVariation ?: "",
            magneticVariationDirection?.name ?: "",
            mode.name,
            navigationalStatus.name
        ).let { "%s*%s".format(it, computeChecksum(it)) }
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
            EnumValidator(charArrayOf('N', 'S'), true),
            // longitude ddddmm.ssss
            LongitudeValidator(true),
            // W or E
            EnumValidator(charArrayOf('W', 'E'), true),
            // speed
            optionalDoubleValidator,
            // angle
            optionalDoubleValidator,
            // date
            DateValidator(true),
            // magneticVariation
            optionalDoubleValidator,
            // magneticVariationDirection
            EnumValidator(charArrayOf('W', 'E'), true),
            // mode
            EnumValidator(Mode.values().map { it.name.single() }.toCharArray(), false),
            // navigational status
            EnumValidator(NavigationalStatus.values().map { it.name.single() }.toCharArray(), false)
        )
    }

    /**
     * A=Active
     * V=Void
     */
    enum class Status {
        A,
        V
    }

    /**
     * Mode Indicator
     * A=Autonomous
     * D=Differential
     * E=Estimated
     * F=Float RTK
     * M=Manual input
     * N=No fix
     * P=Precise
     * R=Real time kinematic
     * S=Simulator
     */
    enum class Mode {
        A, D, E, F, M, N, P, R, S
    }

    /**
     * Navigational Status
     * S=Safe
     * C=Caution
     * U=Unsafe
     * V=Void
     */
    enum class NavigationalStatus {
        S, C, U, V
    }
}
