package io.github.shoalapps.common.ui

import org.json.JSONException
import org.junit.Assert.*
import org.junit.Test

class WhatsNewContentTest {
    @Test fun installedVersionFormatRetainsNameAndLongVersionCode() {
        assertEquals("0.1.42-dev (4294967297)", whatsNewVersion("0.1.42-dev", 4294967297L))
        assertEquals("null (1)", whatsNewVersion(null, 1L))
    }

    @Test fun firstLaunchAndVersionChangesAreUnseenButDismissedVersionIsSeen() {
        val version = whatsNewVersion("0.1.42", 42L)
        assertTrue(hasUnseenWhatsNew(null, version))
        assertFalse(hasUnseenWhatsNew(version, version))
        assertTrue(hasUnseenWhatsNew(version, whatsNewVersion("0.1.43", 43L)))
        assertTrue(hasUnseenWhatsNew(version, whatsNewVersion("0.1.42", 44L)))
    }

    @Test fun notesRetainOrderAndEmptyArraysDoNotInvokeFallback() {
        assertEquals(listOf("First" to "One", "Second" to "Two"), parseWhatsNewHighlights(
            """[{"title":"First","body":"One"},{"title":"Second","body":"Two"}]"""
        ))
        assertTrue(parseWhatsNewHighlights("[]").isEmpty())
    }

    @Test fun invalidOrIncompleteNotesUseTheWholeAppFallback() {
        val fallback = listOf("App title" to "App body")
        for (json in listOf("invalid", """[{"title":"Missing body"}]""",
            """[{"title":"Valid","body":"Valid"},{}]""")) {
            assertEquals(fallback, runCatching { parseWhatsNewHighlights(json) }.getOrDefault(fallback))
        }
    }

    @Test(expected = JSONException::class)
    fun malformedNotesThrowForHostToHandle() {
        parseWhatsNewHighlights("not JSON")
    }
}
