package dev.avadhut.wist.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.avadhut.wist.ui.components.atoms.AppLogoText
import dev.avadhut.wist.ui.components.atoms.KnownSource
import dev.avadhut.wist.ui.components.atoms.PriceRangeTag
import dev.avadhut.wist.ui.components.atoms.PriceTag
import dev.avadhut.wist.ui.components.atoms.SourceIcon
import dev.avadhut.wist.ui.components.atoms.WistButton
import dev.avadhut.wist.ui.components.atoms.WistButtonStyle
import dev.avadhut.wist.ui.components.atoms.WistIconButton
import dev.avadhut.wist.ui.components.molecules.ClipboardSuggestionChip
import dev.avadhut.wist.ui.components.molecules.ListSelectionTile
import dev.avadhut.wist.ui.components.molecules.ProductThumbnailGrid
import dev.avadhut.wist.ui.components.molecules.SearchInput
import dev.avadhut.wist.ui.components.organisms.BottomActionArea
import dev.avadhut.wist.ui.components.organisms.ProductListItem
import dev.avadhut.wist.ui.components.organisms.ProductListItemData
import dev.avadhut.wist.ui.components.organisms.WishlistCard
import dev.avadhut.wist.ui.components.organisms.WishlistDisplayData
import dev.avadhut.wist.ui.components.organisms.WistHomeTopAppBar
import dev.avadhut.wist.ui.theme.BackgroundPrimary
import dev.avadhut.wist.ui.theme.DividerColor
import dev.avadhut.wist.ui.theme.TextSecondary
import dev.avadhut.wist.ui.theme.WistDimensions
import dev.avadhut.wist.ui.theme.WistTheme

/**
 * Component Demo Screen - Showcases all UI components
 *
 * This screen displays all available Wist UI components
 * organized by category for visual reference and testing.
 */
