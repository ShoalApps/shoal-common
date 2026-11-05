package io.github.shoalapps.common.ui

import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

/** Pure catalogue resolution; the host supplies bundled data, cached by language tag. */
internal class TranslationCatalogue {
    private val catalogues = ConcurrentHashMap<String, Map<String, String>>()
    private val resolved = ConcurrentHashMap<String, Map<String, String>>()
    private val patterns = ConcurrentHashMap<String, List<Pair<Regex, String>>>()

    fun translate(text: String, tag: String, load: (String) -> Map<String, String>): String {
        val locale = Locale.forLanguageTag(tag)
        val language = when (locale.language) { "fil" -> "tl"; "no" -> "nb"; else -> locale.language }
        if (language == "en" || text.isBlank()) return text
        val candidates = listOf(tag, if (locale.country.isNotBlank()) "$language-${locale.country}" else "",
            if (locale.script == "Hant") "zh-TW" else "", language).filter { it.isNotBlank() }
        val table = resolved.getOrPut(tag) { linkedMapOf<String, String>().apply {
            candidates.asReversed().forEach { putAll(catalogues.getOrPut(it) { load(it) }) }
        } }
        table[text]?.let { return it }
        val templates = patterns.getOrPut(tag) {
            table.filterKeys { it.contains(Regex("\\{[0-9]+\\}")) }.map { (source, target) ->
                val parts = source.split(Regex("\\{[0-9]+\\}"))
                Regex("^" + parts.joinToString("(.+?)") { Regex.escape(it) } + "$") to target
            }
        }
        for ((pattern, target) in templates) {
            val match = pattern.matchEntire(text) ?: continue
            var result = target
            match.groupValues.drop(1).forEachIndexed { index, value -> result = result.replace("{$index}", value) }
            return result
        }
        return text
    }
}
