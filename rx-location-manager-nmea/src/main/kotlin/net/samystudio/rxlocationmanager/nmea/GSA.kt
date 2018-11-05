@file:Suppress("unused")

package net.samystudio.rxlocationmanager.nmea

class GSA(message: String) : Nmea(message) {
    val mode1: Mode1? by lazy {
        try {
            Mode1.valueOf(data[1])
        } catch (e: IllegalArgumentException) {
            null
        }
    }
    val mode2: Mode2? by lazy {
        try {
            return@lazy Mode2.values()[data[2].toInt() - 1]
        } catch (e: NumberFormatException) {

        } catch (e: ArrayIndexOutOfBoundsException) {

        }
        null
    }
    val satellite1: Int? by lazy { data[3].toIntOrNull() }
    val satellite2: Int? by lazy { data[4].toIntOrNull() }
    val satellite3: Int? by lazy { data[5].toIntOrNull() }
    val satellite4: Int? by lazy { data[6].toIntOrNull() }
    val satellite5: Int? by lazy { data[7].toIntOrNull() }
    val satellite6: Int? by lazy { data[8].toIntOrNull() }
    val satellite7: Int? by lazy { data[9].toIntOrNull() }
    val satellite8: Int? by lazy { data[10].toIntOrNull() }
    val satellite9: Int? by lazy { data[11].toIntOrNull() }
    val satellite10: Int? by lazy { data[12].toIntOrNull() }
    val satellite11: Int? by lazy { data[13].toIntOrNull() }
    val satellite12: Int? by lazy { data[14].toIntOrNull() }
    val positionDilutionOfPrecision: Double? by lazy { data[15].toDoubleOrNull() }
    val horizontalDilutionOfPrecision: Double? by lazy { data[16].toDoubleOrNull() }
    val verticalDilutionOfPrecision: Double? by lazy { data[17].toDoubleOrNull() }

    override fun validate(): Int {
        for (i in 0..17) {
            if (data.size < i + 1) return i
            val token = data[i]
            // type $__GSA
            if (i == 0 && !token.matches("[A-Z]{2}GSA".toRegex())) return i
            // mode1
            if (i == 1 && !token.matches("([AM])?".toRegex())) return i
            // mode2
            if (i == 2 && !token.matches("([123])?".toRegex())) return i
            // satellite 1
            if (i == 3 && !token.matches("(\\d{1,3})?".toRegex())) return i
            // satellite 2
            if (i == 4 && !token.matches("(\\d{1,3})?".toRegex())) return i
            // satellite 3
            if (i == 5 && !token.matches("(\\d{1,3})?".toRegex())) return i
            // satellite 4
            if (i == 6 && !token.matches("(\\d{1,3})?".toRegex())) return i
            // satellite 5
            if (i == 7 && !token.matches("(\\d{1,3})?".toRegex())) return i
            // satellite 6
            if (i == 8 && !token.matches("(\\d{1,3})?".toRegex())) return i
            // satellite 7
            if (i == 9 && !token.matches("(\\d{1,3})?".toRegex())) return i
            // satellite 8
            if (i == 10 && !token.matches("(\\d{1,3})?".toRegex())) return i
            // satellite 9
            if (i == 11 && !token.matches("(\\d{1,3})?".toRegex())) return i
            // satellite 10
            if (i == 12 && !token.matches("(\\d{1,3})?".toRegex())) return i
            // satellite 11
            if (i == 13 && !token.matches("(\\d{1,3})?".toRegex())) return i
            // satellite 12
            if (i == 14 && !token.matches("(\\d{1,3})?".toRegex())) return i
            // position dilution of precision
            if (i == 15 && !token.matches("(\\d+\\.?\\d+)?".toRegex())) return i
            // horizontal dilution of precision
            if (i == 16 && !token.matches("(\\d+\\.?\\d+)?".toRegex())) return i
            // vertical dilution of precision
            if (i == 17 && !token.matches("(\\d+\\.?\\d+)?".toRegex())) return i
        }

        return -1
    }

    enum class Mode1 {
        A, M;
    }

    enum class Mode2 {
        FIX_NOT_AVAILABLE, FIX_2D, FIX_3D
    }
}
