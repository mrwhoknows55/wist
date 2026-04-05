package dev.avadhut.wist

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import dev.avadhut.wist.storage.LocalStorageTokenStorage
import dev.avadhut.wist.ui.theme.WistTheme

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val tokenStorage = LocalStorageTokenStorage()
    ComposeViewport {
        WistTheme {
            App(tokenStorage = tokenStorage)
        }
    }
}