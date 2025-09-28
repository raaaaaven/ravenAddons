package at.raven.ravenAddons.features.pit

import at.raven.ravenAddons.config.ravenAddonsConfig
import at.raven.ravenAddons.data.HypixelGame
import at.raven.ravenAddons.event.GameLoadEvent
import at.raven.ravenAddons.event.render.RenderOverlayEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.utils.APIUtils
import at.raven.ravenAddons.utils.SimpleTimeMark
import at.raven.ravenAddons.utils.render.GuiRenderUtils.renderStrings
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.time.Duration
import kotlin.time.DurationUnit

@LoadModule
object UpcomingEventsDisplay {

    private var eventCache = mutableListOf<PitEventData>()
    private var initialized = false
    private val gson = Gson()

    private val actualEventNames = mapOf(
        "All bounty" to "§6Everyone Gets a Bounty",
        "Quick Maths" to "§dQuick Maths",
        "Dragon Egg" to "§dDragon Egg",
        "2x Rewards" to "§22x Rewards",
        "Care Package" to "§6Care Package",
        "KOTL" to "§aKing of the Ladder",
        "KOTH" to "§bKing of the Hill",
        "Auction" to "§eAuction",
        "Giant Cake" to "§dGiant Cake",
        "Squads" to "§bSquads",
        "Beast" to "§aBeast",
        "Pizza" to "§cPizza",
        "Rage Pit" to "§cRage Pit",
        "Spire" to "§dSpire",
        "Robbery" to "§6Robbery",
        "Blockhead" to "§9Blockhead",
        "Raffle" to "§6Raffle",
        "Team Deathmatch" to "§dTeam Deathmatch",
    )

    private data class PitEventData(
        val event: String,
        val timestamp: Long,
        val type: String,
    )


    @SubscribeEvent
    fun onGameLoaded(event: GameLoadEvent) {
        if (!ravenAddonsConfig.upcomingEventsDisplay) return
        getPitEventData()
        initialized = true
    }

    @SubscribeEvent
    fun onRenderOverlay(event: RenderOverlayEvent) {
        if (!HypixelGame.inPit || !ravenAddonsConfig.upcomingEventsDisplay) return
        if (eventCache.isEmpty() && !initialized) getPitEventData().also { initialized = true }

        updatePitEventData()
        ravenAddonsConfig.upcomingEventsDisplayPosition.renderStrings(buildDisplay(), "upcomingEventsDisplay")
    }

    private fun buildDisplay(): List<String> {
        val nextEvents = eventCache.take(ravenAddonsConfig.upcomingEventsDisplayAmount)

        return buildList {
            nextEvents.forEach { eventData ->
                val timeUntil = eventData.timestamp.toSimpleTimeMark().timeUntil()
                val formattedName = actualEventNames[eventData.event] ?: eventData.event

                add("$formattedName §7- ${timeUntil.getDurationString()}")
            }
        }
    }

    private fun Duration.getDurationString(): String {
        return when {
            inWholeMinutes <= 0L -> toString(DurationUnit.SECONDS)
            inWholeMinutes in 1L..59L -> toString(DurationUnit.MINUTES)
            inWholeMinutes in 60L..1439L -> "${inWholeHours}h ${inWholeMinutes % 60}m"
            else -> "${inWholeDays}d ${inWholeHours % 24}h"
        }
    }

    private fun getPitEventData() {
        val rawData =
            APIUtils.getJsonArrayResponse("https://raw.githubusercontent.com/BrookeAFK/brookeafk-api/main/events.js") ?: return

        val parsedEvents: List<PitEventData> = gson.fromJson(rawData, object : TypeToken<List<PitEventData>>() {}.type)

        eventCache += parsedEvents.filterNot {
            it.timestamp.toSimpleTimeMark().isInPast()
        }
    }

    private fun updatePitEventData() = eventCache.removeAll { SimpleTimeMark(it.timestamp).isInPast() }

    private fun Long.toSimpleTimeMark() = SimpleTimeMark(this)
}
