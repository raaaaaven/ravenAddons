package at.raven.ravenAddons.mixin.hooks

import at.raven.ravenAddons.ravenAddons

object RendererLivingEntityHook {

    private val upsideDown = setOf<String>(
        "Gillsplash",
        "martimavocado"
    )

    @JvmStatic
    fun shouldBeUpsideDown(name: String): Boolean {
        try {
            if (!ravenAddons.config.flipContributors) return false
            return name in upsideDown
        } catch (_: Throwable) {
            return false
        }
    }
}
