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
        description = "Display a 10 second timer above a frozen entity's head.\n&cThis feature is disabled in dungeons.",
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
        description = "Announce the reason when auto kicking.",
        category =  "SkyBlock",
        subcategory = "Dodge List"
    )
    var dodgeListAutoKickWithReason = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Lost Time Calculator",
        description = "Sends a chat message calculating how much time was lost due to lag.",
        category = "SkyBlock",
        subcategory = "Instance"
    )
    var lostTimeCalculator = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Blazetekk Ham Radio Message Hider",
        description = "Hides all messages related to the Blazetekk Ham Radio.",
        category = "SkyBlock",
        subcategory = "Blazetekk Ham Radio"
    )
    var blazetekkHamRadioMessageHider = false

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
        name = "Vanguard Notifier",
        description = "Notify your guild of Vanguard Mineshafts and let them join using !ra join.",
        category = "Mining",
        subcategory = "Vanguard"
    )
    var vanguardNotifier = false

    @Property(
        type = PropertyType.CHECKBOX,
        name = "Vanguard Notifier Warp",
        description = "Automatically warp users that join the Vanguard party.",
        category = "Mining",
        subcategory = "Vanguard"
    )
    var vanguardNotifierWarp = false

    @Property(
        type = PropertyType.SLIDER,
        name = "Vanguard Notifier Warp Delay",
        description = "Choose the delay (in seconds) for how long the mod should wait before warping.",
        category = "Mining",
        subcategory = "Vanguard",
        min = 10,
        max = 30
    )
    var vanguardNotifierWarpDelay = 20

    @Property(
        type = PropertyType.CHECKBOX,
        name = "Announce Prefix",
        description = "Choose whether or not you want [RA] infront of your dungeon announcements.",
        category = "Dungeons"
    )
    var announcePrefix = true

    @Property(
        type = PropertyType.SWITCH,
        name = "Simon Says Personal Best",
        description = "Tracks your personal best for the Simon Says device.",
        category = "Dungeons",
        subcategory = "Floor 7 - 1st Device",
    )
    var simonSaysPersonalBest = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Pre 4 Notification",
        description = "Display a title notification for when completing the 4th device.",
        category = "Dungeons",
        subcategory = "Floor 7 - 4th Device"
    )
    var pre4Notification = false

    @Property(
        type = PropertyType.TEXT,
        name = "Pre 4 Title",
        description = "Choose a title for Pre 4 Notification.",
        category = "Dungeons",
        subcategory = "Floor 7 - 4th Device"
    )
    var pre4NotificationTitle = ""

    @Property(
        type = PropertyType.TEXT,
        name = "Pre 4 Subtitle",
        description = "Choose a subtitle for Pre 4 Notification.",
        category = "Dungeons",
        subcategory = "Floor 7 - 4th Device"
    )
    var pre4NotificationSubtitle = ""

    @Property(
        type = PropertyType.SWITCH,
        name = "Pre 4 Announce",
        description = "Announce when you complete the 4th Device in Floor 7.",
        category = "Dungeons",
        subcategory = "Floor 7 - 4th Device"
    )
    var pre4Announce = false

    @Property(
        type = PropertyType.TEXT,
        name = "Pre 4 Message",
        description = "Enter a custom message for the Pre 4 Announce.\nUse §f\$time §7for the time.",
        category = "Dungeons",
        subcategory = "Floor 7 - 4th Device"
    )
    var pre4AnnounceMessage = "Pre 4 complete in \$time."

    @Property(
        type = PropertyType.SWITCH,
        name = "Pre 4 Personal Best",
        description = "Tracks your personal best for a successful Pre 4.",
        category = "Dungeons",
        subcategory = "Floor 7 - 4th Device",
    )
    var pre4PersonalBest = false

    /*@Property(
        type = PropertyType.SWITCH,
        name = "Enter Section 4 Title",
        description = "Display a title when someone leaps to you while waiting to enter the 4th section of phase 3.",
        category = "Dungeons",
        subcategory = "Floor 7 - 4th Device"
    )
    var enterSection4Title = false*/

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
        description = "Choose a Minecraft sound to indicate when you should freeze The Professor, with the default sound being &erandom.anvil_land&7.",
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
        description = "Sets the subtitle for the test title.",
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

    @Property(
        type = PropertyType.NUMBER,
        name = "Simon Says Personal Best Number",
        description = "Stores the personal best for the 1st device.",
        category = "Developer",
        hidden = true
    )
    var simonSaysPersonalBestNumber = Int.MAX_VALUE

    @Property(
        type = PropertyType.NUMBER,
        name = "Pre 4 Personal Best Number",
        description = "Stores the personal best for the 4th device.",
        category = "Developer",
        hidden = true
    )
    var pre4PersonalBestNumber = Int.MAX_VALUE

    init {
        initialize()

        this::carePackageHighlighterColour requires this::carePackageHighlighter

        this::dropAlertUserName requires this::dropAlert

        this::dropTitleCategory requires this::dropTitle
        this::dropTitleRarity requires this::dropTitle

        this::fireFreezeAnnounce requires this::fireFreezeTimer

        this::dodgeListFullPartyCheck requires this::dodgeList
        this::dodgeListAutoKick requires this::dodgeList
        this::dodgeListAutoKickWithReason requires this::dodgeList

        this::gemstonePowderThreshold requires this::gemstonePowderNotification

        this::vanguardNotifierWarp requires this::vanguardNotifier
        this::vanguardNotifierWarpDelay requires this::vanguardNotifierWarp

        this::pre4NotificationTitle requires this::pre4Notification
        this::pre4NotificationSubtitle requires this::pre4Notification

        this::pre4AnnounceMessage requires this::pre4Announce

        this::leapAnnounceMessage requires this::leapAnnounce

        this::floor3FireFreezeDuration requires this::floor3FireFreezeTimer
        this::floor3FireFreezeSound requires this::floor3FireFreezeTimer
    }


    infix fun <T> KProperty<T>.requires(dependency: KProperty<T>) {
        addDependency(clazz.getDeclaredField(this.name), clazz.getDeclaredField(dependency.name))
    }
}