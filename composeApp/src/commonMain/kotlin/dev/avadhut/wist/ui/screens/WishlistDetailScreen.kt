package dev.avadhut.wist.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import dev.avadhut.wist.client.WistApiClient
import dev.avadhut.wist.core.dto.WishlistDto
import dev.avadhut.wist.core.dto.WishlistItemDto
import dev.avadhut.wist.ui.components.atoms.detectSourceFromUrl
import dev.avadhut.wist.ui.components.organisms.AddLinkBottomSheet
import dev.avadhut.wist.ui.components.organisms.AddLinkBottomSheetContent
import dev.avadhut.wist.ui.components.organisms.BottomActionArea
import dev.avadhut.wist.ui.components.organisms.ClipboardItem
import dev.avadhut.wist.ui.components.organisms.ProductListItem
import dev.avadhut.wist.ui.components.organisms.ProductListItemData
import dev.avadhut.wist.ui.components.organisms.WistDetailTopAppBar
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

    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    var clipboardContent by remember { mutableStateOf<String?>(null) }

    // Fetch Data
    fun loadData() {
        scope.launch {
            isLoading = true
            // Load Wishlist Details
            apiClient.wishlists.getWishlist(wishlistId).onSuccess { wishlist = it }
                .onFailure { error = it.message ?: "Failed to load wishlist" }

            // Load Items
            apiClient.wishlistItems.getWishlistItems(wishlistId).onSuccess { items = it }
                .onFailure { println("Failed to load items: ${it.message}") }

            // Load All Wishlists for the sheet
            apiClient.wishlists.getAllWishlists().onSuccess { allWishlists = it }

            isLoading = false
        }
    }

    LaunchedEffect(wishlistId) {
        loadData()
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
            onSecondaryClick = {})
    }) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (error != null) {
                Text("Error: $error", modifier = Modifier.align(Alignment.Center))
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
                            Text(
                                "No items yet. Add one!",
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    } else {
                        items(items) { item ->
                            ProductListItem(
                                data = ProductListItemData(
                                    id = item.id.toString(),
                                    title = item.productName ?: item.sourceUrl,
                                    price = item.price ?: 0.0,
                                    source = detectSourceFromUrl(item.sourceUrl)
                                ), onClick = {
                                    // TODO: Open detail or edit
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
                scope.launch {
                    val targetLists = allWishlists.filter { it.name in selectedListNames }
                    targetLists.forEach { list ->
                        apiClient.wishlistItems.addItemToWishlist(list.id, urlToAdd)
                    }
                    showAddSheet = false
                    urlToAdd = ""
                    loadData() // Refresh
                }
            },
            onClose = { showAddSheet = false })
    }
}
