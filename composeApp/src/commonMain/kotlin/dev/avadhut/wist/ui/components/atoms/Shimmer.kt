package dev.avadhut.wist.ui.components.atoms

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import dev.avadhut.wist.ui.theme.ShimmerBase
import dev.avadhut.wist.ui.theme.ShimmerHighlight

private const val SHIMMER_DURATION_MS = 1200
private const val SHIMMER_LABEL = "wist_shimmer"

@Composable
fun rememberShimmerBrush(): Brush {
    val transition = rememberInfiniteTransition(label = SHIMMER_LABEL)
    val translate by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(SHIMMER_DURATION_MS, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )
    return Brush.linearGradient(
        colors = listOf(ShimmerBase, ShimmerHighlight, ShimmerBase),
        start = Offset(translate - 400f, translate - 400f),
        end = Offset(translate, translate)
    )
}

fun Modifier.shimmerBackground(brush: Brush, shape: Shape = RectangleShape): Modifier =
    clip(shape).background(brush)

@Composable
fun ShimmerBox(
    modifier: Modifier,
    shape: Shape = RectangleShape,
    brush: Brush = rememberShimmerBrush()
) {
    Box(modifier = modifier.clip(shape).background(brush))
}
