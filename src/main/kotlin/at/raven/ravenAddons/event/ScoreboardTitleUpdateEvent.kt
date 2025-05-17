package at.raven.ravenAddons.event

import at.raven.ravenAddons.event.base.RavenEvent

class ScoreboardTitleUpdateEvent(
    val title: String,
    val objective: String,
) : RavenEvent()