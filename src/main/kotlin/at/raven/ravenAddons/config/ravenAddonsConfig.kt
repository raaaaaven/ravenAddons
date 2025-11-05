package at.raven.ravenAddons.config

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.Checkbox
import cc.polyfrost.oneconfig.config.annotations.Color
import cc.polyfrost.oneconfig.config.annotations.Dropdown
import cc.polyfrost.oneconfig.config.annotations.Number
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.annotations.Text
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType
import cc.polyfrost.oneconfig.config.migration.VigilanceMigrator

class ravenAddonsConfig : Config(Mod("ravenAddons", ModType.UTIL_QOL, VigilanceMigrator("./config/ravenaddons.toml")), "ravenaddons.json") {
    @Switch(
        name = "Check for Updates",
        description = "Automatically check for updates on each startup.",
        category = "General",
        subcategory = "Updates"
    )
    var autoUpdates = false

    @Switch(
        name = "Download Updates",
        description = "Automatically download new version on each startup.",
        category = "General",
        subcategory = "Updates",
    )
    var fullAutoUpdates = false

    @Switch(
        name = "Care Package Highlighter",
        description = "Highlights important items inside of Care Packages inside the Hypixel Pit.",
        category = "Pit",
        subcategory = "Care Package",
    )
    var carePackageHighlighter = false

    @Color(
        name = "Care Package Highlight Colour",
        description = "Customize the color related to the Care Package Highlighter.",
        category = "Pit",
        subcategory = "Care Package",
    )
    var carePackageHighlighterColour = OneColor(0, 255, 0, 100)

    @Switch(
        name = "Required Pants Type",
        description = "Adds the colour of pants required to tier 3 a mystic item to it's description.",
        category = "Pit",
        subcategory = "Mystics",
    )
    var requiredPantsType = false

    @Switch(
        name = "Required Pants Type Highlighter",
        description = "Highlights mystics in the Mystic Well based on the colour of pants they require to tier 3.",
        category = "Pit",
        subcategory = "Mystics",
    )
    var highlightRequiredPantsType = false

    @Switch(
        name = "DROP Alerts",
        description = "Message a user about your RARE DROPS.",
        category = "SkyBlock",
        subcategory = "Drops",
    )
    var dropAlert = false

    @Text(
        name = "DROP Alerts Username",
        description = "Choose a username for your RARE DROPS.",
        category = "SkyBlock",
        subcategory = "Drops",
    )
    var dropAlertUserName = ""

    @Switch(
        name = "DROP Title",
        description = "Create a title notification for your RARE drop.",
        category = "SkyBlock",
        subcategory = "Drops",
    )
    var dropTitle = false

    @Checkbox(
        name = "DROP Title Category",
        description = "Choose whether or not to display the category of the drop in the title.",
        category = "SkyBlock",
        subcategory = "Drops",
    )
    var dropTitleCategory = true

    @Dropdown(
        name = "DROP Title Rarity",
        description = "Choose the minimum rarity the drop must be to display the title.",
        category = "SkyBlock",
        subcategory = "Drops",
        options = ["COMMON", "UNCOMMON", "RARE", "EPIC", "LEGENDARY", "MYTHIC"],
    )
    var dropTitleRarity = 0

    @Switch(
        name = "Fire Freeze Timer",
        description = "Display a 10 second timer above a frozen entity's head.\n&cThis feature is disabled in dungeons.",
        category = "SkyBlock",
        subcategory = "Fire Freeze Staff",
    )
    var fireFreezeTimer = false

    @Dropdown(
        name = "Fire Freeze Announcer",
        description = "Announce to your party when a mob becomes frozen or unfrozen.",
        category = "SkyBlock",
        subcategory = "Fire Freeze Staff",
        options = ["None", "Frozen", "Unfrozen", "Both"],
    )
    var fireFreezeAnnounce = 0

    @Switch(
        name = "Fire Freeze Notification",
        description = "Sends a title and chat message for when fire freeze is available after a successful fire freeze on a mob.",
        category = "SkyBlock",
        subcategory = "Fire Freeze Staff",
    )
    var fireFreezeNotification = false

