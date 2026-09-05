package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.model.CyberThemeMode
import com.example.model.PerformanceMode
import com.example.ui.theme.CyberPalette

@Composable
fun SettingsView(
    currentTheme: CyberThemeMode,
    currentPerfMode: PerformanceMode,
    cloudSyncStatus: CloudSyncStatus,
    palette: CyberPalette,
    isSoundEnabled: Boolean,
    onThemeChange: (CyberThemeMode) -> Unit,
    onPerfModeChange: (PerformanceMode) -> Unit,
    onSoundToggle: (Boolean) -> Unit,
    onManualSync: () -> Unit,
    onExportBackup: () -> Unit,
    isNightVisionAuto: Boolean = true,
    isThermalGuardEnabled: Boolean = true,
    isOledBlackEnabled: Boolean = false,
    onNightVisionToggle: (Boolean) -> Unit = {},
    onThermalGuardToggle: (Boolean) -> Unit = {},
    onOledBlackToggle: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var backupExportMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background)
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
            .testTag("settings_view")
    ) {
        // HEADER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(palette.surface)
                .border(1.dp, palette.primary.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "SYSTEM & ENGINE CONFIG",
                        color = palette.onBackground,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Battery Optimization, Themes, Latency-Free Audio & Cloud Backup",
                        color = palette.primary,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // THEME SELECTION
        Text(
            text = "CYBERNETIC THEME PROTOCOL",
            color = palette.onSurface.copy(alpha = 0.6f),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            CyberThemeMode.values().forEach { mode ->
                val isSelected = (mode == currentTheme)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) palette.primary else palette.surface)
                        .border(1.dp, if (isSelected) palette.primary else palette.borderGlow.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                        .clickable { onThemeChange(mode) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = mode.title.split(" ").first().uppercase(),
                        color = if (isSelected) Color.Black else palette.onBackground,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // PERFORMANCE & BATTERY SAVER MODE
        Text(
            text = "BATTERY & PERFORMANCE ENGINE",
            color = palette.onSurface.copy(alpha = 0.6f),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        PerformanceMode.values().forEach { perf ->
            val isSelected = (perf == currentPerfMode)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(palette.surface)
                    .border(
                        1.dp,
                        if (isSelected) palette.primary else palette.borderGlow.copy(alpha = 0.2f),
                        RoundedCornerShape(8.dp)
                    )
                    .clickable { onPerfModeChange(perf) }
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = perf.title, color = palette.onBackground, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = perf.description, color = palette.onSurface.copy(alpha = 0.6f), fontSize = 10.sp)
                    }
                    if (isSelected) {
                        Text(
                            text = "ACTIVE",
                            color = palette.primary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // AUDIO SYNTHESIZER TOGGLE
        Text(
            text = "AUDIO SYNTHESIZER",
            color = palette.onSurface.copy(alpha = 0.6f),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(palette.surface)
                .border(1.dp, palette.borderGlow.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = palette.primary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = "Synthesized Sound FX", color = palette.onBackground, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Real-time PCM synthesizer with zero audio latency", color = palette.onSurface.copy(alpha = 0.6f), fontSize = 10.sp)
                    }
                }

                Switch(
                    checked = isSoundEnabled,
                    onCheckedChange = { onSoundToggle(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = palette.primary,
                        checkedTrackColor = palette.primary.copy(alpha = 0.3f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // HARDWARE & THERMAL GUARD
        Text(
            text = "BẢO VỆ PHẦN CỨNG & TỐI ƯU PIN (THERMAL & BATTERY GUARD)",
            color = palette.onSurface.copy(alpha = 0.6f),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(palette.surface)
                .border(1.dp, palette.primary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Tự Động Night Vision Protocol", color = palette.onBackground, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("Đo quang thông ánh sáng môi trường để kích hoạt chế độ đêm chống mỏi mắt", color = palette.onSurface.copy(alpha = 0.6f), fontSize = 10.sp)
                    }
                    Switch(
                        checked = isNightVisionAuto,
                        onCheckedChange = onNightVisionToggle,
                        colors = SwitchDefaults.colors(checkedThumbColor = palette.primary, checkedTrackColor = palette.primary.copy(alpha = 0.3f))
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Tối Ưu Nhiệt Độ Chip (Thermal Guard)", color = palette.onBackground, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("Tự động cân bằng GPU/CPU shader khi máy ấm lên để ngăn quá nhiệt và giật lag", color = palette.onSurface.copy(alpha = 0.6f), fontSize = 10.sp)
                    }
                    Switch(
                        checked = isThermalGuardEnabled,
                        onCheckedChange = onThermalGuardToggle,
                        colors = SwitchDefaults.colors(checkedThumbColor = palette.primary, checkedTrackColor = palette.primary.copy(alpha = 0.3f))
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("OLED True Black Mode", color = palette.onBackground, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("Tắt hoàn toàn điểm ảnh OLED nền (#000000) giảm tiêu thụ điện năng tới 45%", color = palette.onSurface.copy(alpha = 0.6f), fontSize = 10.sp)
                    }
                    Switch(
                        checked = isOledBlackEnabled,
                        onCheckedChange = onOledBlackToggle,
                        colors = SwitchDefaults.colors(checkedThumbColor = palette.primary, checkedTrackColor = palette.primary.copy(alpha = 0.3f))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // CLOUD SYNCHRONIZATION & BACKUP
        Text(
            text = "CLOUD SYNC & SECURE SNAPSHOT",
            color = palette.onSurface.copy(alpha = 0.6f),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(palette.surface)
                .border(1.dp, palette.primary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Column {
                Text(
                    text = cloudSyncStatus.syncMessage,
                    color = if (cloudSyncStatus.isOnline) palette.primary else Color.Red,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Cluster: ${cloudSyncStatus.cloudServerRegion} | Version: v${cloudSyncStatus.cloudBackupVersion}",
                    color = palette.onSurface.copy(alpha = 0.7f),
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(vertical = 3.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onManualSync,
                        colors = ButtonDefaults.buttonColors(containerColor = palette.primary),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("manual_cloud_sync_button")
                    ) {
                        Text(text = "SYNC NOW", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            onExportBackup()
                            backupExportMessage = "Encrypted Cloud Snapshot generated and verified."
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = palette.surfaceVariant),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, palette.primary, RoundedCornerShape(6.dp))
                            .testTag("export_backup_button")
                    ) {
                        Text(text = "EXPORT SNAPSHOT", color = palette.primary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                backupExportMessage?.let {
                    Text(
                        text = it,
                        color = palette.hpColor,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        }
    }
}
