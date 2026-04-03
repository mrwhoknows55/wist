package dev.avadhut.wist

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.avadhut.wist.storage.JavaPreferencesTokenStorage
import dev.avadhut.wist.ui.theme.WistTheme
import org.jetbrains.compose.resources.painterResource
import wist.composeapp.generated.resources.Res
import wist.composeapp.generated.resources.wist_icon

private val tokenStorage = JavaPreferencesTokenStorage()

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Wist",
        icon = painterResource(Res.drawable.wist_icon),
    ) {
        WistTheme {
            App(tokenStorage = tokenStorage)
        }
    }
}
