package dev.avadhut.wist.util

import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterCurrencyStyle

actual fun formatCurrency(
    price: Double,
    currencyCode: String,
    showCurrencySymbol: Boolean
): String {
    val formatter = NSNumberFormatter().apply {
        numberStyle = NSNumberFormatterCurrencyStyle
        this.currencyCode = currencyCode
    }

    return if (showCurrencySymbol) {
        formatter.stringFromNumber(NSNumber(price)) ?: ""
    } else {
        val formatted = formatter.stringFromNumber(NSNumber(price)) ?: ""
        formatted.replace(Regex("[^0-9,.]"), "").trim()
    }
}
