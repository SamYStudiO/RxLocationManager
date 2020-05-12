package net.samystudio.rxlocationmanager

import android.location.GnssNavigationMessage

/**
 * @see [GnssNavigationMessage.Callback]
 */
sealed class GnssNavigationMessageState {
    data class StateEvent(val event: GnssNavigationMessage) : GnssNavigationMessageState()
    data class StateStatus(val status: Int) : GnssNavigationMessageState()
}