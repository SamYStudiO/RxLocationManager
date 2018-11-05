@file:Suppress("unused")

package net.samystudio.rxlocationmanager.nmea

class GGA(message: String) : Nmea(message) {
    val time: String by lazy { data[1] }
    val latitude: Double? by lazy {
        convertNmeaLocation(
            data[2],
            LocationDirection.valueOf(data[3], LocationDirection.N)
        )
    }
    val longitude: Double? by lazy {
        convertNmeaLocation(
            data[4],
            LocationDirection.valueOf(data[5], LocationDirection.E)
        )
    }
    val quality: Quality by lazy {
        try {
            return@lazy Quality.values()[data[6].toInt()]
        } catch (e: NumberFormatException) {
        } catch (e: ArrayIndexOutOfBoundsException) {
        }
        Quality.NO_FIX
    }
    val satelliteCount: Int by lazy { data[7].toIntOrNull() ?: 0 }
    val horizontalDilutionOfPrecision: Double? by lazy { data[8].toDoubleOrNull() }
    val altitude: Double? by lazy { data[9].toDoubleOrNull() }
    val ellipsoidalOffset: Double? by lazy { data[11].toDoubleOrNull() }
    val differentialGpsAge: Double? by lazy { data[13].toDoubleOrNull() }
    val differentialGpsStationId: String by lazy { data[14] }

    override fun validate(): Int {
        for (i in 0..14) {
            if (data.size < i + 1) return i
            val token = data[i]
            // type $__GGA
            if (i == 0 && !token.matches("[A-Z]{2}GGA".toRegex())) return i
            // UTC time hhmmss(.sss)
            if (i == 1 && !token.matches("(\\d{6}(\\.\\d{3})?)?".toRegex())) return i
            // latitude ddmm.sssss
            if (i == 2 && !token.matches("(\\d{4}\\.\\d{4})?".toRegex())) return i
            // N or S
            if (i == 3 && !token.matches("([NS])?".toRegex())) return i
            // longitude dddmm.sssss
            if (i == 4 && !token.matches("(\\d{5}\\.\\d{4})?".toRegex())) return i
            // W or E
            if (i == 5 && !token.matches("([WE])?".toRegex())) return i
            // quality 0, 1 or 2 (not fixed, fixed, differential fixed)
            if (i == 6 && !token.matches("([012])?".toRegex())) return i
            // satellites count 0-12
            if (i == 7 && !token.matches("([01][012]?)?".toRegex())) return i
            // horizontal dilution of precision
            if (i == 8 && !token.matches("(\\d+\\.\\d+)?".toRegex())) return i
            // altitude geoid (mean sea level) in meter
            if (i == 9 && !token.matches("(-?\\d+\\.\\d+)?".toRegex())) return i
            // altitude unit M
            if (i == 10 && !token.matches("(M)?".toRegex())) return i
            // WGS-84 earth ellipsoid offset
            if (i == 11 && !token.matches("(-?\\d+\\.\\d+)?".toRegex())) return i
            // ellipsoid offset unit M
            if (i == 12 && !token.matches("(M)?".toRegex())) return i
            // age of differential GPS data (seconds)
            if (i == 13 && !token.matches("(\\d+\\.\\d+)?".toRegex())) return i
            // station
            if (i == 14 && !token.matches("([01]\\d{3})?".toRegex())) return i
        }

        return -1
    }

    enum class Quality {
        NO_FIX, FIX, DIFFERENTIAL_FIX;

        companion object {
            @JvmStatic
            fun valueOfWithDefault(value: String, defaultValue: Quality = Quality.NO_FIX): Quality {
                return try {
                    Quality.valueOf(value)
                } catch (e: IllegalArgumentException) {
                    defaultValue
                }
            }
        }
    }
}
