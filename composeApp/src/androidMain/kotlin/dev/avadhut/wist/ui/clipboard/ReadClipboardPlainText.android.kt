package dev.avadhut.wist.ui.clipboard

import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard

actual suspend fun Clipboard.readPlainTextOrNull(): String? {
    val entry = getClipEntry() ?: return null
    val data = entry.clipDataOrNull() ?: return null
    for (i in 0 until data.itemCount) {
        val text = data.getItemAt(i).text
        if (!text.isNullOrBlank()) return text.toString()
    }
    return null
}

private fun ClipEntry.clipDataOrNull(): android.content.ClipData? =
    runCatching {
        val m = ClipEntry::class.java.getMethod("getClipData")
        m.invoke(this) as? android.content.ClipData
    }.getOrNull()
