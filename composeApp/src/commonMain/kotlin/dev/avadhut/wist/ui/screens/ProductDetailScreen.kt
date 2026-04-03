package dev.avadhut.wist.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import dev.avadhut.wist.core.dto.WishlistItemDto
import dev.avadhut.wist.ui.components.atoms.WistIconButton
import dev.avadhut.wist.ui.components.atoms.detectSourceForWishlistItem
import dev.avadhut.wist.ui.components.organisms.ProductDetailCard
import dev.avadhut.wist.ui.components.organisms.ProductDetailData
import dev.avadhut.wist.ui.components.organisms.WistDetailTopAppBar
import kotlinx.coroutines.launch
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
    onOpenWebView: (String) -> Unit,
    isSecondOpinionDismissed: Boolean = false,
    onDismissSecondOpinion: () -> Unit = {}
) {
    val uriHandler = LocalUriHandler.current
    val dateLabel = ItemCreatedDateFormat.format(item.createdAt.date)
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val redditUrl = remember(item.productName) {
        val query = (item.productName ?: item.sourceUrl).replace(" ", "+")
        "https://www.reddit.com/search/?q=$query+review"
    }

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
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
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
            onRedditClick = { uriHandler.openUri(redditUrl) },
            onComingSoonTap = {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Coming soon!")
                }
            },
            isSecondOpinionDismissed = isSecondOpinionDismissed,
            onDismissSecondOpinion = onDismissSecondOpinion,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        )
    }
}
