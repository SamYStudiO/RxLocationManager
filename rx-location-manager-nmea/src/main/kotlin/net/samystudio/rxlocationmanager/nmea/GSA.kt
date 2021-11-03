package net.samystudio.rxlocationmanager.nmea

import java.util.*

class GSA(message: String) : Nmea(message) {
    val selectionMode by lazy {
        try {
            SelectionMode.valueOf(data[1])
        } catch (e: IllegalArgumentException) {
            SelectionMode.A
        }
    }
    val fix by lazy {
        try {
            return@lazy Fix.values().find { it.value == data[2].toInt() }
                ?: Fix.NO_FIX
        } catch (e: NumberFormatException) {
        } catch (e: ArrayIndexOutOfBoundsException) {
        }
        Fix.NO_FIX
    }
    val satellite1 by lazy { data[3].toIntOrNull() }
    val satellite2 by lazy { data[4].toIntOrNull() }
    val satellite3 by lazy { data[5].toIntOrNull() }
    val satellite4 by lazy { data[6].toIntOrNull() }
    val satellite5 by lazy { data[7].toIntOrNull() }
    val satellite6 by lazy { data[8].toIntOrNull() }
    val satellite7 by lazy { data[9].toIntOrNull() }
    val satellite8 by lazy { data[10].toIntOrNull() }
    val satellite9 by lazy { data[11].toIntOrNull() }
    val satellite10 by lazy { data[12].toIntOrNull() }
    val satellite11 by lazy { data[13].toIntOrNull() }
    val satellite12 by lazy { data[14].toIntOrNull() }
    val positionDilutionOfPrecision by lazy { data[15].toDoubleOrNull() }
    val horizontalDilutionOfPrecision by lazy { data[16].toDoubleOrNull() }
    val verticalDilutionOfPrecision by lazy { data[17].toDoubleOrNull() }
    val satelliteCount by lazy {
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
        selectionMode: SelectionMode = SelectionMode.A,
        fix: Fix = Fix.NO_FIX,
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
            type.uppercase(Locale.ROOT),
            selectionMode.name,
            fix.value,
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
            // selection mode
            EnumValidator(SelectionMode.values().map { it.name.single() }.toCharArray(), true),
            // fix
            EnumValidator(Fix.values().map { it.value.toString().single() }.toCharArray(), true),
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
    enum class SelectionMode {
        A, M;
    }

    enum class Fix(val value: Int) {
        NO_FIX(1), FIX_2D(2), FIX_3D(3)
    }
}
