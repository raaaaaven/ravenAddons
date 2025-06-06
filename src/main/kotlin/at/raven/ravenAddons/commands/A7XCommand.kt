package at.raven.ravenAddons.commands

import at.raven.ravenAddons.event.CommandRegistrationEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.SoundUtils
import at.raven.ravenAddons.utils.TitleManager
import kotlinx.coroutines.delay
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.time.Duration.Companion.seconds

@LoadModule
object A7XCommand {
    private val albums = mapOf(
        "Sounding the Seventh Trumpet" to listOf(
            "To End the Rapture",
            "Turn the Other Way",
            "Darkness Surrounding",
            "The Art of Subconscious Illusion",
            "We Come Out at Night",
            "Lips of Deceit",
            "Warmness on the Soul",
            "An Epic of Time Wasted",
            "Breaking Their Hold",
            "Forgotten Faces",
            "Thick and Thin",
            "Streets",
            "Shattered by Broken Dreams"
        ),

        "Waking The Fallen" to listOf(
            "Unholy Confessions",
            "Chapter Four",
            "Remenissions",
            "Desecrate Through Reverence",
            "Eternal Rest",
            "Second Heartbeat",
            "Radiant Eclipse",
            "I Won't See You Tonight Part 1",
            "I Won't See You Tonight Part 2",
            "Clairvoyant Disease",
            "And All Things Will End"
        ),

        "City of Evil" to listOf(
            "Beast and the Harlot",
            "Burn it Down",
            "Blinded in Chains",
            "Bat Country",
            "Trashed and Scattered",
            "Seize the Day",
            "Sidewinder",
            "The Wicked End",
            "Strength of the World",
            "Betrayed",
            "M.I.A"
        ),

        "Avenged Sevenfold" to listOf(
            "Critical Acclaim",
            "Almost Easy",
            "Scream",
            "Afterlife",
            "Gunslinger",
            "Unbound (The Wild Ride)",
            "Brompton Cocktail",
            "Lost",
            "A Little Piece of Heaven",
            "Dear God"
        ),

        "Nightmare" to listOf(
            "Nightmare",
            "Welcome to the Family",
            "Danger Line",
            "Buried Alive",
            "Natural Born Killer",
            "So Far Away",
            "God Hates Us",
            "Victim",
            "Tonight the World Dies",
            "Fiction",
            "Save Me"
        ),

        "Hail to the King" to listOf(
            "Shepherd of Fire",
            "Hail to the King",
            "Doing Time",
            "This Means War",
            "Requiem",
            "Crimson Day",
            "Heretic",
            "Coming Home",
            "Planets",
            "Acid Rain"
        ),

        "The Stage (Deluxe Edition)" to listOf(
            "The Stage",
            "Paradigm",
            "Sunny Disposition",
            "God Damn",
            "Creating God",
            "Angels",
            "Simulation",
            "Higher",
            "Roman Sky",
            "Fermi Paradox",
            "Exist",
            "Dose",
            "Retrovertigo",
            "Malagueña Salerosa",
            "Runaway",
            "As Tears Go By",
            "Wish You Were Here",
            "God Only Knows"
        ),

        "Black Reign" to listOf(
            "Mad Hatter",
            "Carry On",
            "Not Ready to Die - From \"Call of the Dead\"",
            "Jade Helm - Instrumental"
        ),

        "Diamonds in the Rough" to listOf(
            "Demons",
            "Girl I Know",
            "Crossroads",
            "Flash of the Blade",
            "Until the End",
            "Tension",
            "Walk",
            "The Fight",
            "Dancing Dead",
            "St. James",
            "Set Me Free",
            "4:00 AM",
            "Lost It All",
            "Paranoid"
        ),

        "Live in the LBC" to listOf(
            "Critical Acclaim - Live",
            "Second Heartbeat - Live",
            "Afterlife - Live",
            "Beast and the Harlot - Live",
            "Scream - Live",
            "Seize the Day - Live",
            "Walk - Live",
            "Bat Country - Live",
            "Almost Easy - Live",
            "Gunslinger - Live",
            "Unholy Confessions - Live",
            "A Little Piece of Heaven - Live"
        ),

        "Life Is But a Dream..." to listOf(
            "Game Over",
            "Mattel",
            "Nobody",
            "We Love You",
            "Cosmic",
            "Beautiful Morning",
            "Easier",
            "G",
            "(O)rdinary",
            "(D)eath",
            "Life Is But a Dream..."
        )
    )

    private val colours = mapOf(
        "Sounding the Seventh Trumpet" to "§d",
        "Waking The Fallen" to "§8",
        "City of Evil" to "§c",
        "Avenged Sevenfold" to "§f",
        "Nightmare" to "§9",
        "Hail to the King" to "§7",
        "The Stage (Deluxe Edition)" to "§5",
        "Black Reign" to "§4",
        "Diamonds in the Rough" to "§2",
        "Live in the LBC" to "§a",
        "Life Is But a Dream..." to "§e"
    )

    private val songs: List<Pair<String, String>> = albums.flatMap { (album, songs) -> songs.map { song -> album to song }}

    @SubscribeEvent
    fun onCommandRegistration(event: CommandRegistrationEvent) {
        event.register("a7x") {
            description = "Roll a random Avenged Sevenfold song."
            callback { roll() }
        }
    }

    private fun sound(pitch: Float = 1.0f) {
        SoundUtils.playSound("note.pling", 1.0f, pitch)
    }

    private fun roll() {
        val rolls = 20
        val minimum = 0.05.seconds
        val maximum = 0.50.seconds
        val delay = (maximum - minimum) / rolls

        ravenAddons.launchCoroutine {
            var currentDelay = minimum
            repeat(rolls) { i ->
                val (album, song) = songs.random()
                val colour = colours[album] ?: "§f"
                TitleManager.setTitle(
                    "$colour$song",
                    "$colour$album",
                    1.seconds,
                    0.seconds,
                    0.seconds
                )
                sound(pitch = 1.0f + i * 0.05f)
                delay(currentDelay)
                currentDelay += delay
            }
            val (album, song) = songs.random()
            val colour = colours[album] ?: "§f"
            TitleManager.setTitle(
                "$colour$song",
                "$colour$album",
                2.5.seconds,
                0.seconds,
                0.seconds
            )
            sound(pitch = 2.0f)
            ChatUtils.chat("Your Avenged Sevenfold Song is $colour$song §7from $colour$album§7.")
        }
    }
}