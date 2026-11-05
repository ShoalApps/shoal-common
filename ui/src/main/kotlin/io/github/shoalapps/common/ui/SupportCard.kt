package io.github.shoalapps.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/** Supporting Shoal: a bright card in the app's own colours. The app supplies text, localisation and the heart icon. */
@Composable
fun SupportCard(
    title: String,
    description: String,
    donateLabel: String,
    onDonateLabel: String,
    heartIcon: ImageVector,
    heartContentDescription: String? = null,
    onDonate: () -> Unit,
) {
    val c = MaterialTheme.colorScheme
    Surface(
        shape = RoundedCornerShape(28.dp), color = c.primary,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Row(
            Modifier.clickable(onClickLabel = onDonateLabel, onClick = onDonate)
                .background(Brush.linearGradient(listOf(c.primary, c.tertiary))).padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(heartIcon, heartContentDescription, Modifier.size(36.dp), tint = c.onPrimary)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = c.onPrimary)
                Text(description, style = MaterialTheme.typography.bodyMedium, color = c.onPrimary.copy(alpha = .9f))
                Button(
                    onClick = onDonate, modifier = Modifier.padding(top = 12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = c.onPrimary, contentColor = c.primary),
                ) { Text(donateLabel, fontWeight = FontWeight.Bold) }
            }
        }
    }
}
