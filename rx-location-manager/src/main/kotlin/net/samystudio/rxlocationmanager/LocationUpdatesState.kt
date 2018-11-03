package net.samystudio.rxlocationmanager

import android.location.Location
import android.os.Bundle

sealed class LocationUpdatesState {
    data class StateProviderEnabled(val provider: Provider) : LocationUpdatesState()
    data class StateProviderDisabled(val provider: Provider) : LocationUpdatesState()
    data class StateStatusChanged(
        val provider: Provider,
        val status: Int,
        val extras: Bundle?
    ) :
        LocationUpdatesState()

    data class StateLocationChanged(val location: Location) : LocationUpdatesState()
}