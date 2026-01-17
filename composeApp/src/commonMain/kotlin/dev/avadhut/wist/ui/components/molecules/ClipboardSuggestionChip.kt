package dev.avadhut.wist.ui.components.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.avadhut.wist.ui.components.atoms.SourceIcon
import dev.avadhut.wist.ui.components.atoms.detectSourceFromUrl
import dev.avadhut.wist.ui.theme.BackgroundCard
import dev.avadhut.wist.ui.theme.TextPrimary
import dev.avadhut.wist.ui.theme.TextSecondary
import dev.avadhut.wist.ui.theme.WistDimensions
import dev.avadhut.wist.ui.theme.WistTheme

/**
 * Clipboard Suggestion Chip - Shows detected clipboard link
 *
 * Displays a clickable chip for a URL detected in the clipboard.
 * Shows source icon, truncated URL, and an add action.
 *
 * @param url The detected URL from clipboard
 * @param onClick Callback when chip is clicked
 * @param modifier Modifier for customization
 * @param maxUrlLength Maximum characters to show for URL
 */
@Composable
fun ClipboardSuggestionChip(
    url: String, onClick: () -> Unit, modifier: Modifier = Modifier, maxUrlLength: Int = 20
) {
    val source = detectSourceFromUrl(url)
    val displayUrl =
        truncateUrl(url, maxUrlLength)

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(WistDimensions.ChipRadius))
            .background(BackgroundCard)
            .clickable(onClick = onClick)
            .padding(
                horizontal = WistDimensions.ChipPaddingHorizontal,
                vertical = WistDimensions.SpacingXs
            )
            .height(WistDimensions.ChipHeight), verticalAlignment = Alignment.CenterVertically
    ) {
        // Source Icon
        SourceIcon(
            source = source,
            size = WistDimensions.SourceIconSizeSmall
        )

        Spacer(modifier = Modifier.width(
            WistDimensions.SpacingXs))

        // URL Text
        Text(
            text = displayUrl,
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.width(
            WistDimensions.SpacingXs))

        // Add Icon
        Box(
            modifier = Modifier
                .size(WistDimensions.IconSizeSmall)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add",
                tint = TextPrimary,
                modifier = Modifier.size(12.dp)
            )
        }
    }
}

/**
 * Truncates a URL for display, keeping the domain visible
 */
private fun truncateUrl(url: String, maxLength: Int): String {
    // Remove protocol
    val cleanUrl = url.removePrefix("https://").removePrefix("http://").removePrefix("www.")

    return if (cleanUrl.length <= maxLength) {
        cleanUrl
    } else {
        cleanUrl.take(maxLength - 3) + "..."
    }
}

// For dp literal
private val dp = WistDimensions.SpacingXxs


// PREVIEWS


@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun ClipboardSuggestionChipAmazonPreview() {
    WistTheme {
        ClipboardSuggestionChip(
            url = "https://www.amazon.com/phone-iphone-15-pro",
            onClick = {},
            modifier = Modifier.padding(
                WistDimensions.SpacingSm
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun ClipboardSuggestionChipFlipkartPreview() {
    WistTheme {
        ClipboardSuggestionChip(
            url = "https://www.flipkart.com/oneplus-nord",
            onClick = {},
            modifier = Modifier.padding(
                WistDimensions.SpacingSm
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun ClipboardSuggestionChipRowPreview() {
    WistTheme {
        Row(
            modifier = Modifier.padding(
                WistDimensions.SpacingSm
            )
        ) {
            ClipboardSuggestionChip(
                url = "https://www.amazon.com/phone-i...", onClick = {})
            Spacer(
                modifier = Modifier.width(
                    WistDimensions.SpacingSm
                )
            )
            ClipboardSuggestionChip(
                url = "https://www.flipkart.com/phone", onClick = {})
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun ClipboardSuggestionChipColumnPreview() {
    WistTheme {
        Column(
            modifier = Modifier.padding(
                WistDimensions.SpacingSm
            )
        ) {
            Text(
                text = "Clipboard",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )
            Spacer(
                modifier = Modifier.height(
                    WistDimensions.SpacingSm
                )
            )
            Row {
                ClipboardSuggestionChip(
                    url = "https://www.amazon.com/phone-iphone-15-pro-max", onClick = {})
                Spacer(
                    modifier = Modifier.width(
                        WistDimensions.SpacingSm
                    )
                )
                ClipboardSuggestionChip(
                    url = "https://www.flipkart.com/oneplus", onClick = {})
            }
        }
    }
}
