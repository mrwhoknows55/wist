package dev.avadhut.wist.util

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

actual fun formatCurrency(
    price: Double,
    currencyCode: String,
    showCurrencySymbol: Boolean
): String {
    return try {
        val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
        format.currency = Currency.getInstance(currencyCode)
        if (showCurrencySymbol) {
            format.format(price)
        } else {
            val formatted = format.format(price)
            formatted.replace(Regex("[^0-9,.]"), "").trim()
        }
    } catch (e: Exception) {
        if (showCurrencySymbol) {
            "$${"%.2f".format(price)}"
        } else {
            "%.2f".format(price)
        }
    }
}
