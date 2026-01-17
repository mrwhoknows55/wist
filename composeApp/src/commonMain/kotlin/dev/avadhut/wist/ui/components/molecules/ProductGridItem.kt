package dev.avadhut.wist.ui.components.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.avadhut.wist.ui.theme.BackgroundCard
import dev.avadhut.wist.ui.theme.TextDisabled
import dev.avadhut.wist.ui.theme.WistDimensions
import dev.avadhut.wist.ui.theme.WistTheme

/**
 * Product Grid Item - Square thumbnail for product grids
 *
 * Used in wishlist cards and similar grid displays.
 * Shows a product image thumbnail or placeholder.
 *
 * @param imageUrl URL of the product image (nullable)
 * @param contentDescription Accessibility description
 * @param onClick Click handler
 * @param modifier Modifier for customization
 * @param size Size of the grid item
 */
@Composable
fun ProductGridItem(
    imageUrl: String?,
    contentDescription: String?,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    size: Dp = WistDimensions.ThumbnailSmall
) {
    val clickableModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(WistDimensions.SpacingXs))
            .background(BackgroundCard)
            .then(clickableModifier),
        contentAlignment = Alignment.Center
    ) {
        if (imageUrl != null) {
            // In a real app, use Coil or similar for image loading
            // For now, show placeholder
            ProductImagePlaceholder(
                modifier = Modifier.fillMaxSize()
            )
        } else {
            ProductImagePlaceholder(
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * Product Image Placeholder - Generic placeholder for missing images
 */
@Composable
fun ProductImagePlaceholder(
    modifier: Modifier = Modifier,
    backgroundColor: Color = BackgroundCard,
    iconTint: Color = TextDisabled
) {
    Box(
        modifier = modifier.background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Image,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
        )
    }
}

/**
 * Product Thumbnail Grid - 2x2 grid of product thumbnails
 *
 * Used in WishlistCard to show preview of items.
 *
 * @param imageUrls List of image URLs (max 4 displayed)
 * @param modifier Modifier for customization
 */
@Composable
fun ProductThumbnailGrid(
    imageUrls: List<String?>,
    modifier: Modifier = Modifier
) {
    val displayItems = imageUrls.take(4) + List(maxOf(0, 4 - imageUrls.size)) { null }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(WistDimensions.SpacingXxs)
    ) {
        // Row 1
        androidx.compose.foundation.layout.Row(
            horizontalArrangement = Arrangement.spacedBy(WistDimensions.SpacingXxs)
        ) {
            ProductGridItem(
                imageUrl = displayItems.getOrNull(0),
                contentDescription = "Product 1",
                modifier = Modifier.weight(1f).aspectRatio(1f)
            )
            ProductGridItem(
                imageUrl = displayItems.getOrNull(1),
                contentDescription = "Product 2",
                modifier = Modifier.weight(1f).aspectRatio(1f)
            )
        }
        // Row 2
        androidx.compose.foundation.layout.Row(
            horizontalArrangement = Arrangement.spacedBy(WistDimensions.SpacingXxs)
        ) {
            ProductGridItem(
                imageUrl = displayItems.getOrNull(2),
                contentDescription = "Product 3",
                modifier = Modifier.weight(1f).aspectRatio(1f)
            )
            ProductGridItem(
                imageUrl = displayItems.getOrNull(3),
                contentDescription = "Product 4",
                modifier = Modifier.weight(1f).aspectRatio(1f)
            )
        }
    }
}


// PREVIEWS


@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun ProductGridItemPreview() {
    WistTheme {
        ProductGridItem(
            imageUrl = null,
            contentDescription = "iPhone 15",
            modifier = Modifier.padding(
                WistDimensions.SpacingSm
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun ProductGridItemClickablePreview() {
    WistTheme {
        ProductGridItem(
            imageUrl = "https://example.com/image.jpg",
            contentDescription = "OnePlus 13R",
            onClick = {},
            modifier = Modifier.padding(
                WistDimensions.SpacingSm
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun ProductGridItemLargePreview() {
    WistTheme {
        ProductGridItem(
            imageUrl = null,
            contentDescription = "Product",
            size = WistDimensions.ThumbnailMedium,
            modifier = Modifier.padding(
                WistDimensions.SpacingSm
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun ProductThumbnailGridPreview() {
    WistTheme {
        ProductThumbnailGrid(
            imageUrls = listOf(
                "image1.jpg",
                "image2.jpg",
                "image3.jpg",
                "image4.jpg"
            ),
            modifier = Modifier
                .padding(WistDimensions.SpacingSm)
                .size(120.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun ProductThumbnailGridPartialPreview() {
    WistTheme {
        ProductThumbnailGrid(
            imageUrls = listOf("image1.jpg", "image2.jpg"),
            modifier = Modifier
                .padding(WistDimensions.SpacingSm)
                .size(120.dp)
        )
    }
}
