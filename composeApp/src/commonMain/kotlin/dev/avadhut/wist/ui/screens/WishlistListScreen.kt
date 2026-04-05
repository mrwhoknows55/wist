package dev.avadhut.wist.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.unit.dp
import dev.avadhut.wist.client.WistApiClient
import dev.avadhut.wist.client.util.ApiException
import dev.avadhut.wist.client.util.userVisibleMessage
import dev.avadhut.wist.core.dto.WishlistDto
import dev.avadhut.wist.ui.clipboard.readPlainTextOrNull
import dev.avadhut.wist.ui.components.atoms.KnownSource
import dev.avadhut.wist.ui.components.atoms.SourceIcon
import dev.avadhut.wist.ui.components.atoms.WistButton
import dev.avadhut.wist.ui.components.atoms.WistButtonStyle
import dev.avadhut.wist.ui.components.atoms.WistIconButton
import dev.avadhut.wist.ui.components.molecules.HomeListLoadingContent
import dev.avadhut.wist.ui.components.molecules.LoadErrorWithRetry
import dev.avadhut.wist.ui.components.molecules.SearchInput
import dev.avadhut.wist.ui.components.organisms.AddLinkBottomSheet
import dev.avadhut.wist.ui.components.organisms.AddLinkBottomSheetContent
import dev.avadhut.wist.ui.components.organisms.BottomActionArea
import dev.avadhut.wist.ui.components.organisms.ClipboardItem
import dev.avadhut.wist.ui.components.organisms.WishlistDisplayData
import dev.avadhut.wist.ui.components.organisms.WishlistListItem
import dev.avadhut.wist.ui.components.organisms.WistHomeTopAppBar
import dev.avadhut.wist.ui.theme.AlertRed
import dev.avadhut.wist.ui.theme.TextPrimary
import dev.avadhut.wist.ui.theme.TextSecondary
import dev.avadhut.wist.ui.theme.WistDimensions
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char

private val WishlistCreatedDateDisplayFormat = LocalDate.Format {
    monthName(MonthNames.ENGLISH_ABBREVIATED)
    char(' ')
    day()
}

