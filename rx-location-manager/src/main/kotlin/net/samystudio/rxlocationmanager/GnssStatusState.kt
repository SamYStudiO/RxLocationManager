package net.samystudio.rxlocationmanager

import androidx.core.location.GnssStatusCompat

/**
 * @see [GnssStatusCompat.Callback]
 */
sealed class GnssStatusState {
    data object StateStarted : GnssStatusState()
    data object StateStopped : GnssStatusState()
    data class StateFirstFix(val ttffMillis: Int? = null) : GnssStatusState()
    data class StateChanged(val status: GnssStatusCompat? = null) : GnssStatusState()
}
