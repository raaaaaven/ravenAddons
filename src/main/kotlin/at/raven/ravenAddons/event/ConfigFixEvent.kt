package at.raven.ravenAddons.event

import net.minecraftforge.fml.common.eventhandler.Event

class ConfigFixEvent(var configLines: List<String>, val configVersion: Int): Event()