package at.raven.ravenAddons.config

import gg.essential.vigilance.data.Category
import gg.essential.vigilance.data.SortingBehavior

class ConfigSorting: SortingBehavior() {

    private val categories: List<String> = listOf("General", "SkyBlock", "Mining", "Developer")

    override fun getCategoryComparator(): Comparator<in Category> {
        return compareBy { categories.indexOf(it.name) }
    }
}