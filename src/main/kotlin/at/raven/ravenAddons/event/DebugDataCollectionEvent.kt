package at.raven.ravenAddons.event

import at.raven.ravenAddons.utils.StringUtils.equalsIgnoreColor
import net.minecraftforge.fml.common.eventhandler.Event

class DebugDataCollectionEvent(
    private val list: MutableList<String>,
    private val search: String,
) : Event() {
    var empty = true
    private var currentTitle: String? = null
    private var irrelevant = false

    fun title(title: String) {
        if (currentTitle != null) error("title already set: '$currentTitle'")

        currentTitle = title
    }

    fun addIrrelevant(builder: MutableList<String>.() -> Unit) = addIrrelevant(buildList(builder))

    fun addIrrelevant(text: String) = addIrrelevant(listOf(text))

    fun addIrrelevant(text: List<String>) {
        irrelevant = true
        addData(text)
    }

    fun addData(builder: MutableList<String>.() -> Unit) = addData(buildList(builder))

    fun addData(text: String) = addData(listOf(text))

    fun addData(text: List<String>) {
        if (currentTitle == null) error("Title not set")
        writeData(text)
        currentTitle = null
        irrelevant = false
    }

    private fun writeData(text: List<String>) {
        if (irrelevant && search.isEmpty()) return
        if (search.isNotEmpty()) {
            if (!search.equalsIgnoreColor("all")) {
                if (currentTitle?.contains(search, ignoreCase = true) == false) {
                    return
                }
            }
        }
        empty = false
        list.add("")
        list.add("== $currentTitle ==")
        for (line in text) {
            list.add(" $line")
        }
    }
}