    @Switch(
        name = "Dodge List",
        description = "Enable the player dodge list for party finder.\n&e/ra dodge",
        category = "SkyBlock",
        subcategory = "Dodge List",
    )
    var dodgeList = false

    @Switch(
        name = "Party Check",
        description = "Add an additional party check for people on the dodge list when your party finder group is full.",
        category = "SkyBlock",
        subcategory = "Dodge List",
    )
    var dodgeListFullPartyCheck = false

    @Switch(
        name = "Auto Kick",
        description = "Auto kick users that join your party finder group if they are on your dodge list.",
        category = "SkyBlock",
        subcategory = "Dodge List",
    )
    var dodgeListAutoKick = false

    @Checkbox(
        name = "Auto Kick With Reason",
        description = "Announce the reason when auto kicking.",
        category = "SkyBlock",
        subcategory = "Dodge List",
    )
    var dodgeListAutoKickWithReason = false

    @Switch(
        name = "Lost Time Calculator",
        description = "Sends a chat message calculating how much time was lost due to lag.",
        category = "SkyBlock",
        subcategory = "Instance",
    )
    var lostTimeCalculator = false

    @Switch(
        name = "Blazetekk Ham Radio Message Hider",
        description = "Hides all messages related to the Blazetekk Ham Radio.",
        category = "SkyBlock",
        subcategory = "Blazetekk Ham Radio",
    )
    var blazetekkHamRadioMessageHider = false

    @Switch(
        name = "Mining Ability Notification",
        description = "Display a title when your Mining Ability is ready.",
        category = "Mining",
        subcategory = "Notifications",
    )
    var miningAbilityNotification = false

    @Checkbox(
        name = "Only inside Mining Islands",
        description = "Show Mining Ability Notifications only while you're in a mining island.",
        category = "Mining",
        subcategory = "Notifications",
    )
    var miningAbilityInsideMiningIslands = true

    @Text(
        name = "Mining Ability Notification Sound",
        description = "Choose a Minecraft sound to indicate when your pickaxe ability is ready.",
        category = "Mining",
        subcategory = "Notifications",
    )
    var miningAbilityNotificationSound = "note.pling"

    @Slider(
        name = "Mining Ability Notification Volume",
        description = "Choose the volume for Mining Ability Notification Sound.",
        category = "Mining",
        subcategory = "Notifications",
        min = 0f,
        max = 100f,
        step = 2,
    )
    var miningAbilityNotificationVolume = 100f

    @Slider(
        name = "Mining Ability Notification Pitch",
        description = "Choose the pitch for Mining Ability Notification Sound.",
        category = "Mining",
        subcategory = "Notifications",
        min = 0f,
        max = 200f,
        step = 2,
    )
    var miningAbilityNotificationPitch = 100f

    @Switch(
        name = "Gemstone Powder Notification",
        description = "Display a title based on how much Gemstone Powder you get from chests.",
        category = "Mining",
        subcategory = "Notifications",
    )
    var gemstonePowderNotification = false

    @Slider(
        name = "Powder Notification Amount",
        description = "Minimum amount of Gemstone Powder needed for it to be displayed as a title.",
        category = "Mining",
        subcategory = "Notifications",
        min = 0f,
        max = 10000f,
    )
    var gemstonePowderThreshold = 0

    @Switch(
        name = "Vanguard Notifier",
        description = "Notify your guild of Vanguard Mineshafts and let them join using !ra join.",
        category = "Mining",
        subcategory = "Vanguard",
    )
    var vanguardNotifier = false

    @Checkbox(
        name = "Vanguard Notifier Warp",
        description = "Automatically warp users that join the Vanguard party.",
        category = "Mining",
        subcategory = "Vanguard",
    )
    var vanguardNotifierWarp = false

    @Slider(
        name = "Vanguard Notifier Warp Delay",
        description = "Choose the delay (in seconds) for how long the mod should wait before warping.",
        category = "Mining",
        subcategory = "Vanguard",
        min = 10f,
        max = 30f,
    )
    var vanguardNotifierWarpDelay = 20

    @Checkbox(
        name = "Announce Prefix",
        description = "Choose whether or not you want [RA] infront of your dungeon announcements.",
        category = "Dungeons",
    )
    var announcePrefix = true

