@file:Suppress("DEPRECATION")

package net.samystudio.rxlocationmanager

import android.location.GpsStatus
import android.location.OnNmeaMessageListener

/**
 * [OnNmeaMessageListener]
 * [GpsStatus.NmeaListener]
 */
data class NmeaEvent(val message: String, val timestamp: Long)