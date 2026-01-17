package dev.avadhut.wist.ui.components.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.avadhut.wist.ui.components.atoms.KnownSource
import dev.avadhut.wist.ui.components.atoms.PriceTag
import dev.avadhut.wist.ui.components.atoms.SourceIcon
import dev.avadhut.wist.ui.components.atoms.WistButton
import dev.avadhut.wist.ui.components.atoms.WistButtonStyle
import dev.avadhut.wist.ui.theme.AccentBlue
import dev.avadhut.wist.ui.theme.AlertRed
import dev.avadhut.wist.ui.theme.BackgroundCard
import dev.avadhut.wist.ui.theme.BackgroundPrimary
import dev.avadhut.wist.ui.theme.BackgroundSurface
import dev.avadhut.wist.ui.theme.DividerColor
import dev.avadhut.wist.ui.theme.SourceColors
import dev.avadhut.wist.ui.theme.SuccessGreen
import dev.avadhut.wist.ui.theme.TextDisabled
import dev.avadhut.wist.ui.theme.TextPrimary
import dev.avadhut.wist.ui.theme.TextSecondary
import dev.avadhut.wist.ui.theme.WistDimensions
import dev.avadhut.wist.ui.theme.WistTheme

/**
 * Buy Signal indicator type
 */
enum class BuySignal(val label: String) {
    WAIT_FOR_OFFER("Wait for Offer"),
    GOOD_TO_BUY("Good to Buy")
}

/**
 * Data class for product detail display
 */
data class ProductDetailData(
    val id: String,
    val title: String,
    val price: Double,
    val currencyCode: String = "USD",
    val source: KnownSource,
    val sourceUrl: String,
    val imageUrl: String? = null,
    val highlights: List<String> = emptyList(),
    val buySignal: BuySignal? = null,
    val dateCreated: String? = null
)

/**
 * Product Detail Card - Rich detail view for a single product
 *
 * Displays:
 * - Large product image
 * - Title and source link
 * - Price tag
 * - Highlights section
 * - Buy signal indicator
 * - Price history graph placeholder
 * - Reddit reviews section placeholder
 * - Action buttons (Notify Me, Find best price)
 *
 * @param data Product detail data
 * @param onSourceClick Callback when source link is clicked
 * @param onNotifyClick Callback when Notify Me is clicked
 * @param onFindBestPriceClick Callback when Find best price is clicked
 * @param modifier Modifier for customization
 */
@Composable
fun ProductDetailCard(
    data: ProductDetailData,
    onSourceClick: () -> Unit,
    onNotifyClick: () -> Unit,
    onFindBestPriceClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(BackgroundPrimary)
            .verticalScroll(rememberScrollState())
    ) {
        // Product Image
        ProductDetailImage(
            imageUrl = data.imageUrl,
            modifier = Modifier
                .fillMaxWidth()
                .height(WistDimensions.ProductImageHeight)
        )

        // Content
        Column(
            modifier = Modifier.padding(
                WistDimensions.ScreenPaddingHorizontal)
        ) {
            Spacer(modifier = Modifier.height(
                WistDimensions.SpacingLg))

            // Title
            Text(
                text = data.title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(
                WistDimensions.SpacingMd))

            // Price and Source Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PriceTag(
                    price = data.price,
                    currencyCode = data.currencyCode
                )

                // Source link
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SourceIcon(
                        source = data.source,
                        showLabel = true
                    )
                    Spacer(modifier = Modifier.width(
                        WistDimensions.SpacingXs))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = "Open in browser",
                        tint = TextSecondary,
                        modifier = Modifier.size(
                            WistDimensions.IconSizeSmall)
                    )
                }
            }

            // Highlights Section
            if (data.highlights.isNotEmpty()) {
                Spacer(modifier = Modifier.height(
                    WistDimensions.SpacingLg))

                HorizontalDivider(color = DividerColor)

                Spacer(modifier = Modifier.height(
                    WistDimensions.SpacingLg))

                Text(
                    text = "HIGHLIGHTS",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(
                    WistDimensions.SpacingSm))

                data.highlights.forEach { highlight ->
                    Row(
                        modifier = Modifier.padding(vertical = WistDimensions.SpacingXxs)
                    ) {
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(
                            WistDimensions.SpacingSm))
                        Text(
                            text = highlight,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary
                        )
                    }
                }
            }

            // Buy Signal Section
            if (data.buySignal != null) {
                Spacer(modifier = Modifier.height(
                    WistDimensions.SpacingLg))

                HorizontalDivider(color = DividerColor)

                Spacer(modifier = Modifier.height(
                    WistDimensions.SpacingLg))

                BuySignalIndicator(
                    signal = data.buySignal
                )
            }

            // Price History Section (Placeholder)
            Spacer(modifier = Modifier.height(
                WistDimensions.SpacingLg))

            HorizontalDivider(color = DividerColor)

            Spacer(modifier = Modifier.height(
                WistDimensions.SpacingLg))

            PriceHistorySection()

            // Reddit Reviews Section (Placeholder)
            Spacer(modifier = Modifier.height(
                WistDimensions.SpacingLg))

            HorizontalDivider(color = DividerColor)

            Spacer(modifier = Modifier.height(
                WistDimensions.SpacingLg))

            RedditReviewsSection()

            // Second Opinion Section
            Spacer(modifier = Modifier.height(
                WistDimensions.SpacingLg))

            HorizontalDivider(color = DividerColor)

            Spacer(modifier = Modifier.height(
                WistDimensions.SpacingLg))

            SecondOpinionSection()

            Spacer(modifier = Modifier.height(
                WistDimensions.SpacingXl))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(WistDimensions.SpacingSm)
            ) {
                WistButton(
                    text = "Notify Me",
                    onClick = onNotifyClick,
                    style = WistButtonStyle.SECONDARY,
                    modifier = Modifier.weight(1f)
                )
                WistButton(
                    text = "Find me best price",
                    onClick = onFindBestPriceClick,
                    style = WistButtonStyle.SECONDARY,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(
                WistDimensions.SpacingXl))
        }
    }
}

