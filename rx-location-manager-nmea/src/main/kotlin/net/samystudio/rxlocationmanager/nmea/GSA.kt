package net.samystudio.rxlocationmanager.nmea

import java.util.*

class GSA(message: String) : Nmea(message) {
    val mode1: Mode1 by lazy {
        try {
            Mode1.valueOf(data[1])
        } catch (e: IllegalArgumentException) {
            Mode1.A
        }
    }
    val mode2: Mode2 by lazy {
        try {
            return@lazy Mode2.values()[data[2].toInt() - 1]
        } catch (e: NumberFormatException) {
        } catch (e: ArrayIndexOutOfBoundsException) {
        }
        Mode2.FIX_NOT_AVAILABLE
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
    val satelliteCount: Int by lazy {
        listOfNotNull(
            satellite1,
            satellite2,
            satellite3,
            satellite4,
            satellite5,
            satellite6,
            satellite7,
            satellite8,
            satellite9,
            satellite10,
            satellite11,
            satellite12
        ).count()
    }

    constructor(
        type: String,
        mode1: Mode1 = Mode1.A,
        mode2: Mode2 = Mode2.FIX_NOT_AVAILABLE,
        satellite1: Int? = null,
        satellite2: Int? = null,
        satellite3: Int? = null,
        satellite4: Int? = null,
        satellite5: Int? = null,
        satellite6: Int? = null,
        satellite7: Int? = null,
        satellite8: Int? = null,
        satellite9: Int? = null,
        satellite10: Int? = null,
        satellite11: Int? = null,
        satellite12: Int? = null,
        horizontalDilutionOfPrecision: Double? = null,
        positionDilutionOfPrecision: Double? = null,
        verticalDilutionOfPrecision: Double? = null
    ) : this(
        "$%sGSA,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s".format(
            type.toUpperCase(Locale.ROOT),
            mode1.name,
            Mode2.values().indexOf(mode2) + 1,
            satellite1 ?: "",
            satellite2 ?: "",
            satellite3 ?: "",
            satellite4 ?: "",
            satellite5 ?: "",
            satellite6 ?: "",
            satellite7 ?: "",
            satellite8 ?: "",
            satellite9 ?: "",
            satellite10 ?: "",
            satellite11 ?: "",
            satellite12 ?: "",
            horizontalDilutionOfPrecision ?: "",
            positionDilutionOfPrecision ?: "",
            verticalDilutionOfPrecision ?: ""
        ).let { "%s*%s".format(it, computeChecksum(it)) }
    )

    override fun getTokenValidators(): Array<TokenValidator> {
        val satelliteValidator = IntValidator(true, 0, 999)
        val optionalDoubleValidator = DoubleValidator(true)

        return arrayOf(
            // type $__GSA
            TypeValidator("GSA"),
            // mode1 A (automatic) or M (manual)
            EnumValidator(Mode1.values().map { it.name.single() }.toCharArray(), true),
            // mode2 1 (fix not available) 2 (2D) or 3 (3D)
            EnumValidator(charArrayOf('1', '2', '3'), true),
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

    /**
     * A=Automatic
     * M=Manual
     */
    enum class Mode1 {
        A, M;
    }

    enum class Mode2 {
        FIX_NOT_AVAILABLE, FIX_2D, FIX_3D
    }
}
