package dev.avadhut.wist

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import dev.avadhut.wist.ui.screens.ComponentDemoScreen
import dev.avadhut.wist.ui.theme.WistTheme

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport {
        WistTheme {
            ComponentDemoScreen()
        }
    }
}