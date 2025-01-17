package at.raven.ravenAddons.config

import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyType
import java.io.File

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