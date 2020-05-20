package net.samystudio.rxlocationmanager.nmea

import java.util.*

class RMCNavigational(message: String) : RMCTiming(message) {
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

    override fun getTokenValidators(): Array<TokenValidator> =
        super.getTokenValidators()
            .toMutableList()
            .apply {
                add(
                    // navigational status
                    EnumValidator(charArrayOf('S', 'C', 'U', 'V'), false)
                )
            }
            .toTypedArray()

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
