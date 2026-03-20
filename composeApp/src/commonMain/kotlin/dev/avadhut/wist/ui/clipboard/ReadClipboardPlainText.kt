package dev.avadhut.wist.ui.clipboard

import androidx.compose.ui.platform.Clipboard

expect suspend fun Clipboard.readPlainTextOrNull(): String?