/**
 * Product Detail Image with placeholder
 */
@Composable
private fun ProductDetailImage(
    imageUrl: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(BackgroundSurface),
        contentAlignment = Alignment.Center
    ) {
        // In production, use Coil or similar
        Icon(
            imageVector = Icons.Filled.Image,
            contentDescription = null,
            tint = TextDisabled,
            modifier = Modifier.size(64.dp)
        )
    }
}

/**
 * Buy Signal Indicator
 */
@Composable
private fun BuySignalIndicator(
    signal: BuySignal,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "BUY SIGNAL",
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        when (signal) {
                            BuySignal.WAIT_FOR_OFFER -> AlertRed
                            BuySignal.GOOD_TO_BUY -> SuccessGreen
                        }
                    )
            )
            Spacer(modifier = Modifier.width(
                WistDimensions.SpacingSm))
            Text(
                text = signal.label,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
            )
        }
    }
}

/**
 * Price History Section (Placeholder)
 */
@Composable
private fun PriceHistorySection(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "PRICE HISTORY",
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(
            WistDimensions.SpacingMd))

        // Graph placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(RoundedCornerShape(WistDimensions.CardRadius))
                .background(BackgroundCard),
            contentAlignment = Alignment.Center
        ) {
            // Simple wave line placeholder
            Text(
                text = "📈 Price Graph",
                style = MaterialTheme.typography.bodyMedium,
                color = TextDisabled
            )
        }

        Spacer(modifier = Modifier.height(
            WistDimensions.SpacingSm))

        // Month labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun").forEach { month ->
                Text(
                    text = month,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextDisabled
                )
            }
        }
    }
}

/**
 * Reddit Reviews Section (Placeholder)
 */
@Composable
private fun RedditReviewsSection(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(WistDimensions.CardRadius))
            .background(BackgroundCard)
            .padding(WistDimensions.SpacingLg),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Reddit icon placeholder
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(SourceColors.Amazon),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "R",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
        }

        Spacer(modifier = Modifier.width(
            WistDimensions.SpacingMd))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Reddit Reviews",
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
            Text(
                text = "View what people are saying about this product on reddit.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
            contentDescription = "Open",
            tint = TextSecondary
        )
    }
}

/**
 * Second Opinion Section
 */
@Composable
private fun SecondOpinionSection(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SECOND OPINION",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )

            // Progress indicator placeholder
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(TextDisabled)
            )
        }

        Spacer(modifier = Modifier.height(
            WistDimensions.SpacingSm))

        Text(
            text = "What works?",
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary
        )
        Text(
            text = "What works?",
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary
        )
    }
}


// PREVIEWS


@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun ProductDetailCardPreview() {
    WistTheme {
        ProductDetailCard(
            data = ProductDetailData(
                id = "1",
                title = "OnePlus Nord 5 5G (Dry Ice, 256 GB) (8 GB RAM)",
                price = 12999.0,
                currencyCode = "USD",
                source = KnownSource.FLIPKART,
                sourceUrl = "https://flipkart.com/product/123",
                highlights = listOf(
                    "8 GB RAM",
                    "17.35 cm (6.83 inch) Display",
                    "50MP Rear Camera",
                    "6800 mAh Battery"
                ),
                buySignal = BuySignal.WAIT_FOR_OFFER,
                dateCreated = "12 SEPT"
            ),
            onSourceClick = {},
            onNotifyClick = {},
            onFindBestPriceClick = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun ProductDetailCardMinimalPreview() {
    WistTheme {
        ProductDetailCard(
            data = ProductDetailData(
                id = "2",
                title = "Generic Product",
                price = 499.99,
                source = KnownSource.GENERIC,
                sourceUrl = "https://example.com"
            ),
            onSourceClick = {},
            onNotifyClick = {},
            onFindBestPriceClick = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun BuySignalIndicatorWaitPreview() {
    WistTheme {
        BuySignalIndicator(
            signal = BuySignal.WAIT_FOR_OFFER,
            modifier = Modifier.padding(
                WistDimensions.SpacingLg
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun BuySignalIndicatorGoodPreview() {
    WistTheme {
        BuySignalIndicator(
            signal = BuySignal.GOOD_TO_BUY,
            modifier = Modifier.padding(
                WistDimensions.SpacingLg
            )
        )
    }
}
