package dev.avadhut.wist.util

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.js


@OptIn(ExperimentalWasmJsInterop::class)
private fun formatCurrencyWithSymbolJs(price: Double, currencyCode: String): String =
    js("new Intl.NumberFormat(undefined, { style: 'currency', currency: currencyCode }).format(price)")

@OptIn(ExperimentalWasmJsInterop::class)
private fun formatCurrencyValueJs(price: Double): String =
    js("new Intl.NumberFormat(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 }).format(price)")

@OptIn(ExperimentalWasmJsInterop::class)
actual fun formatCurrency(
    price: Double,
    currencyCode: String,
    showCurrencySymbol: Boolean
): String = try {
    if (showCurrencySymbol) {
        formatCurrencyWithSymbolJs(price, currencyCode)
    } else {
        formatCurrencyValueJs(price)
    }
} catch (e: Throwable) {
    // Basic fallback if Intl is not available or JS call fails
    val fixed = price.toString()
    if (showCurrencySymbol) "$$fixed" else fixed
}