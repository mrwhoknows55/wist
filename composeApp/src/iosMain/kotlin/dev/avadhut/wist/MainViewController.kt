package dev.avadhut.wist

import androidx.compose.ui.window.ComposeUIViewController
import dev.avadhut.wist.ui.screens.ComponentDemoScreen
import dev.avadhut.wist.ui.theme.WistTheme

fun MainViewController() = ComposeUIViewController {
    WistTheme {
        ComponentDemoScreen()
    }
}