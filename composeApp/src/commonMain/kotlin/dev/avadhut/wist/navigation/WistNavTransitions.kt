package dev.avadhut.wist.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene
import androidx.navigationevent.NavigationEvent

private const val WIST_NAV_DURATION_MS = 340

private val wistNavFadeTween = tween<Float>(WIST_NAV_DURATION_MS, easing = FastOutSlowInEasing)

internal val wistNavForwardTransition: AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform =
    {
        (
                slideInHorizontally(initialOffsetX = { it }) +
                        fadeIn(animationSpec = wistNavFadeTween)
                ) togetherWith (
                slideOutHorizontally(targetOffsetX = { -it }) +
                        fadeOut(animationSpec = wistNavFadeTween)
                )
    }

internal val wistNavPopTransition: AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform =
    {
        (
                slideInHorizontally(initialOffsetX = { -it }) +
                        fadeIn(animationSpec = wistNavFadeTween)
                ) togetherWith (
                slideOutHorizontally(targetOffsetX = { it }) +
                        fadeOut(animationSpec = wistNavFadeTween)
                )
    }

@Suppress("UnusedParameter")
internal val wistNavPredictivePopTransition:
        AnimatedContentTransitionScope<Scene<NavKey>>.(@NavigationEvent.SwipeEdge Int) -> ContentTransform =
    { _: Int ->
        (
                slideInHorizontally(initialOffsetX = { w -> -w }) +
                        fadeIn(animationSpec = wistNavFadeTween)
                ) togetherWith (
                slideOutHorizontally(targetOffsetX = { w -> w }) +
                        fadeOut(animationSpec = wistNavFadeTween)
                )
    }
