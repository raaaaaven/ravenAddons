package at.raven.ravenAddons.config

import gg.essential.universal.UChat
import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyType
import java.awt.Color
import java.io.File
import kotlin.math.PI

/**
 * An configuration which gives an overview of all property types,
 * as well as a visual demonstration of each option. Also demos some
 * aspects such as fields with different initial values.
 */
object ravenAddonsConfig : Vigilant(File("./config/ravenAddons.toml")) {

    @Property(
        type = PropertyType.SWITCH,
        name = "DROP Alerts",
        description = "Message a user about your RARE DROPS.",
        category = "SkyBlock"
    )
    var dropAlert = false

    @Property(
        type = PropertyType.TEXT,
        name = "DROP Alerts Username",
        description = "Choose a username for your RARE DROPS.",
        category = "SkyBlock"
    )
    var dropAlertUserName = ""

    @Property(
        type = PropertyType.SWITCH,
        name = "Enable Debug Messages",
        description = "This is a text property. It stores a single line of continuous text.",
        category = "Developer"
    )
    var debugMessages = false

    init {
        initialize()

        val clazz = javaClass

        addDependency(clazz.getDeclaredField("dropAlertUserName"), clazz.getDeclaredField("dropAlert"))
    }
}