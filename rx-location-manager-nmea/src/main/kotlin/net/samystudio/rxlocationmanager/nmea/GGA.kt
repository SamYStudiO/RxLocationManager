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
            if (i == 0 && !typeValidator(token, "GGA")) return i
            // UTC time hhmmss(.sss)
            if (i == 1 && !timeValidator(token)) return i
            // latitude ddmm.ssss
            if (i == 2 && !latitudeValidator(token, true)) return i
            // N or S
            if (i == 3 && !enumValidator(token, arrayOf('N', 'S'), true)) return i
            // longitude ddddmm.ssss
            if (i == 4 && !longitudeValidator(token, true)) return i
            // W or E
            if (i == 5 && !enumValidator(token, arrayOf('W', 'E'), true)) return i
            // quality 0, 1 or 2 (not fixed, fixed, differential fixed)
            if (i == 6 && !enumValidator(token, arrayOf('0', '1', '2'), true)) return i
            // satellites count 0-12
            if (i == 7 && !intValidator(token, true, 0, 12)) return i
            // horizontal dilution of precision
            if (i == 8 && !doubleValidator(token, true)) return i
            // altitude geoid (mean sea level) in meter
            if (i == 9 && !doubleValidator(token, true)) return i
            // altitude unit M
            if (i == 10 && !enumValidator(token, arrayOf('M'), true)) return i
            // WGS-84 earth ellipsoid offset
            if (i == 11 && !doubleValidator(token, true)) return i
            // ellipsoid offset unit M
            if (i == 12 && !enumValidator(token, arrayOf('M'), true)) return i
            // age of differential GPS data (seconds)
            if (i == 13 && !doubleValidator(token, true)) return i
            // station
            if (i == 14 && !stringValidator(token, true, 4, 4)) return i
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
