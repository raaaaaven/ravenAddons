package at.raven.ravenAddons.data

import at.raven.ravenAddons.event.DebugDataCollectionEvent
import at.raven.ravenAddons.event.hypixel.HypixelServerChangeEvent
import at.raven.ravenAddons.event.hypixel.IslandChangeEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.utils.EventUtils.post
import at.raven.ravenAddons.utils.StringUtils.toFormattedName
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

enum class SkyBlockIsland(val id: String, displayName: String? = null) {
    PRIVATE_ISLAND("dynamic"),
    GARDEN("garden"),
    HUB("hub"),
    THE_FARMING_ISLANDS("farming_1"),
    THE_PARK("foraging_1"),
    SPIDER_DEN("combat_1", "Spider's Den"),
    THE_END("combat_3"),
    CRIMSON_ISLE("crimson_isle"),
    GOLD_MINES("mining_1"),
    DEEP_CAVERNS("mining_2"),
    DWARVEN_MINES("mining_3"),
    CRYSTAL_HOLLOWS("crystal_hollows"),
    MINESHAFT("mineshaft"),
    WINTER("winter", "Jerry's Workshop"),
    THE_RIFT("rift"),
    BACKWATER_BAYOU("fishing_1"),

    DUNGEON_HUB("dungeon_hub"),
    DARK_AUCTION("dark_auction"),
    KUUDRA("kuudra"),
    CATACOMBS("dungeon"),
    ;

    fun isInIsland(): Boolean = this == current

    private val displayName: String = displayName ?: toFormattedName()
    override fun toString() = displayName

    @LoadModule
    companion object {
        var current: SkyBlockIsland? = null
            private set

        fun getById(id: String): SkyBlockIsland? = entries.find { it.id == id }

        fun inAnyIsland(vararg islands: SkyBlockIsland): Boolean {
            val current = current ?: return false
            return current in islands
        }
        fun inAnyIsland(islands: Collection<SkyBlockIsland>): Boolean {
            val current = current ?: return false
            return current in islands
        }

        val miningIslands = setOf(
            GOLD_MINES,
            DEEP_CAVERNS,
            DWARVEN_MINES,
            CRYSTAL_HOLLOWS,
            MINESHAFT
        )

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        fun onHypixelData(event: HypixelServerChangeEvent) {
            val old = current
            current = if (event.mode != null) getById(event.mode) else null
            if (old == current) return
            IslandChangeEvent(current, old).post()
        }

        @SubscribeEvent
        fun onDebug(event: DebugDataCollectionEvent) {
            event.title("SkyBlockIsland")
            if (current == null) {
                event.addIrrelevant("Not in any island")
            } else {
                event.addData("Island: $current")
            }
        }

    }
}