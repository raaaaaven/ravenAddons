package at.raven.ravenAddons.event

import at.raven.ravenAddons.event.base.RavenEvent

@Suppress("CanBeParameter", "MemberVisibilityCanBePrivate")
class ScoreboardUpdateEvent(
    val old: List<String>,
    val new: List<String>,
) : RavenEvent() {
    val added = new - old.toSet()
    val removed = old - new.toSet()
}
