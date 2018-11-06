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

    override fun getTokenValidators(): Array<TokenValidator> {
        val satelliteValidator = IntValidator(true, 0, 999)
        val optionalDoubleValidator = DoubleValidator(true)

        return arrayOf(
            // type $__GSA
            TypeValidator("GSA"),
            // mode1 A (automatic) or M (manual)
            EnumValidator(arrayOf('A', 'M'), true),
            // mode2 1 (fix not available) 2 (2D) or 3 (3D)
            EnumValidator(arrayOf('1', '2', '3'), true),
            // satellite 1
            satelliteValidator,
            // satellite 2
            satelliteValidator,
            // satellite 3
            satelliteValidator,
            // satellite 4
            satelliteValidator,
            // satellite 5
            satelliteValidator,
            // satellite 6
            satelliteValidator,
            // satellite 7
            satelliteValidator,
            // satellite 8
            satelliteValidator,
            // satellite 9
            satelliteValidator,
            // satellite 10
            satelliteValidator,
            // satellite 11
            satelliteValidator,
            // satellite 12
            satelliteValidator,
            // position dilution of precision
            optionalDoubleValidator,
            // horizontal dilution of precision
            optionalDoubleValidator,
            // vertical dilution of precision
            optionalDoubleValidator
        )
    }

    enum class Mode1 {
        A, M;
    }

    enum class Mode2 {
        FIX_NOT_AVAILABLE, FIX_2D, FIX_3D
    }
}
