package dev.avadhut.wist.ui.webview

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

expect @Composable
fun InAppWebView(url: String, modifier: Modifier = Modifier)