private fun wishlistCreatedAtLabel(createdAt: LocalDateTime): String =
    "from ${WishlistCreatedDateDisplayFormat.format(createdAt.date)}"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistListScreen(
    apiClient: WistApiClient,
    onWishlistClick: (Int) -> Unit,
    onLogout: () -> Unit = {},
    isSecondOpinionDismissed: Boolean = false,
    onDismissSecondOpinion: () -> Unit = {}
) {
    var wishlists by remember { mutableStateOf<List<WishlistDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var showAddSheet by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var isAddingItem by remember { mutableStateOf(false) }
    var addItemError by remember { mutableStateOf<String?>(null) }
    var createWishlistError by remember { mutableStateOf<String?>(null) }
    var isCreatingWishlist by remember { mutableStateOf(false) }
    var isPullRefreshing by remember { mutableStateOf(false) }
    var wishlistToDelete by remember { mutableStateOf<WishlistDto?>(null) }
    var isDeletingWishlist by remember { mutableStateOf(false) }
    var secondOpinionDismissed by remember { mutableStateOf(isSecondOpinionDismissed) }

    val scope = rememberCoroutineScope()
    val clipboard = LocalClipboard.current
    var clipboardContent by remember { mutableStateOf<String?>(null) }
    var urlToAdd by remember { mutableStateOf("") }
    var selectedListNames by remember { mutableStateOf(setOf<String>()) }
    val sheetState = rememberModalBottomSheetState()

    suspend fun loadWishlists(forceRemote: Boolean, pullRefresh: Boolean = false) {
        if (pullRefresh) {
            isPullRefreshing = true
        } else if (forceRemote || wishlists.isEmpty()) {
            isLoading = true
        }
        try {
            apiClient.wishlistData.getAllWishlists(forceRemote = forceRemote).onSuccess {
                wishlists = it
                error = null
                println("[Wist] WishlistListScreen: loaded ${it.size} wishlists forceRemote=$forceRemote pullRefresh=$pullRefresh")
            }.onFailure { e ->
                error = e.userVisibleMessage("Failed to load wishlists")
                println("[Wist] WishlistListScreen: failed to load wishlists msg=${e.userVisibleMessage()}")
            }
        } finally {
            isLoading = false
            isPullRefreshing = false
        }
    }

    val wishlistListVersion by apiClient.wishlistListVersion.collectAsState()

    LaunchedEffect(wishlistListVersion) {
        loadWishlists(forceRemote = wishlistListVersion > 0 || apiClient.wishlistListForceRemoteForLaunch())
    }

    // Check clipboard when sheet opens
    LaunchedEffect(showAddSheet) {
        if (showAddSheet) {
            val clip = runCatching { clipboard.readPlainTextOrNull() }
                .onFailure { println("[Wist] WishlistListScreen: clipboard read failed ${it.message}") }
                .getOrNull()
            if (clip != null && (clip.startsWith("http") || clip.startsWith("www"))) {
                clipboardContent = clip
                if (urlToAdd.isEmpty()) urlToAdd = clip
            }
            addItemError = null
            if (wishlists.isNotEmpty() && selectedListNames.isEmpty()) {
                selectedListNames = setOf(wishlists.first().name)
            }
        }
    }

    Scaffold(topBar = {
        WistHomeTopAppBar(
            actions = {
                WistIconButton(
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Log out",
                    onClick = { showLogoutDialog = true }
                )
            }
        )
    }, bottomBar = {
        BottomActionArea(
            primaryText = "Add product",
            secondaryText = "Create New list",
            onPrimaryClick = { showAddSheet = true },
            onSecondaryClick = { showCreateDialog = true },
            primaryButtonStyle = WistButtonStyle.PRIMARY,
            secondaryButtonStyle = WistButtonStyle.SECONDARY
        )
    }) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading) {
                HomeListLoadingContent(modifier = Modifier.fillMaxSize())
            } else if (error != null) {
                LoadErrorWithRetry(
                    message = error!!, onRetry = {
                        println("[Wist] WishlistListScreen: error retry tapped")
                        scope.launch { loadWishlists(forceRemote = true) }
                    }, modifier = Modifier.fillMaxSize()
                )
            } else {
                PullToRefreshBox(
                    isRefreshing = isPullRefreshing,
                    onRefresh = {
                        scope.launch {
                            loadWishlists(forceRemote = true, pullRefresh = true)
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                ) {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = WistDimensions.ScreenPaddingHorizontal),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            SearchInput(
                                value = searchText,
                                onValueChange = { searchText = it },
                                onFilterClick = {})
                            Spacer(modifier = Modifier.height(WistDimensions.SpacingLg))
                        }

                        if (!secondOpinionDismissed) {
                            item {
                                SecondOpinionCard(
                                    onDismiss = {
                                        secondOpinionDismissed = true
                                        onDismissSecondOpinion()
                                    }
                                )
                            }
                        }

                        if (wishlists.isEmpty()) {
                            item {
                                FirstWishlistCard(onCreateClick = { showCreateDialog = true })
                            }
                        } else {
                            items(wishlists.filter {
                                it.name.contains(searchText, ignoreCase = true)
                            }) { wishlist ->
                                val dateLabel = wishlistCreatedAtLabel(wishlist.createdAt)
                                WishlistListItem(
                                    data = WishlistDisplayData(
                                        id = wishlist.id.toString(),
                                        name = wishlist.name,
                                        dateLabel = dateLabel,
                                        productImages = wishlist.thumbnailUrls.filter { it.isNotBlank() },
                                        sources = emptyList(),
                                        priceMin = 0.0,
                                        priceMax = 0.0
                                    ),
                                    onClick = { onWishlistClick(wishlist.id) },
                                    onDeleteClick = { wishlistToDelete = wishlist }
                                )
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(WistDimensions.SpacingXxl))
                        }
                    }
                }
            }
        }
    }

    // Logout confirm dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Log out?") },
            text = { Text("You'll need to sign in again to access your wishlists.") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    onLogout()
                }) { Text("Log out", color = AlertRed) }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Delete wishlist confirm dialog
    wishlistToDelete?.let { wishlist ->
        AlertDialog(
            onDismissRequest = { if (!isDeletingWishlist) wishlistToDelete = null },
            title = { Text("Delete wishlist?") },
            text = {
                Text(
                    "\"${wishlist.name}\" and all its items will be deleted.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            isDeletingWishlist = true
                            apiClient.wishlists.deleteWishlist(wishlist.id)
                                .onSuccess {
                                    wishlistToDelete = null
                                    loadWishlists(forceRemote = true)
                                }
                                .onFailure { e ->
                                    println("[Wist] WishlistListScreen: deleteWishlist failed id=${wishlist.id} msg=${e.userVisibleMessage()}")
                                }
                            isDeletingWishlist = false
                        }
                    },
                    enabled = !isDeletingWishlist
                ) {
                    Text(if (isDeletingWishlist) "Deleting…" else "Delete", color = AlertRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { wishlistToDelete = null }, enabled = !isDeletingWishlist) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showCreateDialog) {
        CreateWishlistDialog(
            errorMessage = createWishlistError,
            isLoading = isCreatingWishlist,
            onDismiss = {
                showCreateDialog = false
                createWishlistError = null
                isCreatingWishlist = false
            },
            onConfirm = { name ->
                if (isCreatingWishlist) return@CreateWishlistDialog
                scope.launch {
                    if (name.isBlank()) {
                        createWishlistError = "Name is required"
                        return@launch
                    }
                    isCreatingWishlist = true
                    apiClient.wishlists.createWishlist(name.trim()).onSuccess {
                        createWishlistError = null
                        loadWishlists(forceRemote = true)
                        showCreateDialog = false
                    }.onFailure { e ->
                        createWishlistError = e.userVisibleMessage("Could not create wishlist")
                        println("[Wist] WishlistListScreen: createWishlist failed msg=${e.userVisibleMessage()}")
                    }
                    isCreatingWishlist = false
                }
            },
            onClearError = { createWishlistError = null }
        )
    }

    AddLinkBottomSheet(
        isVisible = showAddSheet, onDismiss = { showAddSheet = false }, sheetState = sheetState
    ) {
        AddLinkBottomSheetContent(
            urlValue = urlToAdd,
            onUrlChange = { urlToAdd = it },
            clipboardItems = if (clipboardContent != null) listOf(ClipboardItem(clipboardContent!!)) else emptyList(),
            onClipboardItemClick = { urlToAdd = it.url },
            availableLists = wishlists.map { it.name },
            selectedLists = selectedListNames,
            onListSelectionChange = { name, selected ->
                selectedListNames =
                    if (selected) selectedListNames + name else selectedListNames - name
            },
            onCreateNewList = {
                // Optional: Allow creating list from sheet
            },
            onConfirm = {
                scope.launch {
                    if (isAddingItem) return@launch
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
                    val targetLists = wishlists.filter { it.name in selectedListNames }
                    if (targetLists.isEmpty()) {
                        addItemError =
                            "No matching wishlists. Pull to refresh or create a list first."
                        isAddingItem = false
                        println("[Wist] WishlistListScreen: add product aborted, targetLists empty")
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
                                println("[Wist] WishlistListScreen: addItem failed listId=${list.id} status=$status msg=${e.userVisibleMessage()}")
                            }
                    }
                    if (!hasFailure) {
                        targetLists.forEach { apiClient.invalidateWishlistDetail(it.id) }
                        showAddSheet = false
                        urlToAdd = ""
                        loadWishlists(forceRemote = true)
                    }
                    isAddingItem = false
                }
            },
            onClose = { showAddSheet = false },
            isLoading = isAddingItem,
            errorMessage = addItemError
        )
    }
}


