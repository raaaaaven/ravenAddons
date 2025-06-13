package at.raven.ravenAddons.utils

import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse

object KeyboardUtils {
    fun Int.isKeyHeld(): Boolean =
        when {
            this == 0 -> false
            this < 0 -> Mouse.isButtonDown(this + 100)
            this >= Keyboard.KEYBOARD_SIZE -> {
                val pressedKey =
                    if (Keyboard.getEventKey() == 0) Keyboard.getEventCharacter().code + 256 else Keyboard.getEventKey()
                Keyboard.getEventKeyState() && this == pressedKey
            }

            else -> Keyboard.isKeyDown(this)
        }
}
