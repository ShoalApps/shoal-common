package io.github.shoalapps.common.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import io.github.shoalapps.common.ui.LocalisedText as Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

/** Release notes ship with the APK, so they work offline and match the installed version. */
object WhatsNew {
    private var requested by mutableStateOf(false)
    fun open() { requested = true }

    @Composable
    fun Host(
        title: String,
        fallbackTitle: String,
        fallbackBody: String,
        communityText: String,
        confirmText: String,
    ) {
        val context = LocalContext.current
        val prefs = remember { context.getSharedPreferences("whats-new", android.content.Context.MODE_PRIVATE) }
        val version = remember {
            @Suppress("DEPRECATION")
            context.packageManager.getPackageInfo(context.packageName, 0).let { whatsNewVersion(it.versionName, androidx.core.content.pm.PackageInfoCompat.getLongVersionCode(it)) }
        }
        var pending by rememberSaveable(version) { mutableStateOf(hasUnseenWhatsNew(prefs.getString("seen", null), version)) }
        val highlights = remember(version) {
            runCatching {
                parseWhatsNewHighlights(context.assets.open("whats-new.json").bufferedReader().use { it.readText() })
            }.getOrDefault(listOf(fallbackTitle to fallbackBody))
        }
        if (!pending && !requested) return
        fun dismiss() {
            prefs.edit().putString("seen", version).apply()
            pending = false
            requested = false
        }
        AlertDialog(
            onDismissRequest = ::dismiss,
            title = { Column { Text(title); Text(version, style = MaterialTheme.typography.labelLarge) } },
            text = {
                Column(Modifier.fillMaxWidth().heightIn(max = 440.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    highlights.forEach { (title, body) ->
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                            Text(body, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    Text(communityText, style = MaterialTheme.typography.labelLarge)
                }
            },
            confirmButton = { TextButton(onClick = ::dismiss) { Text(confirmText) } },
        )
    }
}
