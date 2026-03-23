package dev.avadhut.wist.ui.webview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import dev.avadhut.wist.ui.theme.BackgroundPrimary
import dev.avadhut.wist.ui.theme.TextSecondary

actual @Composable
fun InAppWebView(url: String, modifier: Modifier) {
    val uriHandler = LocalUriHandler.current
    LaunchedEffect(url) { uriHandler.openUri(url) }
    Box(modifier.background(BackgroundPrimary), contentAlignment = Alignment.Center) {
        Text(
            "Opening in browser…",
            color = TextSecondary,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
