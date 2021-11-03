package net.samystudio.rxlocationmanager

import android.location.LocationManager

enum class Provider {
    /**
     * @see [LocationManager.NETWORK_PROVIDER]
     */
    NETWORK,
    /**
     * @see [LocationManager.GPS_PROVIDER]
     */
    GPS,
    /**
     * @see [LocationManager.PASSIVE_PROVIDER]
     */
    PASSIVE
}
