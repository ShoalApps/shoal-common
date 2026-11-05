package io.github.shoalapps.common.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class TranslationCatalogueTest {
    @Test fun englishAndBlankTextDoNotLoadCatalogues() {
        val catalogue = TranslationCatalogue()
        val load: (String) -> Map<String, String> = { error("Unexpected catalogue load: $it") }
        assertEquals("Hello", catalogue.translate("Hello", "en-GB", load))
        assertEquals("  ", catalogue.translate("  ", "fr", load))
    }

    @Test fun exactTagOverridesRegionAndLanguageWhileMissingPhrasesFallBack() {
        val catalogue = TranslationCatalogue()
        val tables = mapOf(
            "fr" to mapOf("Hello" to "Bonjour", "Close" to "Fermer"),
            "fr-CA" to mapOf("Hello" to "Salut"),
            "fr-Latn-CA" to mapOf("Hello" to "Allô"),
        )
        val load = { tag: String -> tables[tag].orEmpty() }
        assertEquals("Allô", catalogue.translate("Hello", "fr-Latn-CA", load))
        assertEquals("Fermer", catalogue.translate("Close", "fr-Latn-CA", load))
        assertEquals("Unknown", catalogue.translate("Unknown", "fr-Latn-CA", load))
    }

    @Test fun traditionalChineseAndLanguageAliasesUseBundledFallbacks() {
        val tables = mapOf(
            "zh" to mapOf("Hello" to "你好"),
            "zh-TW" to mapOf("Hello" to "您好"),
            "tl" to mapOf("Hello" to "Kumusta"),
            "nb" to mapOf("Hello" to "Hei"),
        )
        val catalogue = TranslationCatalogue()
        val load = { tag: String -> tables[tag].orEmpty() }
        assertEquals("您好", catalogue.translate("Hello", "zh-Hant", load))
        assertEquals("Kumusta", catalogue.translate("Hello", "fil", load))
        assertEquals("Hei", catalogue.translate("Hello", "no", load))
    }

    @Test fun templatesEscapeRegexAndPreserveReorderedValuesAndExactMatches() {
        val catalogue = TranslationCatalogue()
        val load = { _: String -> mapOf(
            "Saved {0} ({1})." to "({1}) : {0}",
            "Saved special (item)." to "Exact",
        ) }
        assertEquals("(3) : Alice", catalogue.translate("Saved Alice (3).", "fr", load))
        assertEquals("Exact", catalogue.translate("Saved special (item).", "fr", load))
        assertEquals("Saved Alice (3)!", catalogue.translate("Saved Alice (3)!", "fr", load))
    }

    @Test fun catalogueAndResolvedTablesAreCachedByTag() {
        val loaded = mutableListOf<String>()
        val catalogue = TranslationCatalogue()
        val load = { tag: String -> loaded.add(tag); mapOf("Hello" to "Bonjour") }
        catalogue.translate("Hello", "fr-CA", load)
        catalogue.translate("Missing", "fr-CA", load)
        catalogue.translate("Hello", "fr", load)
        assertEquals(listOf("fr", "fr-CA"), loaded)
        assertFalse(loaded.contains("en"))
    }
}
