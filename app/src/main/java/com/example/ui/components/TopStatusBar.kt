package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CloudSyncStatus
import com.example.model.AntiCheatReport
import com.example.model.FighterStats
import com.example.ui.theme.CyberPalette

@Composable
fun TopStatusBar(
    stats: FighterStats,
    cloudSyncStatus: CloudSyncStatus,
    antiCheatReport: AntiCheatReport,
    palette: CyberPalette,
    onSecurityClick: () -> Unit,
    onSyncClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(palette.surface.copy(alpha = 0.95f))
            .border(width = 1.dp, color = palette.borderGlow.copy(alpha = 0.3f))
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("top_status_bar")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Player Level & Identity
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(palette.primary.copy(alpha = 0.2f))
                        .border(1.dp, palette.primary, RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "L${stats.level}",
                        color = palette.primary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = "CYBER OPERATIVE",
                        color = palette.onBackground,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "XP: ${stats.currentXp}/${stats.xpToNextLevel}",
                        color = palette.onSurface.copy(alpha = 0.6f),
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Currency Indicators (Credits & Nanites)
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Credits
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(text = "₡", color = palette.energyColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${stats.credits}",
                        color = palette.onBackground,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Nanites
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(text = "◆", color = palette.primary, fontSize = 10.sp)
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${stats.nanites}",
                        color = palette.primary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Cloud Sync & Anti-Cheat Quick Status Badges
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Cloud Status
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (cloudSyncStatus.isOnline) palette.primary.copy(alpha = 0.15f) else Color.Red.copy(alpha = 0.2f))
                        .border(1.dp, if (cloudSyncStatus.isOnline) palette.primary.copy(alpha = 0.4f) else Color.Red.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                        .clickable { onSyncClick() }
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                        .testTag("cloud_status_badge")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (cloudSyncStatus.isOnline) Icons.Default.CloudDone else Icons.Default.CloudOff,
                            contentDescription = "Cloud Status",
                            tint = if (cloudSyncStatus.isOnline) palette.primary else Color.Red,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = if (cloudSyncStatus.isOnline) "${cloudSyncStatus.cloudLatencyMs}ms" else "OFFLINE",
                            color = if (cloudSyncStatus.isOnline) palette.primary else Color.Red,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Anti-Cheat Shield Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (antiCheatReport.isSecure) palette.hpColor.copy(alpha = 0.15f) else Color.Red.copy(alpha = 0.2f))
                        .border(1.dp, if (antiCheatReport.isSecure) palette.hpColor.copy(alpha = 0.4f) else Color.Red.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                        .clickable { onSecurityClick() }
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                        .testTag("anti_cheat_badge")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Anti-Cheat Guard",
                            tint = if (antiCheatReport.isSecure) palette.hpColor else Color.Red,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "SECURE",
                            color = if (antiCheatReport.isSecure) palette.hpColor else Color.Red,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}
