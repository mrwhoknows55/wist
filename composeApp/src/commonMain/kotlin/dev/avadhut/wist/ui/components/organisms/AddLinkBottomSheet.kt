package dev.avadhut.wist.ui.components.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.avadhut.wist.ui.components.atoms.WistButton
import dev.avadhut.wist.ui.components.atoms.WistButtonStyle
import dev.avadhut.wist.ui.components.atoms.WistIconButton
import dev.avadhut.wist.ui.components.molecules.ClipboardSuggestionChip
import dev.avadhut.wist.ui.components.molecules.CreateNewListTile
import dev.avadhut.wist.ui.components.molecules.ListSelectionTile
import dev.avadhut.wist.ui.theme.BackgroundCard
import dev.avadhut.wist.ui.theme.BackgroundSurface
import dev.avadhut.wist.ui.theme.BorderDefault
import dev.avadhut.wist.ui.theme.TextDisabled
import dev.avadhut.wist.ui.theme.TextPrimary
import dev.avadhut.wist.ui.theme.TextSecondary
import dev.avadhut.wist.ui.theme.WistDimensions
import dev.avadhut.wist.ui.theme.WistTheme
import kotlin.collections.minus
import kotlin.collections.plus

/**
 * Data class for clipboard suggestions
 */
data class ClipboardItem(
    val url: String
)

