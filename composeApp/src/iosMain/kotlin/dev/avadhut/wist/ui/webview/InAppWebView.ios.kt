@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package dev.avadhut.wist.ui.webview

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitInteropProperties
import androidx.compose.ui.viewinterop.UIKitView

@Composable
actual fun InAppWebView(url: String, modifier: Modifier) {
    UIKitView(
        factory = {
            val wkWebView = platform.WebKit.WKWebView()
            platform.Foundation.NSURL.URLWithString(url)?.let {
                wkWebView.loadRequest(platform.Foundation.NSURLRequest.requestWithURL(it))
            }
            wkWebView
        }, modifier = modifier, update = {}, onRelease = {}, properties = UIKitInteropProperties(
            isInteractive = true, isNativeAccessibilityEnabled = true
        )
    )
}
