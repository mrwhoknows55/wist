package dev.avadhut.wist.ui.theme

import androidx.compose.ui.graphics.Color


// Wist COLOR PALETTE
// Design Philosophy: KISS with premium, high-contrast Dark Mode aesthetic



// BACKGROUNDS

/** Pure Black - Main screen background for maximum contrast */
val BackgroundPrimary = Color(0xFF000000)

/** Dark Grey - Surface/Card background for elevated content */
val BackgroundSurface = Color(0xFF121212)

/** Slightly lighter grey - Alternative card background */
val BackgroundCard = Color(0xFF1E1E1E)

/** Modal overlay with transparency */
val BackgroundOverlay = Color(0xB3000000) // 70% opacity black


// TYPOGRAPHY & ICONS

/** Primary text - Pure white for maximum readability */
val TextPrimary = Color(0xFFFFFFFF)

/** Secondary text - Light grey for supporting content */
val TextSecondary = Color(0xFFB0B0B0)

/** Disabled/hint text - Subtle grey */
val TextDisabled = Color(0xFF666666)


// BORDERS & DIVIDERS

/** Subtle border color for outlined elements */
val BorderDefault = Color(0xFF333333)

/** Divider color */
val DividerColor = Color(0xFF2A2A2A)


// ACCENTS & FUNCTIONAL COLORS

/** Brand/Action - High contrast white for primary actions */
val AccentPrimary = Color(0xFFFFFFFF)

/** Action accent for graphs and highlights */
val AccentBlue = Color(0xFF4A90D9)

/** Alert/Error state */
val AlertRed = Color(0xFFE53935)

/** Success/positive state */
val SuccessGreen = Color(0xFF43A047)

/** Warning state */
val WarningOrange = Color(0xFFFFA726)

/** Buy signal - "Wait for Offer" indicator */
val BuySignalWait = Color(0xFFE53935)

/** Buy signal - "Good to Buy" indicator */
val BuySignalGood = Color(0xFF43A047)


// SOURCE BRAND COLORS
// Specific colors for recognizable e-commerce/service brands

object SourceColors {
    val Amazon = Color(0xFFFF9900)
    val Flipkart = Color(0xFF2874F0)
    val Myntra = Color(0xFFFF3F6C)
    val Generic = Color(0xFF888888) // Fallback for unknown sources
}


// LEGACY COLORS (Kept for compatibility, can be removed later)

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)