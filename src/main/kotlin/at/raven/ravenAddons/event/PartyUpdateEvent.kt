package at.raven.ravenAddons.event

import at.raven.ravenAddons.data.PartyAPI
import at.raven.ravenAddons.utils.PlayerUtils
import net.minecraftforge.fml.common.eventhandler.Event

class PartyUpdateEvent(val partyList: Map<PlayerUtils.PlayerIdentifier, PartyAPI.PartyRole>) : Event()