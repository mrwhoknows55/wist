package dev.avadhut.wist.ui.components.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import dev.avadhut.wist.ui.theme.SourceColors
import dev.avadhut.wist.ui.theme.WistDimensions
import dev.avadhut.wist.ui.theme.WistTheme

/**
 * Known e-commerce/service sources with brand colors and initials
 */
enum class KnownSource(
    val displayName: String, val color: Color, val initial: String
) {
    AMAZON("Amazon", SourceColors.Amazon, "A"), FLIPKART(
        "Flipkart",
        SourceColors.Flipkart,
        "F"
    ),
    MYNTRA("Myntra", SourceColors.Myntra, "M"), GENERIC("Web", SourceColors.Generic, "W")
}

/**
 * Source Icon - Shows brand or generic website icon
 *
 * Displays a circular colored icon with the brand initial.
 * Recognized brands (Amazon, Flipkart, Myntra) show their colors.
 * Unknown sources show a generic globe icon.
 *
 * @param source The source type (enum)
 * @param modifier Modifier for customization
 * @param size Icon size
 * @param showLabel Whether to show the source name alongside the icon
 */
@Composable
fun SourceIcon(
    source: KnownSource,
    modifier: Modifier = Modifier,
    size: Dp = WistDimensions.SourceIconSize,
    showLabel: Boolean = false
) {
    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = modifier
    ) {
        if (source == KnownSource.GENERIC) {
            // Show globe icon for generic sources
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(source.color),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Language,
                    contentDescription = "Website",
                    modifier = Modifier.size(size * 0.6f),
                    tint = Color.White
                )
            }
        } else {
            // Show colored circle with initial for known brands
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(source.color),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = source.initial,
                    color = Color.White,
                    fontSize = (size.value * 0.5f).sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (showLabel) {
            Spacer(modifier = Modifier.width(
                WistDimensions.SpacingXs))
            Text(
                text = source.displayName,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Source Icon with URL-based detection
 *
 * Automatically detects the source from a URL and renders the appropriate icon.
 *
 * @param url The product/item URL
 * @param modifier Modifier for customization
 * @param size Icon size
 * @param showLabel Whether to show the source name
 */
@Composable
fun SourceIconFromUrl(
    url: String,
    modifier: Modifier = Modifier,
    size: Dp = WistDimensions.SourceIconSize,
    showLabel: Boolean = false
) {
    val source = detectSourceFromUrl(url)
    SourceIcon(
        source = source, modifier = modifier, size = size, showLabel = showLabel
    )
}

/**
 * Detects the source platform from a URL
 */
fun detectSourceFromUrl(url: String): KnownSource {
    val lowerUrl = url.lowercase()
    return when {
        lowerUrl.contains("amazon") -> KnownSource.AMAZON
        lowerUrl.contains("flipkart") -> KnownSource.FLIPKART
        lowerUrl.contains("myntra") -> KnownSource.MYNTRA
        else -> KnownSource.GENERIC
    }
}


// PREVIEWS


@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun SourceIconAmazonPreview() {
    WistTheme {
        SourceIcon(source = KnownSource.AMAZON)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun SourceIconFlipkartPreview() {
    WistTheme {
        SourceIcon(source = KnownSource.FLIPKART)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun SourceIconMyntraPreview() {
    WistTheme {
        SourceIcon(source = KnownSource.MYNTRA)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun SourceIconGenericPreview() {
    WistTheme {
        SourceIcon(source = KnownSource.GENERIC)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun SourceIconWithLabelPreview() {
    WistTheme {
        SourceIcon(
            source = KnownSource.FLIPKART,
            showLabel = true
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun SourceIconRowPreview() {
    WistTheme {
        Row {
            KnownSource.entries.forEach { source ->
                SourceIcon(source = source)
                Spacer(
                    modifier = Modifier.width(
                        WistDimensions.SpacingXs
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun SourceIconFromUrlPreview() {
    WistTheme {
        Row {
            SourceIconFromUrl(url = "https://www.amazon.com/product/123")
            Spacer(
                modifier = Modifier.width(
                    WistDimensions.SpacingSm
                )
            )
            SourceIconFromUrl(
                url = "https://www.flipkart.com/phone",
                showLabel = true
            )
        }
    }
}
