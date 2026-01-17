package dev.avadhut.wist.ui.components.atoms

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import dev.avadhut.wist.ui.theme.WistDimensions
import dev.avadhut.wist.ui.theme.WistTheme

/**
 * Wist Icon Button - Transparent icon buttons
 *
 * 24dp icons with transparent background for toolbar actions.
 * Common uses: Back, Close, Filter, Menu, Search
 *
 * @param icon The icon to display
 * @param contentDescription Accessibility description
 * @param onClick Click handler
 * @param modifier Modifier for customization
 * @param tint Icon color, defaults to theme's onBackground
 * @param enabled Whether the button is enabled
 */
@Composable
fun WistIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.onBackground,
    enabled: Boolean = true
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(WistDimensions.IconButtonSize),
        enabled = enabled
    ) {
        Icon(
            imageVector = icon, contentDescription = contentDescription, modifier = Modifier.size(
                WistDimensions.IconSizeMedium
            ), tint = if (enabled) tint else tint.copy(alpha = 0.38f)
        )
    }
}


// PREVIEWS


@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun WistIconButtonBackPreview() {
    WistTheme {
        WistIconButton(
            icon = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", onClick = {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun WistIconButtonClosePreview() {
    WistTheme {
        WistIconButton(
            icon = Icons.Filled.Close, contentDescription = "Close", onClick = {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun WistIconButtonSearchPreview() {
    WistTheme {
        WistIconButton(
            icon = Icons.Filled.Search, contentDescription = "Search", onClick = {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun WistIconButtonMenuPreview() {
    WistTheme {
        WistIconButton(
            icon = Icons.Filled.MoreVert, contentDescription = "Menu", onClick = {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun WistIconButtonDisabledPreview() {
    WistTheme {
        WistIconButton(
            icon = Icons.Filled.Search, contentDescription = "Search", onClick = {}, enabled = false
        )
    }
}
