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
        fun getFromChatColor(colorCode: Char) = entries.firstOrNull { it.colorCode == colorCode}
    }
}