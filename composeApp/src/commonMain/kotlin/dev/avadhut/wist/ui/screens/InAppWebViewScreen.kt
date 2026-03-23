package dev.avadhut.wist.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import dev.avadhut.wist.ui.components.atoms.WistIconButton
import dev.avadhut.wist.ui.components.organisms.WistDetailTopAppBar
import dev.avadhut.wist.ui.webview.InAppWebView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InAppWebViewScreen(url: String, onBack: () -> Unit) {
    val domain = runCatching {
        // Extract host from URL without java.net.URI (KMP-compatible)
        url.removePrefix("https://").removePrefix("http://").substringBefore("/")
    }.getOrDefault(url)
    val uriHandler = LocalUriHandler.current

    Scaffold(
        topBar = {
            WistDetailTopAppBar(
                title = domain,
                onBackClick = onBack,
                actions = {
                    WistIconButton(
                        icon = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = "Open in browser",
                        onClick = { uriHandler.openUri(url) }
                    )
                }
            )
        }
    ) { padding ->
        InAppWebView(
            url = url,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        )
    }
}