/**
 * Add Link Bottom Sheet Content - Modal content for adding items
 *
 * Contains:
 * - Title with close button
 * - URL text input
 * - Clipboard suggestions
 * - List selection tiles
 * - Confirm button
 *
 * @param urlValue Current URL input value
 * @param onUrlChange Callback when URL input changes
 * @param clipboardItems List of detected clipboard URLs
 * @param onClipboardItemClick Callback when a clipboard item is clicked
 * @param availableLists List of available wishlists
 * @param selectedLists Set of selected list names
 * @param onListSelectionChange Callback when list selection changes
 * @param onCreateNewList Callback when "Create New List" is clicked
 * @param onConfirm Callback when Confirm button is clicked
 * @param onClose Callback when close button is clicked
 * @param modifier Modifier for customization
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddLinkBottomSheetContent(
    urlValue: String,
    onUrlChange: (String) -> Unit,
    clipboardItems: List<ClipboardItem>,
    onClipboardItemClick: (ClipboardItem) -> Unit,
    availableLists: List<String>,
    selectedLists: Set<String>,
    onListSelectionChange: (String, Boolean) -> Unit,
    onCreateNewList: () -> Unit,
    onConfirm: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(BackgroundSurface)
            .padding(WistDimensions.ScreenPaddingHorizontal)
    ) {
        // Header: Title + Close button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Add product",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary
            )
            WistIconButton(
                icon = Icons.Filled.Close,
                contentDescription = "Close",
                onClick = onClose
            )
        }

        Spacer(modifier = Modifier.height(
            WistDimensions.SpacingLg))

        // ADD Section
        Text(
            text = "ADD",
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(
            WistDimensions.SpacingSm))

        // URL Input with left border accent
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left accent border
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(WistDimensions.InputHeight)
                    .background(BorderDefault)
            )

            Spacer(modifier = Modifier.width(
                WistDimensions.SpacingSm))

            // Text Input
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(WistDimensions.InputHeight)
                    .clip(RoundedCornerShape(WistDimensions.InputRadius))
                    .background(BackgroundCard)
                    .padding(horizontal = WistDimensions.SpacingLg),
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    value = urlValue,
                    onValueChange = onUrlChange,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = TextPrimary
                    ),
                    singleLine = true,
                    cursorBrush = SolidColor(TextPrimary),
                    decorationBox = { innerTextField ->
                        if (urlValue.isEmpty()) {
                            Text(
                                text = "Enter link to product",
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextDisabled
                            )
                        }
                        innerTextField()
                    }
                )
            }
        }

        // Clipboard Section
        if (clipboardItems.isNotEmpty()) {
            Spacer(modifier = Modifier.height(
                WistDimensions.SpacingMd))

            Text(
                text = "Clipboard",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(
                WistDimensions.SpacingSm))

            Row(
                horizontalArrangement = Arrangement.spacedBy(WistDimensions.SpacingSm)
            ) {
                clipboardItems.take(2).forEach { item ->
                    ClipboardSuggestionChip(
                        url = item.url,
                        onClick = { onClipboardItemClick(item) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(
            WistDimensions.SpacingLg))

        // TO Section
        Text(
            text = "TO",
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(
            WistDimensions.SpacingSm))

        // List Selection Tiles
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(WistDimensions.SpacingSm),
            verticalArrangement = Arrangement.spacedBy(WistDimensions.SpacingSm)
        ) {
            availableLists.forEach { listName ->
                ListSelectionTile(
                    text = listName,
                    selected = listName in selectedLists,
                    onSelectionChange = { selected ->
                        onListSelectionChange(listName, selected)
                    }
                )
            }
            CreateNewListTile(onClick = onCreateNewList)
        }

        Spacer(modifier = Modifier.height(
            WistDimensions.SpacingXl))

        // Confirm Button
        WistButton(
            text = "Confirm",
            onClick = onConfirm,
            style = WistButtonStyle.PRIMARY,
            fillMaxWidth = true
        )

        Spacer(modifier = Modifier.height(
            WistDimensions.SpacingLg))
    }
}

/**
 * Add Link Modal Bottom Sheet
 *
 * Full modal bottom sheet wrapper for the add link flow.
 *
 * @param isVisible Whether the sheet is visible
 * @param onDismiss Callback when sheet is dismissed
 * @param sheetState Optional sheet state for controlling the sheet
 * @param content The sheet content
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLinkBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(),
    content: @Composable () -> Unit
) {
    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = BackgroundSurface,
            shape = RoundedCornerShape(
                topStart = WistDimensions.BottomSheetRadius,
                topEnd = WistDimensions.BottomSheetRadius
            ),
            dragHandle = {
                // Custom drag handle
                Box(
                    modifier = Modifier
                        .padding(vertical = WistDimensions.SpacingSm)
                        .size(
                            width = WistDimensions.BottomSheetHandleWidth,
                            height = WistDimensions.BottomSheetHandleHeight
                        )
                        .clip(RoundedCornerShape(2.dp))
                        .background(BorderDefault)
                )
            }
        ) {
            content()
        }
    }
}


// PREVIEWS


@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun AddLinkBottomSheetContentPreview() {
    WistTheme {
        var url by remember { mutableStateOf("") }
        var selectedLists by remember { mutableStateOf(setOf("Phones")) }

        AddLinkBottomSheetContent(
            urlValue = url,
            onUrlChange = { url = it },
            clipboardItems = listOf(
                ClipboardItem("https://www.amazon.com/phone-iphone-15-pro"),
                ClipboardItem("https://www.flipkart.com/oneplus-nord")
            ),
            onClipboardItemClick = { url = it.url },
            availableLists = listOf("Phones", "My shoe list", "Shopping list 01"),
            selectedLists = selectedLists,
            onListSelectionChange = { list, selected ->
                selectedLists = if (selected) selectedLists + list else selectedLists - list
            },
            onCreateNewList = {},
            onConfirm = {},
            onClose = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun AddLinkBottomSheetContentEmptyPreview() {
    WistTheme {
        AddLinkBottomSheetContent(
            urlValue = "",
            onUrlChange = {},
            clipboardItems = emptyList(),
            onClipboardItemClick = {},
            availableLists = listOf("My Wishlist"),
            selectedLists = setOf("My Wishlist"),
            onListSelectionChange = { _, _ -> },
            onCreateNewList = {},
            onConfirm = {},
            onClose = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun AddLinkBottomSheetContentWithUrlPreview() {
    WistTheme {
        AddLinkBottomSheetContent(
            urlValue = "https://www.amazon.com/product/iphone-15",
            onUrlChange = {},
            clipboardItems = listOf(
                ClipboardItem("https://www.flipkart.com/phone")
            ),
            onClipboardItemClick = {},
            availableLists = listOf("Phones", "Tech Gadgets"),
            selectedLists = setOf("Phones"),
            onListSelectionChange = { _, _ -> },
            onCreateNewList = {},
            onConfirm = {},
            onClose = {}
        )
    }
}