@Composable
fun SecondOpinionCard(onDismiss: (() -> Unit)? = null) {
    Box(
        modifier = Modifier.padding(2.dp).fillMaxWidth().background(
            brush = Brush.horizontalGradient(
                colors = listOf(Color(0xFF1E1E1E), Color(0xFF121212))
            )
        ).height(140.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.weight(0.6f).padding(WistDimensions.CardPadding).fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Second Opinion",
                        style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary)
                    )
                    Text(
                        text = "on products you wish to buy",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                }
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(Color.Transparent)
                        .border(1.dp, Color.White, RoundedCornerShape(4.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        "Coming Soon",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            Box(
                modifier = Modifier.weight(0.4f).fillMaxSize().background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF333333), Color.Transparent)
                    )
                )
            )
        }
        if (onDismiss != null) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Dismiss",
                tint = TextSecondary,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(WistDimensions.SpacingSm)
                    .size(WistDimensions.IconSizeMedium)
                    .clickable { onDismiss() }
            )
        }
    }
}

@Composable
fun FirstWishlistCard(onCreateClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(WistDimensions.CardRadius))
            .background(Color.Black).border(
                1.dp,
                dev.avadhut.wist.ui.theme.BorderDefault,
                RoundedCornerShape(WistDimensions.CardRadius)
            ).height(160.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.weight(0.5f).padding(WistDimensions.CardPadding).fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Let's create your",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "First wishlist!",
                        color = TextPrimary,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }

                WistButton(
                    text = "Create",
                    onClick = onCreateClick,
                    style = WistButtonStyle.PRIMARY,
                    modifier = Modifier.width(100.dp)
                )
            }

            // Icons pile logic (visual placeholder)
            Box(
                modifier = Modifier.weight(0.5f).fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy((-10).dp)) {
                    // Fake icons
                    SourceIcon(KnownSource.FLIPKART, size = 40.dp)
                    SourceIcon(KnownSource.AMAZON, size = 40.dp)
                    SourceIcon(KnownSource.MYNTRA, size = 40.dp)
                }
            }
        }
    }
}

@Composable
fun CreateWishlistDialog(
    errorMessage: String?,
    isLoading: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    onClearError: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        modifier = Modifier.imePadding(),
        onDismissRequest = onDismiss,
        title = { Text("New Wishlist") },
        text = {
            Column {
                TextField(
                    value = name,
                    onValueChange = {
                        name = it
                        onClearError()
                    },
                    label = { Text("Name") },
                    singleLine = true
                )
                if (!errorMessage.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(WistDimensions.SpacingSm))
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = AlertRed
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(name) }, enabled = !isLoading) {
                Text(if (isLoading) "Creating…" else "Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
