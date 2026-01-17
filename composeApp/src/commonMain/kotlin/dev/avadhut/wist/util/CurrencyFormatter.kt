package dev.avadhut.wist.util

/**
 * Formats a price with currency symbol in a platform-specific way.
 * 
 * @param price The price to format
 * @param currencyCode ISO 4217 currency code
 * @param showCurrencySymbol Whether to include the currency symbol
 * @return Formatted price string
 */
expect fun formatCurrency(
    price: Double,
    currencyCode: String,
    showCurrencySymbol: Boolean = true
): String
