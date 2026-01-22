package dev.avadhut.wist.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.avadhut.wist.client.WistApiClient
import dev.avadhut.wist.core.dto.WishlistDto
import dev.avadhut.wist.ui.components.organisms.BottomActionArea
import dev.avadhut.wist.ui.components.organisms.WishlistCard
import dev.avadhut.wist.ui.components.organisms.WishlistDisplayData
import dev.avadhut.wist.ui.theme.WistDimensions
import kotlinx.coroutines.launch

@Composable
fun WishlistListScreen(
    apiClient: WistApiClient, onWishlistClick: (Int) -> Unit, onDemoClick: () -> Unit
) {
    var wishlists by remember { mutableStateOf<List<WishlistDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

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

    Scaffold(
        bottomBar = {
            BottomActionArea(
                primaryText = "Create New list",
                secondaryText = "Components Demo",
                onPrimaryClick = { showCreateDialog = true },
                onSecondaryClick = onDemoClick
            )
        }) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (error != null) {
                Text("Error: $error", modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(WistDimensions.ScreenPaddingHorizontal),
                    verticalArrangement = Arrangement.spacedBy(WistDimensions.SpacingLg),
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Text(
                            text = "My Wishlists",
                            style = MaterialTheme.typography.headlineLarge,
                            modifier = Modifier.padding(vertical = WistDimensions.SpacingLg)
                        )
                    }
                    items(wishlists) { wishlist ->
                        WishlistCard(
                            data = WishlistDisplayData(
                                id = wishlist.id.toString(),
                                name = wishlist.name,
                                dateLabel = wishlist.createdAt.toString().substringBefore("T"),
                                productImages = emptyList(), // No images in DTO yet
                                sources = emptyList(),
                                priceMin = 0.0,
                                priceMax = 0.0
                            ), onClick = { onWishlistClick(wishlist.id) })
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
                }.onFailure {
                    // handle error or show toast
                }
            }
        })
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
