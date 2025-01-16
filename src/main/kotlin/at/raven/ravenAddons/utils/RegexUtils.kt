package at.raven.ravenAddons.utils

import java.util.regex.Matcher
import java.util.regex.Pattern

object RegexUtils {
    fun Pattern.findAll(input: String): List<String> {
        val matcher = matcher(input)

        return buildList {
            while (matcher.find()) {
                add(matcher.group())
            }
        }
    }

    inline fun <T> Pattern.matchMatcher(
        text: String,
        consumer: Matcher.() -> T,
    ) = matcher(text).let { if (it.matches()) consumer(it) else null }

    inline fun <T> Pattern.findMatcher(
        text: String,
        consumer: Matcher.() -> T,
    ) = matcher(text).let { if (it.find()) consumer(it) else null }

    fun Pattern.matches(text: String) = this.toRegex().matches(text)

    fun Pattern.find(text: String) = this.matcher(text).find()
}