@Composable
fun ComponentDemoScreen() {
    Scaffold(
        topBar = {
            WistHomeTopAppBar(
                actions = {
                    WistIconButton(
                        icon = Icons.Filled.MoreVert, contentDescription = "Menu", onClick = {})
                })
        }, bottomBar = {
            BottomActionArea(
                primaryText = "Add product",
                secondaryText = "Create New list",
                onPrimaryClick = {},
                onSecondaryClick = {})
        }, containerColor = BackgroundPrimary
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = WistDimensions.ScreenPaddingHorizontal)
        ) {
            // =================================================================
            // ATOMS SECTION
            // =================================================================
            SectionHeader(title = "Atoms")

            // App Logo Text
            ComponentLabel(label = "AppLogoText")
            AppLogoText()

            Spacer(
                modifier = Modifier.height(
                    WistDimensions.SpacingLg
                )
            )

            // Buttons
            ComponentLabel(label = "WistButton")
            Row(
                horizontalArrangement = Arrangement.spacedBy(WistDimensions.SpacingSm)
            ) {
                WistButton(
                    text = "Primary",
                    onClick = {},
                    style = WistButtonStyle.PRIMARY
                )
                WistButton(
                    text = "Secondary",
                    onClick = {},
                    style = WistButtonStyle.SECONDARY
                )
                WistButton(
                    text = "Tertiary",
                    onClick = {},
                    style = WistButtonStyle.TERTIARY
                )
            }

            Spacer(
                modifier = Modifier.height(
                    WistDimensions.SpacingLg
                )
            )

            // Source Icons
            ComponentLabel(label = "SourceIcon")
            Row(
                horizontalArrangement = Arrangement.spacedBy(WistDimensions.SpacingSm)
            ) {
                KnownSource.entries.forEach { source ->
                    SourceIcon(
                        source = source, showLabel = true
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(
                    WistDimensions.SpacingLg
                )
            )

            // Price Tags
            ComponentLabel(label = "PriceTag")
            Row(
                horizontalArrangement = Arrangement.spacedBy(WistDimensions.SpacingSm)
            ) {
                PriceTag(
                    price = 12999.0, currencyCode = "USD"
                )
                PriceRangeTag(
                    minPrice = 122.0, maxPrice = 455.0
                )
            }

            SectionDivider()

            // =================================================================
            // MOLECULES SECTION
            // =================================================================
            SectionHeader(title = "Molecules")

            // Search Input
            ComponentLabel(label = "SearchInput")
            var searchText by remember { mutableStateOf("") }
            SearchInput(
                value = searchText,
                onValueChange = { searchText = it },
                onFilterClick = {})

            Spacer(
                modifier = Modifier.height(
                    WistDimensions.SpacingLg
                )
            )

            // Clipboard Chips
            ComponentLabel(label = "ClipboardSuggestionChip")
            Row(
                horizontalArrangement = Arrangement.spacedBy(WistDimensions.SpacingSm)
            ) {
                ClipboardSuggestionChip(
                    url = "https://amazon.com/iphone-15", onClick = {})
                ClipboardSuggestionChip(
                    url = "https://flipkart.com/oneplus", onClick = {})
            }

            Spacer(
                modifier = Modifier.height(
                    WistDimensions.SpacingLg
                )
            )

            // List Selection Tiles
            ComponentLabel(label = "ListSelectionTile")
            var selectedList by remember { mutableStateOf("Phones") }
            Row(
                horizontalArrangement = Arrangement.spacedBy(WistDimensions.SpacingSm)
            ) {
                listOf("Phones", "Shoes", "Tech").forEach { list ->
                    ListSelectionTile(
                        text = list,
                        selected = list == selectedList,
                        onSelectionChange = { if (it) selectedList = list })
                }
            }

            Spacer(
                modifier = Modifier.height(
                    WistDimensions.SpacingLg
                )
            )

            // Product Thumbnail Grid
            ComponentLabel(label = "ProductThumbnailGrid")
            ProductThumbnailGrid(
                imageUrls = listOf("img1", "img2", "img3", "img4"),
                modifier = Modifier.width(
                    WistDimensions.ThumbnailLarge * 2
                )
            )

            SectionDivider()

            // =================================================================
            // ORGANISMS SECTION
            // =================================================================
            SectionHeader(title = "Organisms")

            // Wishlist Card
            ComponentLabel(label = "WishlistCard")
            WishlistCard(
                data = WishlistDisplayData(
                    id = "1",
                    name = "Phones",
                    dateLabel = "from Mar 25",
                    productImages = listOf("img1", "img2", "img3", "img4"),
                    sources = listOf(
                        KnownSource.AMAZON,
                        KnownSource.FLIPKART,
                        KnownSource.MYNTRA,
                        KnownSource.GENERIC
                    ),
                    priceMin = 122.0,
                    priceMax = 455.0
                ), onClick = {})

            Spacer(
                modifier = Modifier.height(
                    WistDimensions.SpacingLg
                )
            )

            // Product List Items
            ComponentLabel(label = "ProductListItem")
            listOf(
                ProductListItemData(
                    id = "1",
                    title = "OnePlus 13R",
                    price = 12999.0,
                    source = KnownSource.FLIPKART
                ), ProductListItemData(
                    id = "2",
                    title = "iPhone 15 Pro Max",
                    price = 1199.0,
                    source = KnownSource.AMAZON
                )
            ).forEach { product ->
                ProductListItem(
                    data = product, onClick = {}, modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(
                modifier = Modifier.height(
                    WistDimensions.SpacingXxl
                )
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.headlineLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = WistDimensions.SpacingLg)
    )
}

@Composable
private fun ComponentLabel(label: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        color = TextSecondary,
        modifier = Modifier.padding(bottom = WistDimensions.SpacingSm)
    )
}

@Composable
private fun SectionDivider() {
    Spacer(
        modifier = Modifier.height(
            WistDimensions.SpacingXl
        )
    )
    HorizontalDivider(color = DividerColor)
    Spacer(
        modifier = Modifier.height(
            WistDimensions.SpacingSm
        )
    )
}


// PREVIEW


@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun ComponentDemoScreenPreview() {
    WistTheme {
        ComponentDemoScreen()
    }
}
