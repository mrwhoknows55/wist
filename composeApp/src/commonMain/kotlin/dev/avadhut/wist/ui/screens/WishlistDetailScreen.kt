package dev.avadhut.wist.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.unit.dp
import dev.avadhut.wist.client.WistApiClient
import dev.avadhut.wist.client.util.ApiException
import dev.avadhut.wist.client.util.userVisibleMessage
import dev.avadhut.wist.core.dto.WishlistDto
import dev.avadhut.wist.core.dto.WishlistItemDto
import dev.avadhut.wist.ui.clipboard.readPlainTextOrNull
import dev.avadhut.wist.ui.components.atoms.WistButton
import dev.avadhut.wist.ui.components.atoms.WistButtonStyle
import dev.avadhut.wist.ui.components.atoms.detectSourceForWishlistItem
import dev.avadhut.wist.ui.components.molecules.DetailListLoadingContent
import dev.avadhut.wist.ui.components.molecules.LoadErrorWithRetry
import dev.avadhut.wist.ui.components.organisms.AddLinkBottomSheet
import dev.avadhut.wist.ui.components.organisms.AddLinkBottomSheetContent
import dev.avadhut.wist.ui.components.organisms.BottomActionArea
import dev.avadhut.wist.ui.components.organisms.ClipboardItem
import dev.avadhut.wist.ui.components.organisms.ProductListItem
import dev.avadhut.wist.ui.components.organisms.ProductListItemData
import dev.avadhut.wist.ui.components.organisms.WistDetailTopAppBar
import dev.avadhut.wist.ui.theme.AlertRed
import dev.avadhut.wist.ui.theme.BackgroundCard
import dev.avadhut.wist.ui.theme.BorderDefault
import dev.avadhut.wist.ui.theme.TextPrimary
import dev.avadhut.wist.ui.theme.TextSecondary
import dev.avadhut.wist.ui.theme.WistDimensions
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistDetailScreen(
    apiClient: WistApiClient,
    wishlistId: Int,
    onBack: () -> Unit,
    onItemClick: (WishlistItemDto) -> Unit = {}
) {
    var wishlist by remember { mutableStateOf<WishlistDto?>(null) }
    var items by remember { mutableStateOf<List<WishlistItemDto>>(emptyList()) }
    var allWishlists by remember { mutableStateOf<List<WishlistDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isPullRefreshing by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var showAddSheet by remember { mutableStateOf(false) }
    var isAddingItem by remember { mutableStateOf(false) }
    var addItemError by remember { mutableStateOf<String?>(null) }
    var itemToDelete by remember { mutableStateOf<WishlistItemDto?>(null) }
    var isDeletingItem by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val clipboard = LocalClipboard.current
    var clipboardContent by remember { mutableStateOf<String?>(null) }

    suspend fun loadDetail(forceRemote: Boolean, pullRefresh: Boolean = false) {
        if (pullRefresh) {
            isPullRefreshing = true
        } else {
            isLoading = true
        }
        error = null
        val wlResult = apiClient.wishlistData.getWishlist(wishlistId, forceRemote = forceRemote)
        wlResult.onSuccess { wishlist = it }
            .onFailure { e ->
                error = e.userVisibleMessage("Failed to load wishlist")
                println("[Wist] WishlistDetailScreen: getWishlist failed id=$wishlistId msg=${e.userVisibleMessage()}")
            }

        val itemsResult =
            apiClient.wishlistData.getWishlistItems(wishlistId, forceRemote = forceRemote)
        itemsResult.onSuccess {
            items = it
            println("[Wist] WishlistDetailScreen: loaded ${it.size} items wishlistId=$wishlistId forceRemote=$forceRemote")
        }.onFailure { e ->
            println("[Wist] WishlistDetailScreen: failed to load items wishlistId=$wishlistId msg=${e.userVisibleMessage()}")
            if (error == null) error = e.userVisibleMessage("Failed to load items")
        }

        apiClient.wishlistData.getAllWishlists(forceRemote = false).onSuccess { allWishlists = it }
            .onFailure { e ->
                println("[Wist] WishlistDetailScreen: getAllWishlists cache path failed msg=${e.userVisibleMessage()}")
            }

        if (forceRemote && wlResult.isSuccess && itemsResult.isSuccess) {
            apiClient.markWishlistDetailSyncedFromRemote(wishlistId)
        }
        isLoading = false
        isPullRefreshing = false
    }

    LaunchedEffect(wishlistId) {
        loadDetail(apiClient.wishlistDetailForceRemote(wishlistId))
    }

    val sheetState = rememberModalBottomSheetState()
    var urlToAdd by remember { mutableStateOf("") }
    var selectedListNames by remember { mutableStateOf(setOf<String>()) }

    LaunchedEffect(wishlist) {
        wishlist?.let { selectedListNames = setOf(it.name) }
    }

    LaunchedEffect(showAddSheet) {
        if (showAddSheet) {
            val clip = runCatching { clipboard.readPlainTextOrNull() }
                .onFailure { println("[Wist] WishlistDetailScreen: clipboard read failed ${it.message}") }
                .getOrNull()
            if (clip != null && (clip.startsWith("http") || clip.startsWith("www"))) {
                clipboardContent = clip
                if (urlToAdd.isEmpty()) urlToAdd = clip
            }
            addItemError = null
        }
    }

    Scaffold(topBar = {
        WistDetailTopAppBar(
            title = wishlist?.name ?: "Loading…", onBackClick = onBack
        )
    }, bottomBar = {
        BottomActionArea(
            primaryText = "Add product",
            secondaryText = "",
            onPrimaryClick = { showAddSheet = true },
            showSecondary = false,
            onSecondaryClick = {}
        )
    }) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading) {
                DetailListLoadingContent(modifier = Modifier.fillMaxSize())
            } else if (error != null) {
                LoadErrorWithRetry(
                    message = error!!,
                    onRetry = {
                        println("[Wist] WishlistDetailScreen: error retry tapped")
                        scope.launch { loadDetail(forceRemote = true) }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                PullToRefreshBox(
                    isRefreshing = isPullRefreshing,
                    onRefresh = {
                        scope.launch { loadDetail(forceRemote = true, pullRefresh = true) }
                    },
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        contentPadding = PaddingValues(WistDimensions.ScreenPaddingHorizontal),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item { Spacer(modifier = Modifier.height(WistDimensions.SpacingLg)) }

                        if (items.isEmpty()) {
                            item { EmptyWishlistState(onAddClick = { showAddSheet = true }) }
                        } else {
                            items(items) { item ->
                                ProductListItem(
                                    data = ProductListItemData(
                                        id = item.id.toString(),
                                        title = item.productName ?: item.sourceUrl,
                                        price = item.price,
                                        currencyCode = item.currency?.takeIf { it.isNotBlank() }
                                            ?: "USD",
                                        source = detectSourceForWishlistItem(
                                            item.retailerDomain,
                                            item.retailerName,
                                            item.sourceUrl
                                        ),
                                        imageUrl = item.imageUrl
                                    ),
                                    onClick = { onItemClick(item) },
                                    onDeleteClick = { itemToDelete = item },
                                    modifier = Modifier.padding(vertical = WistDimensions.SpacingSm)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete confirm dialog
    itemToDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { if (!isDeletingItem) itemToDelete = null },
            title = { Text("Remove item?") },
            text = {
                Text(
                    "\"${item.productName ?: item.sourceUrl}\" will be removed from this wishlist.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            isDeletingItem = true
                            apiClient.wishlistItems.deleteItem(wishlistId, item.id)
                                .onSuccess {
                                    itemToDelete = null
                                    apiClient.invalidateWishlistDetail(wishlistId)
                                    apiClient.invalidateWishlistList()
                                    loadDetail(forceRemote = true)
                                }
                                .onFailure { e ->
                                    println("[Wist] WishlistDetailScreen: deleteItem failed id=${item.id} msg=${e.userVisibleMessage()}")
                                }
                            isDeletingItem = false
                        }
                    },
                    enabled = !isDeletingItem
                ) {
                    Text(
                        if (isDeletingItem) "Removing…" else "Remove",
                        color = AlertRed
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }, enabled = !isDeletingItem) {
                    Text("Cancel")
                }
            }
        )
    }

    AddLinkBottomSheet(
        isVisible = showAddSheet,
        onDismiss = { showAddSheet = false },
        sheetState = sheetState,
    ) {
        AddLinkBottomSheetContent(
            urlValue = urlToAdd,
            onUrlChange = { urlToAdd = it },
            clipboardItems = if (clipboardContent != null) listOf(ClipboardItem(clipboardContent!!)) else emptyList(),
            onClipboardItemClick = { urlToAdd = it.url },
            availableLists = allWishlists.map { it.name },
            selectedLists = selectedListNames,
            onListSelectionChange = { name, selected ->
                selectedListNames =
                    if (selected) selectedListNames + name else selectedListNames - name
            },
            onCreateNewList = {},
            onConfirm = {
                if (!isAddingItem) {
                    scope.launch {
                        val trimmedUrl = urlToAdd.trim()
                        if (trimmedUrl.isBlank()) {
                            addItemError = "Enter a product link"
                            return@launch
                        }
                        if (selectedListNames.isEmpty()) {
                            addItemError = "Pick at least one wishlist"
                            return@launch
                        }
                        isAddingItem = true
                        addItemError = null
                        try {
                            val targetLists = allWishlists.filter { it.name in selectedListNames }
                            if (targetLists.isEmpty()) {
                                addItemError = "No matching wishlists. Go back and refresh."
                                println("[Wist] WishlistDetailScreen: add product aborted, targetLists empty")
                                return@launch
                            }
                            var hasFailure = false
                            for (list in targetLists) {
                                if (hasFailure) break
                                apiClient.wishlistItems.addItemToWishlist(list.id, trimmedUrl)
                                    .onFailure { e ->
                                        val status = (e as? ApiException)?.httpStatusCode
                                        addItemError = when (status) {
                                            502 -> "Couldn't read that product page. Check the URL is a direct product link and try again."
                                            400 -> e.userVisibleMessage("Invalid URL — paste a direct product link.")
                                            else -> e.userVisibleMessage("Failed to add item")
                                        }
                                        hasFailure = true
                                        println("[Wist] WishlistDetailScreen: addItem failed listId=${list.id} status=$status msg=${e.userVisibleMessage()}")
                                    }
                            }
                            if (!hasFailure) {
                                showAddSheet = false
                                urlToAdd = ""
                                apiClient.invalidateWishlistDetail(wishlistId)
                                apiClient.invalidateWishlistList()
                                loadDetail(forceRemote = true)
                            }
                        } finally {
                            isAddingItem = false
                        }
                    }
                }
            },
            onClose = { showAddSheet = false },
            isLoading = isAddingItem,
            errorMessage = addItemError
        )
    }
}

@Composable
private fun EmptyWishlistState(onAddClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 240.dp)
            .padding(vertical = WistDimensions.SpacingXxl)
            .clip(RoundedCornerShape(WistDimensions.CardRadius))
            .background(BackgroundCard)
            .border(1.dp, BorderDefault, RoundedCornerShape(WistDimensions.CardRadius)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(WistDimensions.CardPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Nothing here yet",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(WistDimensions.SpacingXs))
                Text(
                    text = "Paste a product link to add your first item.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(WistDimensions.SpacingLg))
                WistButton(
                    text = "Add product",
                    onClick = onAddClick,
                    style = WistButtonStyle.PRIMARY,
                    modifier = Modifier.width(140.dp)
                )
            }
        }
    }
}
