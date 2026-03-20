package dev.avadhut.wist.ui.clipboard

import androidx.compose.ui.platform.Clipboard

actual suspend fun Clipboard.readPlainTextOrNull(): String? = null
