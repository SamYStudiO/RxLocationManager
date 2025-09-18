package net.samystudio.rxlocationmanager.nmea

import java.util.Locale

interface TokenValidator {
    fun validate(token: String): Boolean
}

class IntValidator(
    private val optional: Boolean = false,
    private val min: Int = Int.MIN_VALUE,
    private val max: Int = Int.MAX_VALUE,
) : TokenValidator {
    override fun validate(token: String): Boolean {
        if (token.isBlank() && optional) return true
        val i: Int = token.toIntOrNull() ?: return false
        return i in min..max
    }
}

class DoubleValidator(
    private val optional: Boolean = false,
    private val min: Double = -Double.MAX_VALUE,
    private val max: Double = Double.MAX_VALUE,
) : TokenValidator {
    override fun validate(token: String): Boolean {
        if (token.isBlank() && optional) return true
        val i: Double = token.toDoubleOrNull() ?: return false
        return i in min..max
    }
}

class TypeValidator(
    private val type: String,
) : TokenValidator {
    override fun validate(token: String): Boolean = token.matches(("[A-Z]{2}${type.uppercase(Locale.ROOT)}").toRegex())
}

class EnumValidator(
    private val chars: CharArray,
    private val optional: Boolean = false,
) : TokenValidator {
    override fun validate(token: String): Boolean {
        if (token.isBlank() && optional) return true
        val s = chars.joinToString(",")
        return token.matches(("[$s]").toRegex())
    }
}

class LatitudeValidator(
    private val optional: Boolean = false,
) : TokenValidator {
    override fun validate(token: String): Boolean {
        if (token.isBlank() && optional) return true
        return token.matches("^\\d{4}\\.\\d{4,}$".toRegex())
    }
}

class LongitudeValidator(
    private val optional: Boolean = false,
) : TokenValidator {
    override fun validate(token: String): Boolean {
        if (token.isBlank() && optional) return true
        return token.matches("^\\d{5}\\.\\d{4,}$".toRegex())
    }
}

class TimeValidator(
    private val optional: Boolean = false,
) : TokenValidator {
    override fun validate(token: String): Boolean {
        if (token.isBlank() && optional) return true
        return token.matches("^\\d{6}(\\.\\d{1,4})?$".toRegex())
    }
}

class DateValidator(
    private val optional: Boolean = false,
) : TokenValidator {
    override fun validate(token: String): Boolean {
        if (token.isBlank() && optional) return true
        return token.matches("^\\d{6}$".toRegex())
    }
}

class StringValidator(
    private val optional: Boolean = false,
    private val min: Int = Int.MIN_VALUE,
    private val max: Int = Int.MAX_VALUE,
) : TokenValidator {
    override fun validate(token: String): Boolean {
        if (token.isBlank() && optional) return true
        return token.length in min..max
    }
}
