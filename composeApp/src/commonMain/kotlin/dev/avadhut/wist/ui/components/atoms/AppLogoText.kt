package dev.avadhut.wist.ui.components.atoms

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview

/**
 * App Logo Text - "Wist" branding
 *
 * Display-sized bold text for app header/branding.
 * Uses the display typography style (24sp Bold White).
 *
 * @param modifier Modifier for customization
 * @param text Logo text, defaults to "Wist"
 */
@Composable
fun AppLogoText(
    modifier: Modifier = Modifier, text: String = "Wist"
) {
    Text(
        text = text, style = MaterialTheme.typography.displayLarge.copy(
            fontWeight = FontWeight.Bold
        ), color = MaterialTheme.colorScheme.onBackground, modifier = modifier
    )
}


// PREVIEWS
@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun AppLogoTextPreview() {
    AppLogoText()
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun AppLogoTextCustomPreview() {
    AppLogoText(text = "My Wishlist")
}
