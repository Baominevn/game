package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import com.example.model.AntiCheatReport
import com.example.ui.theme.CyberPalette
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AntiCheatSecurityView(
    report: AntiCheatReport,
    palette: CyberPalette,
    onRunScan: () -> Unit,
    modifier: Modifier = Modifier
) {
    val timeStr = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(report.lastValidationTime))

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background)
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
            .testTag("anti_cheat_security_view")
    ) {
        // TOP SECURITY STATUS BANNER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(palette.surface)
                .border(
                    1.dp,
                    if (report.isSecure) palette.hpColor.copy(alpha = 0.6f) else palette.accentCrit,
                    RoundedCornerShape(10.dp)
                )
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = if (report.isSecure) palette.hpColor else palette.accentCrit,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "ANTI-CHEAT INTEGRITY ENGINE",
                            color = palette.onBackground,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = report.threatLevel,
                        color = if (report.isSecure) palette.hpColor else palette.accentCrit,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .background(if (report.isSecure) palette.hpColor.copy(alpha = 0.2f) else palette.accentCrit.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (report.isSecure) "100% SECURE" else "ALERT",
                        color = if (report.isSecure) palette.hpColor else palette.accentCrit,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // RUN SCAN BUTTON
        Button(
            onClick = onRunScan,
            colors = ButtonDefaults.buttonColors(containerColor = palette.surfaceVariant),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, palette.primary, RoundedCornerShape(8.dp))
                .testTag("run_anti_cheat_scan_button")
        ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = palette.primary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "EXECUTE FULL MEMORY & CLOCK INTEGRITY AUDIT", color = palette.primary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // INTEGRITY VECTORS
        Text(
            text = "ACTIVE DEFENSE MONITORS",
            color = palette.onSurface.copy(alpha = 0.6f),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // 1. Memory Checksum
        SecurityVectorTile(
            title = "State Checksum & Memory Signature",
            statusText = if (report.memoryIntegrityValid) "VERIFIED (HMAC-SHA256)" else "MEMORY TAMPER DETECTED",
            isValid = report.memoryIntegrityValid,
            token = "TOKEN: ${report.signatureToken}",
            icon = Icons.Default.Fingerprint,
            palette = palette
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 2. Speed-Hack & Timing Integrity
        SecurityVectorTile(
            title = "Clock Drift & Speed-Hack Shield",
            statusText = if (report.clockTimingValid) "SYNCHRONIZED (Nano Delta OK)" else "SPEED-HACK GEAR DETECTED",
            isValid = report.clockTimingValid,
            token = "TICK OFFSET: < 2ms",
            icon = Icons.Default.Speed,
            palette = palette
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 3. Debugger & Hook Isolation
        SecurityVectorTile(
            title = "Debugger & Hook Isolation Guard",
            statusText = if (report.debuggerIsolated) "ISOLATED (No Hooks Found)" else "DEBUGGER ATTACHED",
            isValid = report.debuggerIsolated,
            token = "PID SANDBOX: ENFORCED",
            icon = Icons.Default.Lock,
            palette = palette
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 4. Cloud Payload Cryptography
        SecurityVectorTile(
            title = "Cloud Save Payload Cryptography",
            statusText = "END-TO-END ENCRYPTED (AES-GCM/HMAC)",
            isValid = true,
            token = "AUDIT TIMESTAMP: $timeStr",
            icon = Icons.Default.VpnKey,
            palette = palette
        )
    }
}

@Composable
fun SecurityVectorTile(
    title: String,
    statusText: String,
    isValid: Boolean,
    token: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    palette: CyberPalette
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(palette.surface)
            .border(
                1.dp,
                if (isValid) palette.hpColor.copy(alpha = 0.25f) else palette.accentCrit.copy(alpha = 0.6f),
                RoundedCornerShape(8.dp)
            )
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isValid) palette.hpColor else palette.accentCrit,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = title, color = palette.onBackground, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = statusText,
                        color = if (isValid) palette.hpColor else palette.accentCrit,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = token,
                        color = palette.onSurface.copy(alpha = 0.5f),
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = if (isValid) palette.hpColor else palette.accentCrit,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
