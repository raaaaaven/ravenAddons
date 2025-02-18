package at.raven.ravenAddons.utils

import at.raven.ravenAddons.ravenAddons
import kotlinx.coroutines.delay
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.StringSelection
import kotlin.time.Duration.Companion.milliseconds

object ClipboardUtils {
    private var lastClipboardAccessTime = SimpleTimeMark.farPast()

    private fun canAccessClipboard(): Boolean {
        val result = lastClipboardAccessTime.passedSince() > 10.milliseconds
        if (result) {
            lastClipboardAccessTime = SimpleTimeMark.now()
        }
        return result
    }

    private suspend fun getClipboard(attempt: Int = 20): Clipboard? =
        if (canAccessClipboard()) {
            Toolkit.getDefaultToolkit().systemClipboard
        } else if (attempt > 0) {
            delay(11)
            getClipboard(attempt - 1)
        } else {
            ChatUtils.warning("Failed to read the clipboard.")
            null
        }

    fun copyToClipboard(
        text: String,
        attempt: Int = 0,
    ) {
        ravenAddons.launchCoroutine {
            try {
                getClipboard()?.setContents(StringSelection(text), null)
            } catch (_: Exception) {
                if (attempt == 3) {
                    ChatUtils.warning("Failed to access the clipboard.")
                } else {
                    copyToClipboard(text, attempt + 1)
                }
            }
        }
    }
}
