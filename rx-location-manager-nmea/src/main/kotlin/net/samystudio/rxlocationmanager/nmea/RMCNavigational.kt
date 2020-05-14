package net.samystudio.rxlocationmanager.nmea

class RMCNavigational(message: String) : RMCTiming(message) {
    val navigationalStatus: NavigationalStatus by lazy {
        try {
            return@lazy NavigationalStatus.valueOf(data[13])
        } catch (e: IllegalArgumentException) {
        }
        NavigationalStatus.V
    }

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
