package at.raven.ravenaddons.config

import gg.essential.vigilance.data.Category
import gg.essential.vigilance.data.SortingBehavior

class ConfigSorting: SortingBehavior() {

    private val categories: List<String> = listOf("General", "Pit", "SkyBlock", "Dungeons", "Kuudra", "Slayers", "Events", "Mining", "Farming", "Developer")

    override fun getCategoryComparator(): Comparator<in Category> {
        return compareBy { categories.indexOf(it.name) }
    }
}