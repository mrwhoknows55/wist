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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import dev.avadhut.wist.ui.components.atoms.KnownSource
import dev.avadhut.wist.ui.components.atoms.PriceTag
import dev.avadhut.wist.ui.components.atoms.SourceIcon
import dev.avadhut.wist.ui.theme.BackgroundCard
import dev.avadhut.wist.ui.theme.TextDisabled
import dev.avadhut.wist.ui.theme.TextPrimary
import dev.avadhut.wist.ui.theme.WistDimensions
import dev.avadhut.wist.ui.theme.WistTheme

/**
 * Data class representing a product for list display
 */
data class ProductListItemData(
    val id: String,
    val title: String,
    val price: Double,
    val currencyCode: String = "USD",
    val source: KnownSource,
    val imageUrl: String? = null
)

/**
 * Product List Item - Horizontal product row for list detail
 *
 * Displays:
 * - Product thumbnail (left)
 * - Title
 * - Price tag
 * - Source icon with name
 *
 * @param data Product display data
 * @param onClick Click handler
 * @param modifier Modifier for customization
 */
@Composable
fun ProductListItem(
    data: ProductListItemData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                horizontal = WistDimensions.ScreenPaddingHorizontal,
                vertical = WistDimensions.SpacingSm
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Product Image
        ProductListItemImage(
            imageUrl = data.imageUrl,
            modifier = Modifier.size(
                WistDimensions.ThumbnailLarge
            )
        )

        Spacer(modifier = Modifier.width(
            WistDimensions.SpacingLg)
        )

        // Product Info
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(WistDimensions.SpacingXs)
        ) {
            // Title
            Text(
                text = data.title,
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Price
            PriceTag(
                price = data.price,
                currencyCode = data.currencyCode
            )

            Spacer(modifier = Modifier.height(
                WistDimensions.SpacingXs)
            )

            // Source
            SourceIcon(
                source = data.source,
                showLabel = true
            )
        }
    }
}

/**
 * Product List Item Image - Thumbnail with placeholder
 */
@Composable
private fun ProductListItemImage(
    imageUrl: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(WistDimensions.SpacingSm))
            .background(BackgroundCard),
        contentAlignment = Alignment.Center
    ) {
        // In production, use Coil or similar for image loading
        // For now, show placeholder
        Icon(
            imageVector = Icons.Filled.Image,
            contentDescription = null,
            tint = TextDisabled,
            modifier = Modifier.size(
                WistDimensions.IconSizeLarge)
        )
    }
}


// PREVIEWS


@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun ProductListItemPreview() {
    WistTheme {
        ProductListItem(
            data = ProductListItemData(
                id = "1",
                title = "Oneplus 13R",
                price = 12999.0,
                currencyCode = "USD",
                source = KnownSource.FLIPKART
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun ProductListItemAmazonPreview() {
    WistTheme {
        ProductListItem(
            data = ProductListItemData(
                id = "2",
                title = "OnePlus Nord 5 5G (Dry Ice, 256 GB)",
                price = 12999.0,
                currencyCode = "USD",
                source = KnownSource.AMAZON
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun ProductListItemMyntraPreview() {
    WistTheme {
        ProductListItem(
            data = ProductListItemData(
                id = "3",
                title = "Nike Running Shoes Pro",
                price = 149.99,
                currencyCode = "USD",
                source = KnownSource.MYNTRA
            ),
            onClick = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun ProductListItemListPreview() {
    WistTheme {
        Column {
            listOf(
                ProductListItemData(
                    id = "1",
                    title = "Oneplus 13R",
                    price = 12999.0,
                    source = KnownSource.FLIPKART
                ),
                ProductListItemData(
                    id = "2",
                    title = "OnePlus Nord 5 5G (Dry Ice, 256 GB) (8 GB RAM)",
                    price = 12999.0,
                    source = KnownSource.AMAZON
                ),
                ProductListItemData(
                    id = "3",
                    title = "iPhone 15 Pro Max",
                    price = 1199.0,
                    source = KnownSource.GENERIC
                )
            ).forEach { product ->
                ProductListItem(
                    data = product,
                    onClick = {}
                )
            }
        }
    }
}
