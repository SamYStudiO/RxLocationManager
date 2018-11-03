package net.samystudio.rxlocationmanager

import android.location.LocationManager

enum class Provider {
    /**
     * [LocationManager.NETWORK_PROVIDER]
     */
    NETWORK,
    /**
     * [LocationManager.GPS_PROVIDER]
     */
    GPS,
    /**
     * [LocationManager.PASSIVE_PROVIDER]
     */
    PASSIVE
}