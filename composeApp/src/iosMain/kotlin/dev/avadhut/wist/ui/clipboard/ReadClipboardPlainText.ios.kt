package dev.avadhut.wist.ui.clipboard

import androidx.compose.ui.platform.Clipboard
import platform.UIKit.UIPasteboard

actual suspend fun Clipboard.readPlainTextOrNull(): String? =
    UIPasteboard.generalPasteboard.string
