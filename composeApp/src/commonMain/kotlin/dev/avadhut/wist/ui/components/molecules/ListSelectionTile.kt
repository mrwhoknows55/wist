package dev.avadhut.wist.ui.components.molecules

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.avadhut.wist.ui.theme.BackgroundCard
import dev.avadhut.wist.ui.theme.BorderDefault
import dev.avadhut.wist.ui.theme.TextPrimary
import dev.avadhut.wist.ui.theme.WistDimensions
import dev.avadhut.wist.ui.theme.WistTheme

/**
 * List Selection Tile - Pill-shaped selection for wishlists
 *
 * Used in the Add Link flow to select destination list.
 * Shows selected/unselected visual states.
 *
 * @param text List name
 * @param selected Whether this tile is selected
 * @param onSelectionChange Callback when selection changes
 * @param modifier Modifier for customization
 */
@Composable
fun ListSelectionTile(
    text: String,
    selected: Boolean,
    onSelectionChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = { onSelectionChange(!selected) },
        label = {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge
            )
        },
        modifier = modifier.height(WistDimensions.ChipHeight),
        shape = RoundedCornerShape(WistDimensions.ChipRadius),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = BorderDefault,
            selectedBorderColor = TextPrimary,
            borderWidth = 1.dp,
            selectedBorderWidth = 1.dp
        ),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = Color.Transparent,
            labelColor = TextPrimary,
            selectedContainerColor = Color.Transparent,
            selectedLabelColor = TextPrimary
        )
    )
}

/**
 * Create New List Tile - Special tile for creating a new list
 *
 * Shows a "+" icon with "Create New List" text.
 *
 * @param onClick Callback when tile is clicked
 * @param modifier Modifier for customization
 */
@Composable
fun CreateNewListTile(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = false,
        onClick = onClick,
        label = {
            Row {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    tint = TextPrimary
                )
                Spacer(modifier = Modifier.width(
                    WistDimensions.SpacingXs))
                Text(
                    text = "Create New List",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        },
        modifier = modifier.height(WistDimensions.ChipHeight),
        shape = RoundedCornerShape(WistDimensions.ChipRadius),
        border = BorderStroke(1.dp, BorderDefault),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = Color.Transparent,
            labelColor = TextPrimary
        )
    )
}


// PREVIEWS


@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun ListSelectionTileUnselectedPreview() {
    WistTheme {
        ListSelectionTile(
            text = "Phones",
            selected = false,
            onSelectionChange = {},
            modifier = Modifier.padding(
                WistDimensions.SpacingSm
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun ListSelectionTileSelectedPreview() {
    WistTheme {
        ListSelectionTile(
            text = "Phones",
            selected = true,
            onSelectionChange = {},
            modifier = Modifier.padding(
                WistDimensions.SpacingSm
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun CreateNewListTilePreview() {
    WistTheme {
        CreateNewListTile(
            onClick = {},
            modifier = Modifier.padding(
                WistDimensions.SpacingSm
            )
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun ListSelectionRowPreview() {
    WistTheme {
        var selectedList by remember { mutableStateOf("Phones") }
        FlowRow(
            modifier = Modifier.padding(
                WistDimensions.SpacingSm
            ),
            horizontalArrangement = Arrangement.spacedBy(WistDimensions.SpacingSm)
        ) {
            listOf("Phones", "My shoe list", "Shopping list 01").forEach { listName ->
                ListSelectionTile(
                    text = listName,
                    selected = listName == selectedList,
                    onSelectionChange = { if (it) selectedList = listName }
                )
            }
            CreateNewListTile(onClick = {})
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun ListSelectionMultiplePreview() {
    WistTheme {
        var selectedLists by remember { mutableStateOf(setOf("Phones")) }
        FlowRow(
            modifier = Modifier.padding(
                WistDimensions.SpacingSm
            ),
            horizontalArrangement = Arrangement.spacedBy(WistDimensions.SpacingSm),
            verticalArrangement = Arrangement.spacedBy(WistDimensions.SpacingSm)
        ) {
            listOf("Phones", "My shoe list", "Tech Gadgets", "Summer Trip").forEach { listName ->
                ListSelectionTile(
                    text = listName,
                    selected = listName in selectedLists,
                    onSelectionChange = { selected ->
                        selectedLists = if (selected) {
                            selectedLists + listName
                        } else {
                            selectedLists - listName
                        }
                    }
                )
            }
            CreateNewListTile(onClick = {})
        }
    }
}
