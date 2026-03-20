package dev.avadhut.wist.ui.clipboard

import androidx.compose.ui.platform.Clipboard
import org.jetbrains.skiko.ClipboardManager

actual suspend fun Clipboard.readPlainTextOrNull(): String? =
    ClipboardManager().getText()
