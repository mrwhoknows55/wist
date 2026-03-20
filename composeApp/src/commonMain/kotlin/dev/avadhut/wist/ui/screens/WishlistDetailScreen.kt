package dev.avadhut.wist.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import dev.avadhut.wist.client.WistApiClient
import dev.avadhut.wist.client.util.userVisibleMessage
import dev.avadhut.wist.core.dto.WishlistDto
import dev.avadhut.wist.core.dto.WishlistItemDto
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
import dev.avadhut.wist.ui.theme.TextPrimary
import dev.avadhut.wist.ui.theme.WistDimensions
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistDetailScreen(
    apiClient: WistApiClient, wishlistId: Int, onBack: () -> Unit
) {
    var wishlist by remember { mutableStateOf<WishlistDto?>(null) }
    var items by remember { mutableStateOf<List<WishlistItemDto>>(emptyList()) }
    var allWishlists by remember { mutableStateOf<List<WishlistDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var showAddSheet by remember { mutableStateOf(false) }
    var isAddingItem by remember { mutableStateOf(false) }
    var addItemError by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current
    val clipboardManager = LocalClipboardManager.current
    var clipboardContent by remember { mutableStateOf<String?>(null) }

    suspend fun loadDetail(forceRemote: Boolean) {
        isLoading = true
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
    }

    LaunchedEffect(wishlistId) {
        loadDetail(apiClient.wishlistDetailForceRemote(wishlistId))
    }

    // Sheet State
    val sheetState = rememberModalBottomSheetState()
    var urlToAdd by remember { mutableStateOf("") }
    var selectedListNames by remember { mutableStateOf(setOf<String>()) } // Selected list names

    // Update selectedListNames when wishlist loads
    LaunchedEffect(wishlist) {
        wishlist?.let {
            selectedListNames = setOf(it.name)
        }
    }

    // Check clipboard when sheet opens (simulation)
    LaunchedEffect(showAddSheet) {
        if (showAddSheet) {
            val clip = clipboardManager.getText()?.text
            if (clip != null && (clip.startsWith("http") || clip.startsWith("www"))) {
                clipboardContent = clip
                // Optionally auto-fill if empty
                if (urlToAdd.isEmpty()) urlToAdd = clip
            }
            addItemError = null
        }
    }

    Scaffold(topBar = {
        WistDetailTopAppBar(
            title = wishlist?.name ?: "Loading...", onBackClick = onBack
        )
    }, bottomBar = {
        BottomActionArea(
            primaryText = "Add product",
            secondaryText = "", // No secondary action needed
            onPrimaryClick = { showAddSheet = true },
            showSecondary = false,
            onSecondaryClick = {})
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
                LazyColumn(
                    contentPadding = PaddingValues(WistDimensions.ScreenPaddingHorizontal),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Spacer(modifier = Modifier.height(WistDimensions.SpacingLg))
                    }
                    if (items.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 240.dp)
                                    .padding(vertical = WistDimensions.SpacingXxl),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No items yet. Add one!",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = TextPrimary
                                )
                            }
                        }
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
                                ), onClick = {
                                    uriHandler.openUri(item.sourceUrl)
                                }, modifier = Modifier.padding(vertical = WistDimensions.SpacingSm)
                            )
                        }
                    }
                }
            }
        }
    }

    // Add Link Sheet
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
                selectedListNames = if (selected) {
                    selectedListNames + name
                } else {
                    selectedListNames - name
                }
            },
            onCreateNewList = {
                // TODO: Handle create new list from sheet
            },
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
                                        addItemError = e.userVisibleMessage("Failed to add item")
                                        hasFailure = true
                                        println("[Wist] WishlistDetailScreen: addItem failed listId=${list.id} msg=${e.userVisibleMessage()}")
                                    }
                            }
                            if (!hasFailure) {
                                showAddSheet = false
                                urlToAdd = ""
                                apiClient.invalidateWishlistDetail(wishlistId)
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
