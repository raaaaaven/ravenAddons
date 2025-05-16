package at.raven.ravenAddons.event

import at.raven.ravenAddons.event.base.RavenEvent

open class ScoreboardUpdateEvent : RavenEvent() {
    open class Title(
        val title: String,
        val objective: String,
    ) : ScoreboardUpdateEvent()

    open class Content(
        val oldLines: List<String>,
        val newLines: List<String>,
    ) : ScoreboardUpdateEvent()
}
