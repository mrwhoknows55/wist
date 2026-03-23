package dev.avadhut.wist.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene
import androidx.navigationevent.NavigationEvent

internal val wistNavForwardTransition: AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform =
    {
        slideInHorizontally(initialOffsetX = { it }) togetherWith
                slideOutHorizontally(targetOffsetX = { -it })
    }

internal val wistNavPopTransition: AnimatedContentTransitionScope<Scene<NavKey>>.() -> ContentTransform =
    {
        slideInHorizontally(initialOffsetX = { -it }) togetherWith
                slideOutHorizontally(targetOffsetX = { it })
    }

@Suppress("UnusedParameter")
internal val wistNavPredictivePopTransition:
        AnimatedContentTransitionScope<Scene<NavKey>>.(@NavigationEvent.SwipeEdge Int) -> ContentTransform =
    { _: Int ->
        slideInHorizontally(initialOffsetX = { w -> -w }) togetherWith
                slideOutHorizontally(targetOffsetX = { w -> w })
    }
