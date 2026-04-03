package dev.avadhut.wist

import androidx.compose.ui.window.ComposeUIViewController
import dev.avadhut.wist.storage.NSUserDefaultsTokenStorage
import dev.avadhut.wist.ui.theme.WistTheme

private val tokenStorage = NSUserDefaultsTokenStorage()

fun MainViewController() = ComposeUIViewController {
    WistTheme {
        App(tokenStorage = tokenStorage)
    }
}