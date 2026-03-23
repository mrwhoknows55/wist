package dev.avadhut.wist.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import dev.avadhut.wist.core.dto.WishlistItemDto
import dev.avadhut.wist.ui.components.atoms.WistIconButton
import dev.avadhut.wist.ui.components.atoms.detectSourceForWishlistItem
import dev.avadhut.wist.ui.components.organisms.ProductDetailCard
import dev.avadhut.wist.ui.components.organisms.ProductDetailData
import dev.avadhut.wist.ui.components.organisms.WistDetailTopAppBar
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char

private val ItemCreatedDateFormat = LocalDate.Format {
    monthName(MonthNames.ENGLISH_ABBREVIATED)
    char(' ')
    day()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    item: WishlistItemDto,
    onBack: () -> Unit,
    onOpenWebView: (String) -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val dateLabel = ItemCreatedDateFormat.format(item.createdAt.date)

    Scaffold(
        topBar = {
            WistDetailTopAppBar(
                title = item.productName ?: "Product",
                onBackClick = onBack,
                actions = {
                    WistIconButton(
                        icon = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = "Open externally",
                        onClick = { uriHandler.openUri(item.sourceUrl) }
                    )
                }
            )
        }
    ) { padding ->
        ProductDetailCard(
            data = ProductDetailData(
                id = item.id.toString(),
                title = item.productName ?: item.sourceUrl,
                description = item.productDescription,
                price = item.price ?: 0.0,
                currencyCode = item.currency?.takeIf { it.isNotBlank() } ?: "USD",
                source = detectSourceForWishlistItem(
                    item.retailerDomain,
                    item.retailerName,
                    item.sourceUrl
                ),
                sourceUrl = item.sourceUrl,
                imageUrl = item.imageUrl,
                dateCreated = dateLabel
            ),
            onSourceClick = { onOpenWebView(item.sourceUrl) },
            onNotifyClick = {},
            onFindBestPriceClick = {},
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        )
    }
}
