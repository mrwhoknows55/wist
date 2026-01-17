package dev.avadhut.wist.ui.components.organisms

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import dev.avadhut.wist.ui.components.atoms.AppLogoText
import dev.avadhut.wist.ui.components.atoms.WistIconButton
import dev.avadhut.wist.ui.theme.BackgroundPrimary
import dev.avadhut.wist.ui.theme.TextPrimary
import dev.avadhut.wist.ui.theme.WistTheme

/**
 * Wist Top App Bar - Home Variant
 *
 * Shows the app logo with optional action buttons.
 * Used on the main dashboard screen.
 *
 * @param modifier Modifier for customization
 * @param actions Optional composable for action buttons (trailing)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WistHomeTopAppBar(
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            AppLogoText()
        },
        modifier = modifier.fillMaxWidth(),
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BackgroundPrimary,
            titleContentColor = TextPrimary,
            actionIconContentColor = TextPrimary
        )
    )
}

/**
 * Wist Top App Bar - Detail Variant
 *
 * Shows a back button, centered title, and optional menu.
 * Used on list detail and product detail screens.
 *
 * @param title Screen title
 * @param onBackClick Callback when back button is pressed
 * @param modifier Modifier for customization
 * @param actions Optional composable for action buttons (trailing)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WistDetailTopAppBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Medium
                )
            )
        },
        modifier = modifier.fillMaxWidth(),
        navigationIcon = {
            WistIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                onClick = onBackClick
            )
        },
        actions = actions,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = BackgroundPrimary,
            titleContentColor = TextPrimary,
            navigationIconContentColor = TextPrimary,
            actionIconContentColor = TextPrimary
        )
    )
}


// PREVIEWS


@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun WistHomeTopAppBarPreview() {
    WistTheme {
        WistHomeTopAppBar()
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun WistHomeTopAppBarWithActionsPreview() {
    WistTheme {
        WistHomeTopAppBar(
            actions = {
                WistIconButton(
                    icon = Icons.Filled.MoreVert,
                    contentDescription = "Menu",
                    onClick = {}
                )
            }
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun WistDetailTopAppBarPreview() {
    WistTheme {
        WistDetailTopAppBar(
            title = "Phones",
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun WistDetailTopAppBarWithActionsPreview() {
    WistTheme {
        WistDetailTopAppBar(
            title = "My Wishlist",
            onBackClick = {},
            actions = {
                WistIconButton(
                    icon = Icons.Filled.MoreVert,
                    contentDescription = "Options",
                    onClick = {}
                )
            }
        )
    }
}
