package io.github.shoalapps.common.ui

import org.json.JSONArray

internal fun whatsNewVersion(name: String?, code: Long): String = "$name ($code)"

internal fun hasUnseenWhatsNew(seen: String?, version: String): Boolean = seen != version

/** Invalid notes throw so Host can use the app's fallback; an empty array stays empty. */
internal fun parseWhatsNewHighlights(json: String): List<Pair<String, String>> {
    val entries = JSONArray(json)
    return (0 until entries.length()).map {
        entries.getJSONObject(it).let { note -> note.getString("title") to note.getString("body") }
    }
}