    @Switch(
        name = "Simon Says Personal Best",
        description = "Tracks your personal best for the Simon Says device.",
        category = "Dungeons",
        subcategory = "Floor 7 - 1st Device",
    )
    var simonSaysPersonalBest = false

    @Switch(
        name = "Pre 4 Notification",
        description = "Display a title notification for when completing the 4th device.",
        category = "Dungeons",
        subcategory = "Floor 7 - 4th Device",
    )
    var pre4Notification = false

    @Text(
        name = "Pre 4 Title",
        description = "Choose a title for Pre 4 Notification.",
        category = "Dungeons",
        subcategory = "Floor 7 - 4th Device",
    )
    var pre4NotificationTitle = ""

    @Text(
        name = "Pre 4 Subtitle",
        description = "Choose a subtitle for Pre 4 Notification.",
        category = "Dungeons",
        subcategory = "Floor 7 - 4th Device",
    )
    var pre4NotificationSubtitle = ""

    @Switch(
        name = "Pre 4 Announce",
        description = "Announce when you complete the 4th Device in Floor 7.",
        category = "Dungeons",
        subcategory = "Floor 7 - 4th Device",
    )
    var pre4Announce = false

    @Text(
        name = "Pre 4 Message",
        description = "Enter a custom message for the Pre 4 Announce.\nUse §f\$time §7for the time.",
        category = "Dungeons",
        subcategory = "Floor 7 - 4th Device",
    )
    var pre4AnnounceMessage = "Pre 4 complete in \$time."

