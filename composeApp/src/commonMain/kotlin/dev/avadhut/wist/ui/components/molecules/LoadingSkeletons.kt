package dev.avadhut.wist.ui.components.molecules

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.avadhut.wist.ui.components.atoms.ShimmerBox
import dev.avadhut.wist.ui.components.atoms.rememberShimmerBrush
import dev.avadhut.wist.ui.theme.BorderDefault
import dev.avadhut.wist.ui.theme.WistDimensions
import dev.avadhut.wist.ui.theme.WistTheme

private const val HOME_SKELETON_ROWS = 5
private const val DETAIL_SKELETON_ROWS = 6

@Composable
fun WishlistListItemSkeleton(
    modifier: Modifier = Modifier,
    brush: Brush = rememberShimmerBrush()
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp)
                .border(
                    width = WistDimensions.DividerThickness,
                    color = BorderDefault,
                    shape = RectangleShape
                )
                .padding(WistDimensions.SpacingLg),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                ShimmerBox(
                    modifier = Modifier.fillMaxWidth(0.85f).height(28.dp),
                    shape = RoundedCornerShape(4.dp),
                    brush = brush
                )
                Spacer(modifier = Modifier.height(WistDimensions.SpacingSm))
                ShimmerBox(
                    modifier = Modifier.width(120.dp).height(14.dp),
                    shape = RoundedCornerShape(4.dp),
                    brush = brush
                )
                Spacer(modifier = Modifier.height(WistDimensions.SpacingLg))
                Row(horizontalArrangement = Arrangement.spacedBy(WistDimensions.SpacingXxs)) {
                    repeat(4) {
                        ShimmerBox(
                            modifier = Modifier.size(WistDimensions.SourceIconSize),
                            shape = RectangleShape,
                            brush = brush
                        )
                    }
                }
                Spacer(modifier = Modifier.height(WistDimensions.SpacingSm))
                ShimmerBox(
                    modifier = Modifier.width(100.dp).height(18.dp),
                    shape = RoundedCornerShape(4.dp),
                    brush = brush
                )
            }
            Spacer(modifier = Modifier.width(WistDimensions.SpacingMd))
            ShimmerBox(
                modifier = Modifier.size(120.dp),
                shape = RectangleShape,
                brush = brush
            )
        }
    }
}

@Composable
fun ProductListItemSkeleton(
    modifier: Modifier = Modifier,
    brush: Brush = rememberShimmerBrush()
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = WistDimensions.DividerThickness,
                    color = BorderDefault
                )
                .padding(
                    horizontal = WistDimensions.ScreenPaddingHorizontal,
                    vertical = WistDimensions.SpacingLg
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ShimmerBox(
                modifier = Modifier.size(WistDimensions.ThumbnailLarge),
                shape = RoundedCornerShape(WistDimensions.SpacingSm),
                brush = brush
            )
            Spacer(modifier = Modifier.width(WistDimensions.SpacingLg))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(WistDimensions.SpacingXs)
            ) {
                ShimmerBox(
                    modifier = Modifier.fillMaxWidth().height(22.dp),
                    shape = RoundedCornerShape(4.dp),
                    brush = brush
                )
                ShimmerBox(
                    modifier = Modifier.fillMaxWidth(0.92f).height(22.dp),
                    shape = RoundedCornerShape(4.dp),
                    brush = brush
                )
                ShimmerBox(
                    modifier = Modifier.width(96.dp).height(20.dp),
                    shape = RoundedCornerShape(4.dp),
                    brush = brush
                )
                ShimmerBox(
                    modifier = Modifier.width(140.dp).height(16.dp),
                    shape = RoundedCornerShape(4.dp),
                    brush = brush
                )
            }
        }
    }
}

@Composable
fun HomeListLoadingContent(
    modifier: Modifier = Modifier,
    skeletonRowCount: Int = HOME_SKELETON_ROWS
) {
    val brush = rememberShimmerBrush()
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = WistDimensions.ScreenPaddingHorizontal)
    ) {
        item {
            SearchInputSkeleton(brush = brush)
            Spacer(modifier = Modifier.height(WistDimensions.SpacingLg))
        }
        item {
            ShimmerBox(
                modifier = Modifier
                    .padding(2.dp)
                    .fillMaxWidth()
                    .height(140.dp),
                shape = RectangleShape,
                brush = brush
            )
        }
        items(skeletonRowCount) {
            WishlistListItemSkeleton(brush = brush)
        }
        item {
            Spacer(modifier = Modifier.height(WistDimensions.SpacingXxl))
        }
    }
}

@Composable
fun DetailListLoadingContent(modifier: Modifier = Modifier) {
    val brush = rememberShimmerBrush()
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = WistDimensions.ScreenPaddingHorizontal)
    ) {
        item {
            Spacer(modifier = Modifier.height(WistDimensions.SpacingLg))
        }
        items(DETAIL_SKELETON_ROWS) {
            ProductListItemSkeleton(brush = brush)
        }
    }
}

@Composable
private fun SearchInputSkeleton(brush: Brush = rememberShimmerBrush()) {
    ShimmerBox(
        modifier = Modifier
            .fillMaxWidth()
            .height(WistDimensions.InputHeight),
        shape = RectangleShape,
        brush = brush
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun WishlistListItemSkeletonPreview() {
    WistTheme {
        WishlistListItemSkeleton(
            modifier = Modifier.padding(WistDimensions.ScreenPaddingHorizontal)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun ProductListItemSkeletonPreview() {
    WistTheme {
        ProductListItemSkeleton()
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun HomeListLoadingContentPreview() {
    WistTheme {
        HomeListLoadingContent()
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun DetailListLoadingContentPreview() {
    WistTheme {
        DetailListLoadingContent()
    }
}
