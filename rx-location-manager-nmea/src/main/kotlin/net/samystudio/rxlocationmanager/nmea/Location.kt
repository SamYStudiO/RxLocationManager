package net.samystudio.rxlocationmanager.nmea

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.math.abs
import kotlin.math.floor

/**
 * Convert Nmea latitude/longitude token to [Double]. Return null if conversion failed.
 */
fun convertNmeaLocation(token: String, direction: Cardinal): Double? {
    if (!LatitudeValidator().validate(token) && !LongitudeValidator().validate(token)) return null

    val sign =
        if (direction == Cardinal.S || direction == Cardinal.W) "-" else ""
    val index =
        if (direction == Cardinal.N || direction == Cardinal.S) 2 else 3
    val formattedLocation =
        sign + token.substring(0, index) + ":" + token.substring(index)

    return try {
        convert(formattedLocation)
    } catch (e: IllegalArgumentException) {
        null
    }
}

/**
 * Extracted from android Location class.
 */
fun convertLocationNmea(coordinate: Double) =
    try {
        convert(abs(coordinate)).replace(":", "")
    } catch (e: IllegalArgumentException) {
        ""
    }

enum class Cardinal(internal val cardinalDirection: CardinalDirection) {
    N(CardinalDirection.NORTH_SOUTH),
    S(CardinalDirection.NORTH_SOUTH),
    E(CardinalDirection.WEST_EAST),
    W(CardinalDirection.WEST_EAST);

    companion object {
        @JvmStatic
        fun valueOf(value: String, defaultValue: Cardinal?): Cardinal? {
            return try {
                valueOf(value)
            } catch (e: IllegalArgumentException) {
                defaultValue
            }
        }
    }
}

internal enum class CardinalDirection {
    NORTH_SOUTH,
    WEST_EAST
}

/**
 * Extracted from android Location class.
 */
private fun convert(coordinate: Double): String {
    var c = coordinate
    require(!(c < -180.0 || c > 180.0 || java.lang.Double.isNaN(c))) { "coordinate=$c" }
    val sb = StringBuilder()

    // Handle negative values
    if (c < 0) {
        sb.append('-')
        c = -c
    }
    val df = DecimalFormat(
        "###.#####",
        DecimalFormatSymbols.getInstance().apply {
            decimalSeparator = '.'
        })
    val degrees = floor(c).toInt()
    sb.append(degrees)
    sb.append(':')
    c -= degrees.toDouble()
    c *= 60.0
    sb.append(df.format(c))
    return sb.toString()
}

/**
 * Extracted from android Location class.
 */
private fun convert(coordinate: String): Double {
    var c = coordinate
    var negative = false
    if (c[0] == '-') {
        c = c.substring(1)
        negative = true
    }
    val st = StringTokenizer(c, ":")
    val tokens = st.countTokens()
    require(tokens >= 1) { "coordinate=$c" }
    return try {
        val degrees = st.nextToken()
        var value: Double
        if (tokens == 1) {
            value = degrees.toDouble()
            return if (negative) -value else value
        }
        val minutes = st.nextToken()
        val deg = degrees.toInt()
        val min: Double
        var sec = 0.0
        var secPresent = false
        if (st.hasMoreTokens()) {
            min = minutes.toInt().toDouble()
            val seconds = st.nextToken()
            sec = seconds.toDouble()
            secPresent = true
        } else {
            min = minutes.toDouble()
        }
        val isNegative180 = negative && deg == 180 &&
                min == 0.0 && sec == 0.0

        // deg must be in [0, 179] except for the case of -180 degrees
        require(!(deg < 0.0 || deg > 179 && !isNegative180)) { "coordinate=$c" }

        // min must be in [0, 59] if seconds are present, otherwise [0.0, 60.0)
        require(!(min < 0 || min >= 60 || secPresent && min > 59)) {
            "coordinate=$c"
        }

        // sec must be in [0.0, 60.0)
        require(!(sec < 0 || sec >= 60)) {
            "coordinate=$c"
        }
        value = deg * 3600.0 + min * 60.0 + sec
        value /= 3600.0
        if (negative) -value else value
    } catch (nfe: NumberFormatException) {
        throw java.lang.IllegalArgumentException("coordinate=$c")
    }
}