    @Switch(
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

    @Switch(
        name = "Energy Crystal Notification",
        description = "Shows a reminder on screen when you have an unplaced Energy Crystal.",
        category = "Dungeons",
        subcategory = "Floor 7",
    )
    var energyCrystalNotification = false

    @Switch(
        name = "Leap Announce",
        description = "Announce when you leap to someone in party chat.",
        category = "Dungeons",
        subcategory = "Leap",
    )
    var leapAnnounce = false

    @Text(
        name = "Leap Message",
        description = "Enter a custom message for the leap announce. \nUse §f\$ign §7for the username.",
        category = "Dungeons",
        subcategory = "Leap",
    )
    var leapAnnounceMessage = "Leaping to \$ign."

    @Switch(
        name = "Leap Pling",
        description = "Play the pling sound effect when leaping to someone.",
        category = "Dungeons",
        subcategory = "Leap",
    )
    var leapSound = false

    @Switch(
        name = "Fire Freeze Timer",
        description = "Display a timer for when to freeze The Professor.",
        category = "Dungeons",
        subcategory = "Floor 3",
    )
    var floor3FireFreezeTimer = false

    @Slider(
        name = "Fire Freeze Timer Duration",
        description = "Select how long the timer should last in seconds.",
        category = "Dungeons",
        subcategory = "Floor 3",
        min = 3f,
        max = 5f,
    )
    var floor3FireFreezeDuration = 5f

    @Text(
        name = "Fire Freeze Timer Sound",
        description = "Choose a Minecraft sound to indicate when you should freeze The Professor.",
        category = "Dungeons",
        subcategory = "Floor 3",
    )
    var floor3FireFreezeSound = "random.anvil_land"

    @Slider(
        name = "Fire Freeze Timer Volume",
        description = "Choose the volume for Fire Freeze Timer Sound.",
        category = "Dungeons",
        subcategory = "Floor 3",
        min = 0f,
        max = 100f,
        step = 2,
    )
    var floor3FireFreezeVolume = 100f

    @Slider(
        name = "Fire Freeze Timer Pitch",
        description = "Choose the pitch for Fire Freeze Timer Sound.",
        category = "Dungeons",
        subcategory = "Floor 3",
        min = 0f,
        max = 200f,
        step = 2,
    )
    var floor3FireFreezePitch = 100f

    @Switch(
        name = "Blood Timer",
        description = "Display a message and title for when to kill blood mobs.",
        category = "Dungeons",
        subcategory = "Blood Camp",
    )
    var bloodTimer = false

    @Switch(
        name = "Skeleton Master Chestplate Tracker",
        description = "Tracks how many M7 runs it takes for you to drop a 50/50 Skeleton Master Chestplate.",
        category = "Dungeons",
        subcategory = "Master Mode Floor 7",
    )
    var skeletonMasterChestplateTracker = false

    @Number(
        name = "Skeleton Master Chestplate Tracker Number",
        description = "Stores the amount of runs it takes for the Skeleton Master Chestplate tracker.",
        category = "Dungeons",
        subcategory = "Master Mode Floor 7",
        min = Float.MIN_VALUE,
        max = Float.MAX_VALUE,
        // todo hidden = true,
    )
    var skeletonMasterChestplateTrackerNumber = 0

    @Switch(
        name = "!since",
        description = "Announces to the party how many mobs you have spawned before spawning an Inquisitor.",
        category = "Party Commands",
    )
    var sinceCommand = false

    @Switch(
        name = "Enable Debug Messages",
        description = "This allows the user to see debug messages.",
        category = "Developer",
    )
    var debugMessages = false

    @Text(
        name = "/ra testtitle Title",
        description = "Sets the title for the test title.",
        category = "Developer",
        subcategory = "Title",
    )
    var developerTitle = ""

    @Text(
        name = "/ra testtitle SubTitle",
        description = "Sets the subtitle for the test title.",
        category = "Developer",
        subcategory = "Title",
    )
    var developerSubTitle = ""

    @Switch(
        name = "Flip Contributors",
        description = "Turns contributors upside down.",
        category = "Developer",
        subcategory = "Contributor",
    )
    var flipContributors = true

    @Number(
        name = "ravenAddonsVersion",
        description = "Stores the last loaded ravenAddons version",
        category = "Developer",
        min = Float.MIN_VALUE,
        max = Float.MAX_VALUE,
        // todo hidden = true,
    )
    var configVersion = 0

    @Number(
        name = "sinceInq",
        description = "Stores the number of mobs before inquisitor.",
        category = "Developer",
        min = Float.MIN_VALUE,
        max = Float.MAX_VALUE,
        // todo hidden = true,
    )
    var sinceInq = 0

    @Number(
        name = "Simon Says Personal Best Number",
        description = "Stores the personal best for the 1st device.",
        category = "Developer",
        min = Float.MIN_VALUE,
        max = Float.MAX_VALUE,
        // todo hidden = true,
    )
    var simonSaysPersonalBestNumber = Int.MAX_VALUE

    @Number(
        name = "Pre 4 Personal Best Number",
        description = "Stores the personal best for the 4th device.",
        category = "Developer",
        min = Float.MIN_VALUE,
        max = Float.MAX_VALUE,
        // todo hidden = true,
    )
    var pre4PersonalBestNumber = Int.MAX_VALUE

    init {
        initialize()

        addDependency("carePackageHighlighterColour", "carePackageHighlighter")
        addDependency("highlightRequiredPantsType", "requiredPantsType")

        addDependency("dropAlertUserName", "dropAlert")

        addDependency("dropTitleCategory", "dropTitle")
        addDependency("dropTitleRarity", "dropTitle")

        addDependency("fireFreezeAnnounce", "fireFreezeTimer")

        addDependency("dodgeListFullPartyCheck", "dodgeList")
        addDependency("dodgeListAutoKick", "dodgeList")
        addDependency("dodgeListAutoKickWithReason", "dodgeList")

        addDependency("miningAbilityInsideMiningIslands", "miningAbilityNotification")
        addDependency("miningAbilityNotificationSound", "miningAbilityNotification")
        addDependency("miningAbilityNotificationVolume", "miningAbilityNotification")
        addDependency("miningAbilityNotificationPitch", "miningAbilityNotification")

        addDependency("gemstonePowderThreshold", "gemstonePowderNotification")

        addDependency("vanguardNotifierWarp", "vanguardNotifier")
        addDependency("vanguardNotifierWarpDelay", "vanguardNotifierWarp")

        addDependency("pre4NotificationTitle", "pre4Notification")
        addDependency("pre4NotificationSubtitle", "pre4Notification")

        addDependency("pre4AnnounceMessage", "pre4Announce")

        addDependency("leapAnnounceMessage", "leapAnnounce")

        addDependency("floor3FireFreezeDuration", "floor3FireFreezeTimer")
        addDependency("floor3FireFreezeSound", "floor3FireFreezeTimer")
    }
}
