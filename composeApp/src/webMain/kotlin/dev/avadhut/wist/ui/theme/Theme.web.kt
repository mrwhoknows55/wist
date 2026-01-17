package dev.avadhut.wist.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
actual fun WistTheme(
    darkTheme: Boolean, content: @Composable (() -> Unit)
) {
    val colorScheme = if (darkTheme) WistDarkColorScheme else WistLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme, typography = WistTypography, content = content
    )
}