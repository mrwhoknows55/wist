package dev.avadhut.wist.ui.components.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import dev.avadhut.wist.ui.theme.BackgroundSurface
import dev.avadhut.wist.ui.theme.TextDisabled
import dev.avadhut.wist.ui.theme.TextPrimary
import dev.avadhut.wist.ui.theme.WistDimensions
import dev.avadhut.wist.ui.theme.WistTheme

/**
 * Search Input - Dark grey search bar with filter option
 *
 * Features:
 * - Search icon (leading)
 * - Text input with placeholder
 * - Optional filter icon button (trailing)
 *
 * @param value Current search text
 * @param onValueChange Callback when text changes
 * @param modifier Modifier for customization
 * @param placeholder Placeholder text
 * @param onFilterClick Optional callback for filter button click
 * @param enabled Whether the input is enabled
 */
@Composable
fun SearchInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search",
    onFilterClick: (() -> Unit)? = null,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(WistDimensions.InputHeight)
            .clip(RoundedCornerShape(WistDimensions.InputRadius))
            .background(BackgroundSurface)
            .padding(horizontal = WistDimensions.SpacingLg),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Search Icon
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = null,
            tint = TextDisabled,
            modifier = Modifier.padding(end = WistDimensions.SpacingSm)
        )

        // Text Input
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            enabled = enabled,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = TextPrimary
            ),
            singleLine = true,
            cursorBrush = SolidColor(TextPrimary),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextDisabled
                    )
                }
                innerTextField()
            }
        )

        // Optional Filter Button
        if (onFilterClick != null) {
            IconButton(onClick = onFilterClick) {
                Icon(
                    imageVector = Icons.Filled.Tune,
                    contentDescription = "Filter",
                    tint = TextDisabled
                )
            }
        }
    }
}


// PREVIEWS


@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun SearchInputEmptyPreview() {
    WistTheme {
        SearchInput(
            value = "",
            onValueChange = {},
            modifier = Modifier.padding(
                WistDimensions.ScreenPaddingHorizontal
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun SearchInputWithTextPreview() {
    WistTheme {
        SearchInput(
            value = "iPhone 15",
            onValueChange = {},
            modifier = Modifier.padding(
                WistDimensions.ScreenPaddingHorizontal
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun SearchInputWithFilterPreview() {
    WistTheme {
        SearchInput(
            value = "",
            onValueChange = {},
            onFilterClick = {},
            modifier = Modifier.padding(
                WistDimensions.ScreenPaddingHorizontal
            )
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun SearchInputInteractivePreview() {
    WistTheme {
        var text by remember { mutableStateOf("") }
        SearchInput(
            value = text,
            onValueChange = { },
            onFilterClick = {},
            modifier = Modifier.padding(
                WistDimensions.ScreenPaddingHorizontal
            )
        )
    }
}
