package dev.avadhut.wist.ui.components.atoms

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.avadhut.wist.ui.theme.BackgroundPrimary
import dev.avadhut.wist.ui.theme.BorderDefault
import dev.avadhut.wist.ui.theme.TextPrimary
import dev.avadhut.wist.ui.theme.WistDimensions
import dev.avadhut.wist.ui.theme.WistTheme

/**
 * Button style variants for Wist
 */
enum class WistButtonStyle {
    /** Solid white background with black text - for primary actions */
    PRIMARY,

    /** Outlined with transparent background - for secondary actions */
    SECONDARY,

    /** Grey background (surface color) - for less prominent actions */
    TERTIARY
}

/**
 * Wist Button - Primary action button
 *
 * Three variants:
 * - PRIMARY: Solid white background, black text (Confirm, Add)
 * - SECONDARY: Outlined with white text (Cancel, Create New)
 * - TERTIARY: Grey surface background, white text (Less prominent actions)
 *
 * @param text Button label text
 * @param onClick Click handler
 * @param modifier Modifier for customization
 * @param style Button style variant
 * @param enabled Whether the button is enabled
 * @param fillMaxWidth Whether to fill max width
 */
@Composable
fun WistButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: WistButtonStyle = WistButtonStyle.PRIMARY,
    enabled: Boolean = true,
    fillMaxWidth: Boolean = false
) {
    val buttonModifier = modifier.height(WistDimensions.ButtonHeight)
        .then(if (fillMaxWidth) Modifier.fillMaxWidth() else Modifier)

    when (style) {
        WistButtonStyle.PRIMARY -> {
            Button(
                onClick = onClick,
                modifier = buttonModifier,
                enabled = enabled,
                shape = RoundedCornerShape(WistDimensions.ButtonRadius),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TextPrimary,
                    contentColor = BackgroundPrimary,
                    disabledContainerColor = TextPrimary.copy(alpha = 0.38f),
                    disabledContentColor = BackgroundPrimary.copy(alpha = 0.38f)
                ),
                contentPadding = PaddingValues(
                    horizontal = WistDimensions.ButtonPaddingHorizontal,
                    vertical = WistDimensions.ButtonPaddingVertical
                )
            ) {
                Text(
                    text = text, style = MaterialTheme.typography.labelLarge
                )
            }
        }

        WistButtonStyle.SECONDARY -> {
            OutlinedButton(
                onClick = onClick,
                modifier = buttonModifier,
                enabled = enabled,
                shape = RoundedCornerShape(WistDimensions.ButtonRadius),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (enabled) BorderDefault else BorderDefault.copy(alpha = 0.38f)
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = TextPrimary,
                    disabledContentColor = TextPrimary.copy(alpha = 0.38f)
                ),
                contentPadding = PaddingValues(
                    horizontal = WistDimensions.ButtonPaddingHorizontal,
                    vertical = WistDimensions.ButtonPaddingVertical
                )
            ) {
                Text(
                    text = text, style = MaterialTheme.typography.labelLarge
                )
            }
        }

        WistButtonStyle.TERTIARY -> {
            Button(
                onClick = onClick,
                modifier = buttonModifier,
                enabled = enabled,
                shape = RoundedCornerShape(WistDimensions.ButtonRadius),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = TextPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.38f),
                    disabledContentColor = TextPrimary.copy(alpha = 0.38f)
                ),
                contentPadding = PaddingValues(
                    horizontal = WistDimensions.ButtonPaddingHorizontal,
                    vertical = WistDimensions.ButtonPaddingVertical
                )
            ) {
                Text(
                    text = text, style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}


// PREVIEWS


@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun WistButtonPrimaryPreview() {
    WistTheme {
        WistButton(
            text = "Confirm", onClick = {}, style = WistButtonStyle.PRIMARY
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun WistButtonSecondaryPreview() {
    WistTheme {
        WistButton(
            text = "Create New list", onClick = {}, style = WistButtonStyle.SECONDARY
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun WistButtonTertiaryPreview() {
    WistTheme {
        WistButton(
            text = "Add product", onClick = {}, style = WistButtonStyle.TERTIARY
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun WistButtonDisabledPreview() {
    WistTheme {
        WistButton(
            text = "Disabled", onClick = {}, style = WistButtonStyle.PRIMARY, enabled = false
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun WistButtonRowPreview() {
    WistTheme {
        Row {
            WistButton(
                text = "Add product",
                onClick = {},
                style = WistButtonStyle.TERTIARY,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            WistButton(
                text = "Create New list",
                onClick = {},
                style = WistButtonStyle.SECONDARY,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun WistButtonFullWidthPreview() {
    WistTheme {
        WistButton(
            text = "Confirm", onClick = {}, style = WistButtonStyle.PRIMARY, fillMaxWidth = true
        )
    }
}
