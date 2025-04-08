package at.raven.ravenAddons.config

import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyType
import java.awt.Color
import kotlin.reflect.KProperty

object ravenAddonsConfig : Vigilant(
    ConfigFixer.configFile,
    sortingBehavior = ConfigSorting()
) {
    private val clazz = javaClass

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
        category = "Pit",
        subcategory = "Care Package"
    )
    var carePackageHighlighter = false

    @Property(
        type = PropertyType.COLOR,
        name = "Care Package Highlight Colour",
        description = "Customize the color related to the Care Package Highlighter.",
        category = "Pit",
        subcategory = "Care Package"
    )
    var carePackageHighlighterColour = Color(0, 255, 0, 100)

    @Property(
        type = PropertyType.SWITCH,
        name = "DROP Alerts",
        description = "Message a user about your RARE DROPS.",
        category = "SkyBlock",
        subcategory = "Drops"
    )
    var dropAlert = false

    @Property(
        type = PropertyType.TEXT,
        name = "DROP Alerts Username",
        description = "Choose a username for your RARE DROPS.",
        category = "SkyBlock",
        subcategory = "Drops"
    )
    var dropAlertUserName = ""

    @Property(
        type = PropertyType.SWITCH,
        name = "DROP Title",
        description = "Create a title notification for your RARE drop.",
        category = "SkyBlock",
        subcategory = "Drops"
    )
    var dropTitle = false

    @Property(
        type = PropertyType.CHECKBOX,
        name = "DROP Title Category",
        description = "Choose whether or not to display the category of the drop in the title.",
        category = "SkyBlock",
        subcategory = "Drops"
    )
    var dropTitleCategory = true

    @Property(
        type = PropertyType.SELECTOR,
        name = "DROP Title Rarity",
        description = "Choose the minimum rarity the drop must be to display the title.",
        category = "SkyBlock",
        subcategory = "Drops",
        options = ["COMMON", "UNCOMMON", "RARE", "EPIC", "LEGENDARY", "MYTHIC"]
    )
    var dropTitleRarity = 0

    @Property(
        type = PropertyType.SWITCH,
        name = "Fire Freeze Timer",
        description = "Display a 10 second timer above a frozen entity's head.",
        category = "SkyBlock",
        subcategory = "Fire Freeze Staff"
    )
    var fireFreezeTimer = false

    @Property(
        type = PropertyType.SELECTOR,
        name = "Fire Freeze Announcer",
        description = "Announce to your party when a mob becomes frozen or unfrozen.",
        category = "SkyBlock",
        subcategory = "Fire Freeze Staff",
        options = ["None", "Frozen", "Unfrozen", "Both"]
    )
    var fireFreezeAnnounce = 0

    @Property(
        type = PropertyType.SWITCH,
        name = "Fire Freeze Notification",
        description = "Sends a title and chat message for when fire freeze is available after a successful fire freeze on a mob.",
        category = "SkyBlock",
        subcategory = "Fire Freeze Staff"
    )
    var fireFreezeNotification = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Dodge List",
        description = "Enable the player dodge list for party finder.\n&e/ra dodge",
        category = "SkyBlock",
        subcategory = "Dodge List"
    )
    var dodgeList = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Party Check",
        description = "Add an additional party check for people on the dodge list when your party finder group is full.",
        category = "SkyBlock",
        subcategory = "Dodge List"
    )
    var dodgeListFullPartyCheck = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Auto Kick",
        description = "Auto kick users that join your party finder group if they are on your dodge list.",
        category =  "SkyBlock",
        subcategory = "Dodge List"
    )
    var dodgeListAutoKick = false

    @Property(
        type = PropertyType.CHECKBOX,
        name = "Auto Kick With Reason",
        description = "Announce the reason just before the auto kick.",
        category =  "SkyBlock",
        subcategory = "Dodge List"
    )
    var dodgeListAutoKickWithReason = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Mining Ability Notification",
        description = "Display a title when your Mining Ability is ready.",
        category = "Mining",
        subcategory = "Notifications"
    )
    var miningAbilityNotification = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Gemstone Powder Notification",
        description = "Display a title based on how much Gemstone Powder you get from chests.",
        category = "Mining",
        subcategory = "Notifications"
    )
    var gemstonePowderNotification = false

    @Property(
        type = PropertyType.SLIDER,
        name = "Powder Notification Amount",
        description = "Minimum amount of Gemstone Powder needed for it to be displayed as a title.",
        category = "Mining",
        subcategory = "Notifications",
        min = 0,
        max = 10000
    )
    var gemstonePowderThreshold = 0

    @Property(
        type = PropertyType.SWITCH,
        name = "Better Device Notifications",
        description = "Replace Hypixel's device titles for your username.",
        category = "Dungeons",
        subcategory = "Floor 7"
    )
    var betterDeviceNotification = false

    @Property(
        type = PropertyType.TEXT,
        name = "Title",
        description = "Choose a title for Better Device Notifications.",
        category = "Dungeons",
        subcategory = "Floor 7"
    )
    var betterDeviceNotificationTitle = ""

    @Property(
        type = PropertyType.TEXT,
        name = "Subtitle",
        description = "Choose a subtitle for Better Device Notifications.",
        category = "Dungeons",
        subcategory = "Floor 7"
    )
    var betterDeviceNotificationSubTitle = ""

    @Property(
        type = PropertyType.SWITCH,
        name = "Energy Crystal Notification",
        description = "Shows a reminder on screen when you have an unplaced Energy Crystal.",
        category = "Dungeons",
        subcategory = "Floor 7"
    )
    var energyCrystalNotification = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Leap Announce",
        description = "Announce when you leap to someone in party chat.",
        category = "Dungeons",
        subcategory = "Leap"
    )
    var leapAnnounce = false

    @Property(
        type = PropertyType.TEXT,
        name = "Leap Message",
        description = "Enter a custom message for the leap announce. \nUse §f\$ign §7for the username.",
        category = "Dungeons",
        subcategory = "Leap"
    )
    var leapAnnounceMessage = "Leaping to \$ign."

    @Property(
        type = PropertyType.CHECKBOX,
        name = "Leap Prefix",
        description = "Enable having [RA] in front of your leap announce?",
        category = "Dungeons",
        subcategory = "Leap"
    )
    var leapAnnouncePrefix = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Leap Pling",
        description = "Play the pling sound effect when leaping to someone.",
        category = "Dungeons",
        subcategory = "Leap"
    )
    var leapSound = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Fire Freeze Timer",
        description = "Display a timer for when to freeze The Professor.",
        category = "Dungeons",
        subcategory = "Floor 3"
    )
    var floor3FireFreezeTimer = false

    @Property(
        type = PropertyType.SLIDER,
        name = "Fire Freeze Timer Duration",
        description = "Select how long the timer should last in seconds.",
        category = "Dungeons",
        subcategory = "Floor 3",
        min = 3,
        max = 5
    )
    var floor3FireFreezeDuration = 5

    @Property(
        type = PropertyType.TEXT,
        name = "Fire Freeze Timer Sound",
        description = "Choose a minecraft sound to play when you should freeze The Professor. Default: &erandom.anvil_land",
        category = "Dungeons",
        subcategory = "Floor 3"
    )
    var floor3FireFreezeSound = "random.anvil_land"

    @Property(
        type = PropertyType.SWITCH,
        name = "!since",
        description = "Announces to the party how many mobs you have spawned before spawning an Inquisitor.",
        category = "Party Commands"
    )
    var sinceCommand = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Enable Debug Messages",
        description = "This allows the user to see debug messages.",
        category = "Developer"
    )
    var debugMessages = false

    @Property(
        type = PropertyType.TEXT,
        name = "/ra testtitle Title",
        description = "Sets the title for the test title.",
        category = "Developer",
        subcategory = "Title"
    )
    var developerTitle = ""

    @Property(
        type = PropertyType.TEXT,
        name = "/ra testtitle SubTitle",
        description = "Sets the subTitle for the test title.",
        category = "Developer",
        subcategory = "Title"
    )
    var developerSubTitle = ""

    @Property(
        type = PropertyType.SWITCH,
        name = "Flip Contributors",
        description = "Turns contributors upside down.",
        category = "Developer",
        subcategory = "Contributor"
    )
    var flipContributors = true

    @Property(
        type = PropertyType.NUMBER,
        name = "ravenAddonsVersion",
        description = "Stores the last loaded ravenAddons version",
        category = "Developer",
        hidden = true
    )
    var configVersion = 0

    @Property(
        type = PropertyType.NUMBER,
        name = "sinceInq",
        description = "Stores the number of mobs before inquisitor.",
        category = "Developer",
        hidden = true
    )
    var sinceInq = 0

    init {
        initialize()

        this::carePackageHighlighterColour requires this::carePackageHighlighter

        this::dropAlertUserName requires this::dropAlert

        this::fireFreezeAnnounce requires this::fireFreezeTimer

        this::dodgeListFullPartyCheck requires this::dodgeList
        this::dodgeListAutoKick requires this::dodgeList
        this::dodgeListAutoKickWithReason requires this::dodgeList

        this::gemstonePowderThreshold requires this::gemstonePowderNotification

        this::betterDeviceNotificationTitle requires this::betterDeviceNotification
        this::betterDeviceNotificationSubTitle requires this::betterDeviceNotification

        this::leapAnnounceMessage requires this::leapAnnounce
        this::leapAnnouncePrefix requires this::leapAnnounce

        this::floor3FireFreezeDuration requires this::floor3FireFreezeTimer
        this::floor3FireFreezeSound requires this::floor3FireFreezeTimer
    }


    infix fun <T> KProperty<T>.requires(dependency: KProperty<T>) {
        addDependency(clazz.getDeclaredField(this.name), clazz.getDeclaredField(dependency.name))
    }
}