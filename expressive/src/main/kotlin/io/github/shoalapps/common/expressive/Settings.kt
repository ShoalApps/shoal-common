@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package io.github.shoalapps.common.expressive

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.shoalapps.common.ui.LocalisedIcon as Icon
import io.github.shoalapps.common.ui.LocalisedText as Text

/*
 * The settings tiles every Shoal app uses: titled groups of Material 3 Expressive segmented list
 * items, rounded tiles with small gaps, the outer corners rounder than the inner ones. Text is
 * passed in English and localised from the app's own catalogue.
 */

/** Collects a group's rows; each is given its place's shape. */
class SettingsGroupBuilder {
    internal val rows = mutableListOf<@Composable (ListItemShapes) -> Unit>()
    fun row(content: @Composable (ListItemShapes) -> Unit) { rows += content }
}

/** A group's title, in the primary colour above its tiles. */
@Composable
fun SettingsSection(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.titleSmallEmphasized,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 32.dp, top = 24.dp, bottom = 8.dp),
    )
}

/** A titled group of tiles. Nothing is shown when no rows were added. */
fun LazyListScope.settingsGroup(title: String, showTitle: Boolean = true, build: SettingsGroupBuilder.() -> Unit) {
    val rows = SettingsGroupBuilder().apply(build).rows
    if (rows.isEmpty()) return
    if (showTitle) item(key = "t-$title") { SettingsSection(title) }
    rows.forEachIndexed { i, row ->
        item(key = "$title-$i") {
            Box(Modifier.padding(horizontal = 16.dp, vertical = ListItemDefaults.SegmentedGap / 2)) {
                row(ListItemDefaults.segmentedShapes(i, rows.size))
            }
        }
    }
}

/** Every tile sits on the same tonal container, whether its switch is on or off. */
@Composable
fun settingsTileColors() = ListItemDefaults.segmentedColors(
    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    selectedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    selectedContentColor = MaterialTheme.colorScheme.onSurface,
    selectedSupportingContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
)

@Composable
fun SettingsToggle(
    title: String,
    body: String?,
    checked: Boolean,
    shapes: ListItemShapes,
    leading: (@Composable () -> Unit)? = null,
    onChange: (Boolean) -> Unit,
) {
    SegmentedListItem(
        checked = checked,
        onCheckedChange = onChange,
        shapes = shapes,
        leadingContent = leading,
        supportingContent = body?.let { { Text(it) } },
        trailingContent = { Switch(checked = checked, onCheckedChange = null) },
        colors = settingsTileColors(),
    ) { Text(title) }
}

@Composable
fun SettingsLink(title: String, body: String?, shapes: ListItemShapes, trailingArrow: Boolean = true, onClick: () -> Unit) {
    SegmentedListItem(
        onClick = onClick,
        shapes = shapes,
        supportingContent = body?.let { { Text(it) } },
        trailingContent = if (trailingArrow) ({ Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, null) }) else null,
        colors = settingsTileColors(),
    ) { Text(title) }
}

/** Free-form content in a group tile. */
@Composable
fun SettingsTile(shapes: ListItemShapes, content: @Composable ColumnScope.() -> Unit) {
    Surface(shape = shapes.shape, color = MaterialTheme.colorScheme.surfaceContainerHigh, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), content = content)
    }
}

/** A choice between a few options, as a segmented button row in a tile. */
@Composable
fun SettingsChoices(title: String, body: String?, labels: List<String>, selected: Int, shapes: ListItemShapes, onSelect: (Int) -> Unit) {
    SettingsTile(shapes) {
        Text(title, style = MaterialTheme.typography.bodyLarge)
        body?.let { Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth().padding(top = 8.dp)) {
            labels.forEachIndexed { i, l ->
                SegmentedButton(selected = selected == i, onClick = { onSelect(i) }, shape = SegmentedButtonDefaults.itemShape(i, labels.size)) { Text(l, maxLines = 1) }
            }
        }
    }
}
