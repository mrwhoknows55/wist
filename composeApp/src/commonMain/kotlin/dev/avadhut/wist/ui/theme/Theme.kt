package dev.avadhut.wist.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb


// Wist THEME
// Dark mode first design with premium, high-contrast aesthetic


/**
 * Wist Dark Color Scheme
 * Primary aesthetic - pure black background with white accents
 */
val WistDarkColorScheme = darkColorScheme(
    // Primary colors (white for high contrast)
    primary = AccentPrimary,
    onPrimary = BackgroundPrimary,
    primaryContainer = BackgroundSurface,
    onPrimaryContainer = TextPrimary,

    // Secondary colors
    secondary = TextSecondary,
    onSecondary = BackgroundPrimary,
    secondaryContainer = BackgroundCard,
    onSecondaryContainer = TextPrimary,

    // Tertiary/Accent colors
    tertiary = AccentBlue,
    onTertiary = TextPrimary,
    tertiaryContainer = BackgroundCard,
    onTertiaryContainer = TextPrimary,

    // Error colors
    error = AlertRed,
    onError = TextPrimary,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    // Background colors
    background = BackgroundPrimary,
    onBackground = TextPrimary,

    // Surface colors
    surface = BackgroundPrimary,
    onSurface = TextPrimary,
    surfaceVariant = BackgroundSurface,
    onSurfaceVariant = TextSecondary,

    // Container colors
    surfaceContainer = BackgroundCard,
    surfaceContainerHigh = BackgroundSurface,
    surfaceContainerHighest = BackgroundCard,
    surfaceContainerLow = BackgroundPrimary,
    surfaceContainerLowest = BackgroundPrimary,

    // Outline colors
    outline = BorderDefault,
    outlineVariant = DividerColor,

    // Inverse colors (for snackbars, etc.)
    inverseSurface = TextPrimary,
    inverseOnSurface = BackgroundPrimary,
    inversePrimary = BackgroundPrimary,

    // Scrim for modals
    scrim = BackgroundOverlay
)

/**
 * Wist Light Color Scheme (fallback, dark mode is primary)
 * Inverted colors for potential future light theme support
 */
val WistLightColorScheme = lightColorScheme(
    primary = BackgroundPrimary,
    onPrimary = TextPrimary,
    primaryContainer = BackgroundSurface,
    onPrimaryContainer = TextPrimary,

    secondary = TextSecondary,
    onSecondary = BackgroundPrimary,

    background = Color(0xFFFFFBFE),
    onBackground = BackgroundPrimary,

    surface = Color(0xFFFFFBFE),
    onSurface = BackgroundPrimary,
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = TextDisabled,

    outline = BorderDefault
)


/**
 * Wist Theme Composable
 *
 * @param darkTheme Force dark theme (default: follow system)
 * @param content The composable content to theme
 */
@Composable
expect fun WistTheme(
    darkTheme: Boolean = true, // Dark mode is the default design
    content: @Composable () -> Unit
)