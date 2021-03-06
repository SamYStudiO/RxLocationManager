package net.samystudio.rxlocationmanager

import android.location.GnssMeasurementsEvent

/**
 * @see [GnssMeasurementsEvent.Callback]
 */
sealed class GnssMeasurementsState {
    data class StateEvent(val event: GnssMeasurementsEvent) : GnssMeasurementsState()
    data class StateStatus(val status: Int) : GnssMeasurementsState()
}