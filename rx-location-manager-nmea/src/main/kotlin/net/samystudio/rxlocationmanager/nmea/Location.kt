package net.samystudio.rxlocationmanager.nmea

import android.location.Location

/**
 * Convert Nmea latitude/longitude to [Double]
 */
fun convertNmeaLocation(nmeaLocation: String, direction: LocationDirection): Double? {
    if (!nmeaLocation.matches("\\d{4,5}\\.\\d{4,}".toRegex())) return null

    val sign = if (direction == LocationDirection.S || direction == LocationDirection.W) "-" else ""
    val index = if (direction == LocationDirection.N || direction == LocationDirection.S) 2 else 3
    val formattedLocation =
        sign + nmeaLocation.substring(0, index) + ":" + nmeaLocation.substring(index)

    return try {
        Location.convert(formattedLocation)
    } catch (e: IllegalArgumentException) {
        null
    }
}

enum class LocationDirection {
    N, S, E, W;

    companion object {
        @JvmStatic
        fun valueOf(value: String, defaultValue: LocationDirection): LocationDirection {
            return try {
                LocationDirection.valueOf(value)
            } catch (e: IllegalArgumentException) {
                defaultValue
            }
        }
    }
}