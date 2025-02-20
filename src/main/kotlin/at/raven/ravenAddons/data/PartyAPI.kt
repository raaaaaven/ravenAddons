package at.raven.ravenAddons.data

import at.raven.ravenAddons.data.PartyAPI.PartyRole.Companion.getRole
import at.raven.ravenAddons.event.PartyUpdateEvent
import at.raven.ravenAddons.event.hypixel.HypixelPartyEvent
import at.raven.ravenAddons.event.managers.HypixelEvents
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.EventUtils.post
import at.raven.ravenAddons.utils.PlayerUtils.getPlayerName
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPartyInfoPacket
import net.hypixel.modapi.packet.impl.serverbound.ServerboundPartyInfoPacket
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@LoadModule
object PartyAPI {
    var partyList: Map<String, PartyRole> = emptyMap()
        private set

    @SubscribeEvent
    fun onPartyPacket(event: HypixelPartyEvent) {
        ravenAddons.launchCoroutine {
            if (!event.inParty) {
                partyList = emptyMap()
            }

            val newPartyMap = mutableMapOf<String, PartyRole>()

            for ((memberUUID, role) in event.memberMap) {
                val name = memberUUID.getPlayerName() ?: continue

                newPartyMap.put(name, role.role.getRole())
            }

            partyList = newPartyMap.toMap()
            PartyUpdateEvent(partyList).post()
        }
    }

    fun sendPartyPacket() = HypixelEvents.modApi.sendPacket(ServerboundPartyInfoPacket())

    enum class PartyRole(private val hypixelRole: ClientboundPartyInfoPacket.PartyRole) {
        LEADER(ClientboundPartyInfoPacket.PartyRole.LEADER),
        MODERATOR(ClientboundPartyInfoPacket.PartyRole.MOD),
        MEMBER(ClientboundPartyInfoPacket.PartyRole.MEMBER),
        ;

        companion object {
            fun ClientboundPartyInfoPacket.PartyRole.getRole() = entries.first { it.hypixelRole == this }
        }
    }
}