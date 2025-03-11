package at.raven.ravenAddons.mixin.hooks

import at.raven.ravenAddons.config.ravenAddonsConfig

object RendererLivingEntityHook {

    private val upsideDown = setOf<String>("Gillsplash", "martimavocado")
    @JvmStatic
    fun shouldBeUpsideDown(name: String): Boolean {
        if (!ravenAddonsConfig.flipContributors) return false

        return name in upsideDown
    }
}