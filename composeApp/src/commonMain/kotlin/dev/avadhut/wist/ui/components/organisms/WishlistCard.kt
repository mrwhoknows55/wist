package dev.avadhut.wist.ui.components.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.avadhut.wist.ui.components.atoms.KnownSource
import dev.avadhut.wist.ui.components.atoms.PriceRangeTag
import dev.avadhut.wist.ui.components.atoms.SourceIcon
import dev.avadhut.wist.ui.components.molecules.ProductThumbnailGrid
import dev.avadhut.wist.ui.theme.BackgroundCard
import dev.avadhut.wist.ui.theme.BorderDefault
import dev.avadhut.wist.ui.theme.TextPrimary
import dev.avadhut.wist.ui.theme.TextSecondary
import dev.avadhut.wist.ui.theme.WistDimensions
import dev.avadhut.wist.ui.theme.WistTheme

/**
 * Data class representing a wishlist for display
 */
data class WishlistDisplayData(
    val id: String,
    val name: String,
    val dateLabel: String,
    val productImages: List<String?>,
    val sources: List<KnownSource>,
    val priceMin: Double,
    val priceMax: Double,
    val currencyCode: String = "USD"
)

/**
 * Wishlist Card - Dashboard card showing wishlist preview
 *
 * Displays:
 * - List name and date
 * - 2x2 product thumbnail grid
 * - Source icons row
 * - Price range
 *
 * @param data Wishlist display data
 * @param onClick Click handler for the card
 * @param modifier Modifier for customization
 */
@Composable
fun WishlistCard(
    data: WishlistDisplayData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(WistDimensions.CardRadius))
            .background(BackgroundCard)
            .clickable(onClick = onClick)
            .padding(WistDimensions.SpacingLg),
        verticalAlignment = Alignment.Top
    ) {
        // Left side: Info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // List name
            Text(
                text = data.name,
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary
            )

            // Date
            Text(
                text = data.dateLabel,
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(
                WistDimensions.SpacingLg))

            // Source icons row
            Row(
                horizontalArrangement = Arrangement.spacedBy(WistDimensions.SpacingXxs)
            ) {
                data.sources.take(4).forEach { source ->
                    SourceIcon(
                        source = source,
                        size = WistDimensions.SourceIconSize
                    )
                }
            }

            Spacer(modifier = Modifier.height(
                WistDimensions.SpacingSm))

            // Price range
            PriceRangeTag(
                minPrice = data.priceMin,
                maxPrice = data.priceMax,
                currencyCode = data.currencyCode
            )
        }

        Spacer(modifier = Modifier.width(
            WistDimensions.SpacingMd))

        // Right side: Thumbnail grid
        ProductThumbnailGrid(
            imageUrls = data.productImages,
            modifier = Modifier.size(120.dp)
        )
    }
}

/**
 * Wishlist Card with border - Alternative styling with left border
 */
@Composable
fun WishlistCardBordered(
    data: WishlistDisplayData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(WistDimensions.CardRadius))
            .clickable(onClick = onClick)
    ) {
        // Left border accent
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(140.dp)
                .background(BorderDefault)
        )

        // Card content
        Row(
            modifier = Modifier
                .weight(1f)
                .padding(WistDimensions.SpacingLg),
            verticalAlignment = Alignment.Top
        ) {
            // Left side: Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = data.name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary
                )

                Text(
                    text = data.dateLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(
                    WistDimensions.SpacingLg))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(WistDimensions.SpacingXxs)
                ) {
                    data.sources.take(4).forEach { source ->
                        SourceIcon(
                            source = source,
                            size = WistDimensions.SourceIconSize
                        )
                    }
                }

                Spacer(modifier = Modifier.height(
                    WistDimensions.SpacingSm))

                PriceRangeTag(
                    minPrice = data.priceMin,
                    maxPrice = data.priceMax,
                    currencyCode = data.currencyCode
                )
            }

            Spacer(modifier = Modifier.width(
                WistDimensions.SpacingMd))

            ProductThumbnailGrid(
                imageUrls = data.productImages,
                modifier = Modifier.size(120.dp)
            )
        }
    }
}


// PREVIEWS


@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun WishlistCardPreview() {
    WistTheme {
        WishlistCard(
            data = WishlistDisplayData(
                id = "1",
                name = "Phones",
                dateLabel = "from Mar 25",
                productImages = listOf("img1", "img2", "img3", "img4"),
                sources = listOf(
                    KnownSource.AMAZON,
                    KnownSource.FLIPKART,
                    KnownSource.MYNTRA,
                    KnownSource.GENERIC
                ),
                priceMin = 122.0,
                priceMax = 455.0,
                currencyCode = "USD"
            ),
            onClick = {},
            modifier = Modifier.padding(
                WistDimensions.SpacingLg
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun WishlistCardBorderedPreview() {
    WistTheme {
        WishlistCardBordered(
            data = WishlistDisplayData(
                id = "2",
                name = "My shoe list",
                dateLabel = "from Apr 25",
                productImages = listOf("img1", "img2"),
                sources = listOf(
                    KnownSource.AMAZON,
                    KnownSource.FLIPKART
                ),
                priceMin = 82.0,
                priceMax = 124.0,
                currencyCode = "USD"
            ),
            onClick = {},
            modifier = Modifier.padding(
                WistDimensions.SpacingLg
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun WishlistCardListPreview() {
    WistTheme {
        Column(
            modifier = Modifier.padding(
                WistDimensions.SpacingLg
            ),
            verticalArrangement = Arrangement.spacedBy(WistDimensions.SpacingSm)
        ) {
            listOf(
                WishlistDisplayData(
                    id = "1",
                    name = "Phones",
                    dateLabel = "from Mar 25",
                    productImages = listOf("img1", "img2", "img3", "img4"),
                    sources = listOf(
                        KnownSource.AMAZON,
                        KnownSource.FLIPKART,
                        KnownSource.MYNTRA,
                        KnownSource.GENERIC
                    ),
                    priceMin = 122.0,
                    priceMax = 455.0
                ),
                WishlistDisplayData(
                    id = "2",
                    name = "My shoe list",
                    dateLabel = "from Apr 25",
                    productImages = listOf("img1", "img2"),
                    sources = listOf(
                        KnownSource.AMAZON,
                        KnownSource.FLIPKART
                    ),
                    priceMin = 82.0,
                    priceMax = 124.0
                )
            ).forEach { wishlist ->
                WishlistCardBordered(
                    data = wishlist,
                    onClick = {}
                )
            }
        }
    }
}
