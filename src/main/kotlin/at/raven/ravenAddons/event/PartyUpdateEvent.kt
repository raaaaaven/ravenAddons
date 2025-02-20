package at.raven.ravenAddons.event

import at.raven.ravenAddons.data.PartyAPI
import net.minecraftforge.fml.common.eventhandler.Event

class PartyUpdateEvent(val partyList: Map<String, PartyAPI.PartyRole>) : Event()