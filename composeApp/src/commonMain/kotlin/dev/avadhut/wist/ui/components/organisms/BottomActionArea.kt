package dev.avadhut.wist.ui.components.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.avadhut.wist.ui.components.atoms.WistButton
import dev.avadhut.wist.ui.components.atoms.WistButtonStyle
import dev.avadhut.wist.ui.theme.BackgroundPrimary
import dev.avadhut.wist.ui.theme.WistDimensions
import dev.avadhut.wist.ui.theme.WistTheme

/**
 * Bottom Action Area - Sticky bottom buttons
 *
 * Used at the bottom of screens for primary actions.
 * Common configuration: "Add product" + "Create New list"
 *
 * @param primaryText Primary button text
 * @param secondaryText Secondary button text
 * @param onPrimaryClick Primary button click handler
 * @param onSecondaryClick Secondary button click handler
 * @param modifier Modifier for customization
 * @param primaryEnabled Whether primary button is enabled
 * @param secondaryEnabled Whether secondary button is enabled
 */
@Composable
fun BottomActionArea(
    primaryText: String,
    secondaryText: String,
    onPrimaryClick: () -> Unit,
    onSecondaryClick: () -> Unit,
    modifier: Modifier = Modifier,
    primaryEnabled: Boolean = true,
    secondaryEnabled: Boolean = true,
    showSecondary: Boolean = true,
    primaryButtonStyle: WistButtonStyle = WistButtonStyle.TERTIARY,
    secondaryButtonStyle: WistButtonStyle = WistButtonStyle.SECONDARY
) {
    Row(
        modifier = modifier.navigationBarsPadding().fillMaxWidth().background(BackgroundPrimary)
            .padding(WistDimensions.BottomActionAreaPadding),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        WistButton(
            text = primaryText,
            onClick = onPrimaryClick,
            style = primaryButtonStyle,
            enabled = primaryEnabled,
            modifier = Modifier.weight(1f)
        )

        if (showSecondary) {

            Spacer(
                modifier = Modifier.width(
                    WistDimensions.SpacingSm
                )
            )

            WistButton(
                text = secondaryText,
                onClick = onSecondaryClick,
                style = secondaryButtonStyle,
                enabled = secondaryEnabled,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * Single Action Bottom Area - For single primary action
 *
 * Used when only one action is needed (e.g., "Add Product" on list detail).
 *
 * @param text Button text
 * @param onClick Click handler
 * @param modifier Modifier for customization
 * @param enabled Whether button is enabled
 * @param style Button style
 */
@Composable
fun SingleActionBottomArea(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    style: WistButtonStyle = WistButtonStyle.SECONDARY
) {
    Row(
        modifier = modifier.fillMaxWidth().background(BackgroundPrimary)
            .padding(WistDimensions.BottomActionAreaPadding),
        horizontalArrangement = Arrangement.Center
    ) {
        WistButton(
            text = text, onClick = onClick, style = style, enabled = enabled, fillMaxWidth = true
        )
    }
}


// PREVIEWS


@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun BottomActionAreaPreview() {
    WistTheme {
        BottomActionArea(
            primaryText = "Add product",
            secondaryText = "Create New list",
            onPrimaryClick = {},
            onSecondaryClick = {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun BottomActionAreaDisabledPreview() {
    WistTheme {
        BottomActionArea(
            primaryText = "Add product",
            secondaryText = "Create New list",
            onPrimaryClick = {},
            onSecondaryClick = {},
            primaryEnabled = false
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun SingleActionBottomAreaPreview() {
    WistTheme {
        SingleActionBottomArea(
            text = "Add Product", onClick = {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun SingleActionBottomAreaPrimaryPreview() {
    WistTheme {
        SingleActionBottomArea(
            text = "Confirm", onClick = {}, style = WistButtonStyle.PRIMARY
        )
    }
}
