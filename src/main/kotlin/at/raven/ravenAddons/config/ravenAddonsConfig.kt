package at.raven.ravenAddons.config

import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyType
import org.fusesource.jansi.Ansi
import java.awt.Color
import java.io.File

object ravenAddonsConfig : Vigilant(
    File("./config/ravenAddons.toml"),
    sortingBehavior = ConfigSorting()
) {

    @Property(
        type = PropertyType.SWITCH,
        name = "QUICK MATHS! Solver",
        description = "Solves the QUICK MATHS! equation for you.\nUseful for §eHypixel SkyBlock §rand §eThe Pit§r.",
        category = "General"
    )
    var quickMathsSolver = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Check for Updates",
        description = "Automatically check for updates on each startup.",
        category = "General",
        subcategory = "Updates"
    )
    var autoUpdates = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Download Updates",
        description = "Automatically download new version on each startup.",
        category = "General",
        subcategory = "Updates"
    )
    var fullAutoUpdates = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Care Package Highlighter",
        description = "Highlights important items inside of Care Packages inside the Hypixel Pit.",
        category = "Pit"
    )
    var carePackageHighlighter = false

    @Property(
        type = PropertyType.COLOR,
        name = "Care Package Highlight Colour",
        description = "Customize the color related to the Care Package Highlighter.",
        category = "Pit"
    )
    var carePackageHighlighterColour = Color(0, 255, 0, 100)

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
        name = "Mining Ability Notification",
        description = "Display a title when your Mining Ability is ready.",
        category = "Mining"
    )
    var miningAbilityNotification = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Gemstone Powder Notification",
        description = "Display a title based on how much Gemstone Powder you get from chests.",
        category = "Mining"
    )
    var gemstonePowderNotification = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Better Device Notifications",
        description = "Replace Hypixel's device titles for your username.",
        category = "Dungeons"
    )
    var betterDeviceNotification = false

    @Property(
        type = PropertyType.TEXT,
        name = "Better Device Notifications Title",
        description = "Choose a title.",
        category = "Dungeons"
    )
    var betterDeviceNotificationTitle = ""

    @Property(
        type = PropertyType.TEXT,
        name = "Better Device Notifications SubTitle",
        description = "Choose a subtitle.",
        category = "Dungeons"
    )
    var betterDeviceNotificationSubTitle = ""

    @Property(
        type = PropertyType.SWITCH,
        name = "Enable Debug Messages",
        description = "This allows the user to see debug messages.",
        category = "Developer"
    )
    var debugMessages = false

    @Property(
        type = PropertyType.TEXT,
        name = "/ratesttitle Title",
        description = "Sets the title for the test title.",
        category = "Developer"
    )
    var developerTitle = ""

    @Property(
        type = PropertyType.TEXT,
        name = "/ratesttitle SubTitle",
        description = "Sets the subTitle for the test title.",
        category = "Developer"
    )
    var developerSubTitle = ""

    init {
        initialize()

        val clazz = javaClass

        addDependency(clazz.getDeclaredField("carePackageHighlighterColour"), clazz.getDeclaredField("carePackageHighlighter"))

        addDependency(clazz.getDeclaredField("dropAlertUserName"), clazz.getDeclaredField("dropAlert"))

        addDependency(clazz.getDeclaredField("betterDeviceNotificationTitle"), clazz.getDeclaredField("betterDeviceNotification"))
        addDependency(clazz.getDeclaredField("betterDeviceNotificationSubTitle"), clazz.getDeclaredField("betterDeviceNotification"))
    }
}