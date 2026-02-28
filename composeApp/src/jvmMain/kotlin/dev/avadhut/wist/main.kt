package dev.avadhut.wist

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.avadhut.wist.ui.theme.WistTheme

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Wist",
        icon = painterResource("wist_icon.png"),
    ) {
        WistTheme {
            App()
        }
    }
}
