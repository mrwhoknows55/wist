package dev.avadhut.wist.ui.components.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.avadhut.wist.ui.theme.BackgroundCard
import dev.avadhut.wist.ui.theme.TextPrimary
import dev.avadhut.wist.ui.theme.WistDimensions
import dev.avadhut.wist.ui.theme.WistTheme
import dev.avadhut.wist.util.formatCurrency

/**
 * Price Tag - Currency-formatted price display
 *
 * Displays a price with proper currency formatting.
 * Uses a distinct visual style with background container.
 *
 * @param price The price value
 * @param currencyCode ISO 4217 currency code (e.g., "USD", "INR")
 * @param modifier Modifier for customization
 * @param showContainer Whether to show a background container
 */
@Composable
fun PriceTag(
    price: Double,
    currencyCode: String = "USD",
    modifier: Modifier = Modifier,
    showContainer: Boolean = true
) {
    val formattedPrice = formatCurrency(
        price = price,
        currencyCode = currencyCode,
        showCurrencySymbol = true
    )

    if (showContainer) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(4.dp))
                .background(BackgroundCard)
                .padding(horizontal = WistDimensions.SpacingSm, vertical = WistDimensions.SpacingXs)
        ) {
            Text(
                text = formattedPrice,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = TextPrimary
            )
        }
    } else {
        Text(
            text = formattedPrice,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = TextPrimary,
            modifier = modifier
        )
    }
}

/**
 * Price Range Tag - Displays min-max price range
 *
 * @param minPrice Minimum price
 * @param maxPrice Maximum price
 * @param currencyCode ISO 4217 currency code
 * @param modifier Modifier for customization
 */
@Composable
fun PriceRangeTag(
    minPrice: Double,
    maxPrice: Double,
    currencyCode: String = "USD",
    modifier: Modifier = Modifier
) {
    val formattedMin = formatCurrency(
        price = minPrice,
        currencyCode = currencyCode,
        showCurrencySymbol = true
    )
    val formattedMax = formatCurrency(
        price = maxPrice,
        currencyCode = currencyCode,
        showCurrencySymbol = false
    )

    Text(
        text = "$formattedMin - $formattedMax",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
    )
}


// PREVIEWS


@Preview
@Composable
private fun PriceTagUsdPreview() {
    WistTheme {
        PriceTag(
            price = 12999.0,
            currencyCode = "USD"
        )
    }
}

@Preview
@Composable
private fun PriceTagInrPreview() {
    WistTheme {
        PriceTag(
            price = 12999.0,
            currencyCode = "INR"
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun PriceTagNoContainerPreview() {
    WistTheme {
        PriceTag(
            price = 49.99,
            currencyCode = "USD",
            showContainer = false
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun PriceRangeTagPreview() {
    WistTheme {
        PriceRangeTag(
            minPrice = 122.0,
            maxPrice = 455.0,
            currencyCode = "USD"
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun PriceTagColumnPreview() {
    WistTheme {
        Column {
            PriceTag(
                price = 12999.0,
                currencyCode = "USD"
            )
            PriceTag(
                price = 499.99,
                currencyCode = "EUR"
            )
            PriceRangeTag(
                minPrice = 82.0,
                maxPrice = 124.0,
                currencyCode = "USD"
            )
        }
    }
}
