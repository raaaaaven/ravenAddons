package at.raven.ravenaddons.utils

object SoundUtils {
    fun playSound(
        sound: String,
        volume: Float,
        pitch: Float,
    ) {
        val player = PlayerUtils.getPlayer() ?: return

        player.playSound(sound, volume, pitch)
    }

    fun pling() =  playSound("note.pling", 1f, 1f)
}