package at.raven.ravenAddons.features.skyblock

enum class ItemRarity(
    val colorCode: Char,
) {
    COMMON('f'),
    UNCOMMON('a'),
    RARE('9'),
    EPIC('5'),
    LEGENDARY('6'),
    MYTHIC('d')
    ;
    companion object {

        val runeMap: Map<String, ItemRarity> by lazy {
            // TO-DO: Move to repo
            mapOf(
                "◆ Pestilence Rune I" to RARE,
                "◆ Spirit Rune I" to RARE,
                "◆ Bite Rune I" to EPIC,
                "◆ End Rune I" to EPIC,
                "◆ Soultwist Rune I" to EPIC,
                "◆ Snake Rune I" to LEGENDARY,
                "◆ Couture Rune I" to LEGENDARY,
                "◆ Darkness Within Rune I" to LEGENDARY,
                "◆ Endersnake Rune I" to LEGENDARY,
                "◆ Enchant Rune I" to LEGENDARY,
                "◆ Lavatears Rune I" to LEGENDARY,
                "◆ Fiery Burst Rune I" to LEGENDARY
            )
        }

        fun getFromChatColor(colorCode: Char) = entries.firstOrNull { it.colorCode == colorCode}
    }
}
