package at.raven.ravenAddons.event

import at.raven.ravenAddons.data.PartyAPI
import at.raven.ravenAddons.event.base.RavenEvent
import at.raven.ravenAddons.utils.PlayerUtils

class PartyUpdateEvent(val partyList: Map<PlayerUtils.PlayerIdentifier, PartyAPI.PartyRole>) : RavenEvent()