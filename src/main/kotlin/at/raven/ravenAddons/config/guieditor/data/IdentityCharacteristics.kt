package at.raven.ravenAddons.config.guieditor.data

class IdentityCharacteristics<T>(
    val value: T,
) {
    override fun equals(other: Any?): Boolean {
        if (other !is IdentityCharacteristics<*>) return false
        return this.value === other.value
    }

    override fun hashCode(): Int = System.identityHashCode(value)
}
