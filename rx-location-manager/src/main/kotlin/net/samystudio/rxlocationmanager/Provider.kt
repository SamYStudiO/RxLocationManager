package net.samystudio.rxlocationmanager

import android.location.LocationManager

/**
 * [LocationManager.NETWORK_PROVIDER]
 * [LocationManager.GPS_PROVIDER]
 * [LocationManager.PASSIVE_PROVIDER]
 */
enum class Provider {
    NETWORK, GPS, PASSIVE
}