package net.samystudio.rxlocationmanager

import android.location.Location
import android.location.LocationListener

/**
 * @see [LocationListener]
 */
sealed class LocationUpdatesState {
    data class StateProviderEnabled(
        val provider: Provider,
    ) : LocationUpdatesState()

    data class StateProviderDisabled(
        val provider: Provider,
    ) : LocationUpdatesState()

    data class StateLocationChanged(
        val location: Location,
    ) : LocationUpdatesState()
}
