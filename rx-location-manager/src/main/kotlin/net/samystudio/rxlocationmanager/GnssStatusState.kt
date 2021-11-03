package net.samystudio.rxlocationmanager

import android.location.GnssStatus

/**
 * @see [GnssStatus.Callback]
 */
sealed class GnssStatusState {
    object StateStarted : GnssStatusState()
    object StateStopped : GnssStatusState()
    data class StateFirstFix(val ttffMillis: Int? = null) : GnssStatusState()
    data class StateChanged(val status: GnssStatus? = null) : GnssStatusState()
}
