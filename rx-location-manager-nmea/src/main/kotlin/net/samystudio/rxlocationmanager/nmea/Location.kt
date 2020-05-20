package net.samystudio.rxlocationmanager.nmea

import android.location.Location
import kotlin.math.abs

/**
 * Convert Nmea latitude/longitude token to [Double]. Return null if conversion failed.
 */
fun convertNmeaLocation(token: String, direction: LocationDirection): Double? {
    if (!LatitudeValidator().validate(token) && !LongitudeValidator().validate(token)) return null

    val sign =
        if (direction == LocationDirection.S || direction == LocationDirection.W) "-" else ""
    val index =
        if (direction == LocationDirection.N || direction == LocationDirection.S) 2 else 3
    val formattedLocation =
        sign + token.substring(0, index) + ":" + token.substring(index)

    return try {
        Location.convert(formattedLocation)
    } catch (e: IllegalArgumentException) {
        null
    }
}

/**
 * Convert [Double] latitude/longitude coordinate to nmea token. Return empty string if conversion
 * failed.
 */
fun convertLocationNmea(coordinate: Double): String {
    return try {
        Location.convert(abs(coordinate), Location.FORMAT_MINUTES).replace(":", "")
    } catch (e: IllegalArgumentException) {
        ""
    }
}

enum class LocationDirection {
    N, S, E, W;

    companion object {
        @JvmStatic
        fun valueOf(value: String, defaultValue: LocationDirection?): LocationDirection? {
            return try {
                valueOf(value)
            } catch (e: IllegalArgumentException) {
                defaultValue
            }
        }
    }
}