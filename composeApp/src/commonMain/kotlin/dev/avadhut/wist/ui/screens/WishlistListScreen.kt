package dev.avadhut.wist.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.unit.dp
import dev.avadhut.wist.client.WistApiClient
import dev.avadhut.wist.core.dto.WishlistDto
import dev.avadhut.wist.ui.components.atoms.KnownSource
import dev.avadhut.wist.ui.components.atoms.SourceIcon
import dev.avadhut.wist.ui.components.atoms.WistButton
import dev.avadhut.wist.ui.components.atoms.WistButtonStyle
import dev.avadhut.wist.ui.components.molecules.SearchInput
import dev.avadhut.wist.ui.components.organisms.AddLinkBottomSheet
import dev.avadhut.wist.ui.components.organisms.AddLinkBottomSheetContent
import dev.avadhut.wist.ui.components.organisms.BottomActionArea
import dev.avadhut.wist.ui.components.organisms.ClipboardItem
import dev.avadhut.wist.ui.components.organisms.WishlistDisplayData
import dev.avadhut.wist.ui.components.organisms.WishlistListItem
import dev.avadhut.wist.ui.components.organisms.WistHomeTopAppBar
import dev.avadhut.wist.ui.theme.TextPrimary
import dev.avadhut.wist.ui.theme.TextSecondary
import dev.avadhut.wist.ui.theme.WistDimensions
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WishlistListScreen(
    apiClient: WistApiClient, onWishlistClick: (Int) -> Unit
) {
    var wishlists by remember { mutableStateOf<List<WishlistDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var showAddSheet by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    var clipboardContent by remember { mutableStateOf<String?>(null) }
    var urlToAdd by remember { mutableStateOf("") }
    var selectedListNames by remember { mutableStateOf(setOf<String>()) }
    val sheetState = rememberModalBottomSheetState()

    fun loadWishlists() {
        scope.launch {
            isLoading = true
            apiClient.wishlists.getAllWishlists().onSuccess {
                wishlists = it
                error = null
            }.onFailure {
                error = it.message ?: "Failed to load wishlists"
            }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadWishlists()
    }

    // Check clipboard when sheet opens
    LaunchedEffect(showAddSheet) {
        if (showAddSheet) {
            val clip = clipboardManager.getText()?.text
            if (clip != null && (clip.startsWith("http") || clip.startsWith("www"))) {
                clipboardContent = clip
                if (urlToAdd.isEmpty()) urlToAdd = clip
            }
        }
    }

    Scaffold(topBar = {
        // Using Box to overlay title or custom AppBar
        WistHomeTopAppBar() // Defaults to "Wist" logo, assuming it's okay or will be updated globally
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
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (error != null) {
                Text("Error: $error", modifier = Modifier.align(Alignment.Center))
            } else {
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

                    // Second Opinion Card
                    item {
                        SecondOpinionCard()
                    }

                    if (wishlists.isEmpty()) {
                        item {
                            FirstWishlistCard(onCreateClick = { showCreateDialog = true })
                        }
                    } else {
                        items(wishlists.filter {
                            it.name.contains(
                                searchText, ignoreCase = true
                            )
                        }) { wishlist ->
                            // Format date as "from Jan 22" style
                            val dateLabel =
                                wishlist.createdAt.toString().substringBefore("T").let { isoDate ->
                                    try {
                                        val parts = isoDate.split("-")
                                        val month = when (parts.getOrNull(1)) {
                                            "01" -> "Jan"
                                            "02" -> "Feb"
                                            "03" -> "Mar"
                                            "04" -> "Apr"
                                            "05" -> "May"
                                            "06" -> "Jun"
                                            "07" -> "Jul"
                                            "08" -> "Aug"
                                            "09" -> "Sep"
                                            "10" -> "Oct"
                                            "11" -> "Nov"
                                            "12" -> "Dec"
                                            else -> "Jan"
                                        }
                                        val day = parts.getOrNull(2)?.toIntOrNull() ?: 1
                                        "from $month $day"
                                    } catch (_: Exception) {
                                        isoDate
                                    }
                                }
                            WishlistListItem(
                                data = WishlistDisplayData(
                                    id = wishlist.id.toString(),
                                    name = wishlist.name,
                                    dateLabel = dateLabel,
                                    productImages = emptyList(),
                                    sources = emptyList(),
                                    priceMin = 0.0,
                                    priceMax = 0.0
                                ), onClick = { onWishlistClick(wishlist.id) })
                        }
                    }

                    // Spacer for bottom bar
                    item {
                        Spacer(modifier = Modifier.height(WistDimensions.SpacingXxl))
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateWishlistDialog(onDismiss = { showCreateDialog = false }, onConfirm = { name ->
            scope.launch {
                apiClient.wishlists.createWishlist(name).onSuccess {
                    loadWishlists()
                    showCreateDialog = false
                }
            }
        })
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
                    val targetLists = wishlists.filter { it.name in selectedListNames }
                    targetLists.forEach { list ->
                        apiClient.wishlistItems.addItemToWishlist(list.id, urlToAdd)
                    }
                    showAddSheet = false
                    urlToAdd = ""
                    // update list if needed or just notify
                }
            },
            onClose = { showAddSheet = false })
    }
}


@Composable
fun SecondOpinionCard() {
    Box(
        modifier = Modifier.padding(2.dp).fillMaxWidth().background(
            brush = Brush.horizontalGradient(
                colors = listOf(Color(0xFF1E1E1E), Color(0xFF121212))
            )
        ).height(140.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
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

                // Small Get Started Button
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(4.dp)).background(Color.Transparent)
                        .border(1.dp, Color.White, RoundedCornerShape(4.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        "Get Started",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium
                    )
                }

            }
            // Placeholder for gradient/image
            Box(
                modifier = Modifier.weight(0.4f).fillMaxSize().background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF333333), Color.Transparent)
                    )
                )
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
fun CreateWishlistDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("New Wishlist") }, text = {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            singleLine = true
        )
    }, confirmButton = {
        Button(onClick = { onConfirm(name) }) {
            Text("Create")
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text("Cancel")
        }
    })
}
