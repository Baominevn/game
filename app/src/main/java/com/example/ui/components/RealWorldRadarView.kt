package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RealWorldSensorData
import com.example.ui.theme.CyberPalette
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RealWorldRadarView(
    sensorData: RealWorldSensorData,
    palette: CyberPalette,
    onScanSectorClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar_sweep")
    val radarAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "angle"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background)
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
            .testTag("real_world_radar_view")
    ) {
        // TOP HEADER
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
                        text = "REAL-WORLD SENSORY MATRIX",
                        color = palette.onBackground,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Hardware Telemetry Linked: Light, Motion, Thermals, Geo-Grid",
                        color = palette.primary,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Icon(
                    imageVector = Icons.Default.Sensors,
                    contentDescription = null,
                    tint = palette.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // ROTATING RADAR SWEEP CANVAS
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(palette.surface)
                .border(1.dp, palette.borderGlow.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                val maxRadius = (size.height / 2) * 0.85f

                // Radar rings
                drawCircle(color = palette.primary.copy(alpha = 0.15f), radius = maxRadius, center = center, style = Stroke(1.5f))
                drawCircle(color = palette.primary.copy(alpha = 0.15f), radius = maxRadius * 0.66f, center = center, style = Stroke(1f))
                drawCircle(color = palette.primary.copy(alpha = 0.15f), radius = maxRadius * 0.33f, center = center, style = Stroke(1f))

                // Crosshairs
                drawLine(color = palette.primary.copy(alpha = 0.15f), start = Offset(center.x - maxRadius, center.y), end = Offset(center.x + maxRadius, center.y))
                drawLine(color = palette.primary.copy(alpha = 0.15f), start = Offset(center.x, center.y - maxRadius), end = Offset(center.x, center.y + maxRadius))

                // Rotating radar beam
                val rad = Math.toRadians(radarAngle.toDouble())
                val beamEnd = Offset(
                    (center.x + maxRadius * cos(rad)).toFloat(),
                    (center.y + maxRadius * sin(rad)).toFloat()
                )
                drawLine(
                    color = palette.primary.copy(alpha = 0.85f),
                    start = center,
                    end = beamEnd,
                    strokeWidth = 2.5f
                )

                // Blips for detected geo-nodes
                drawCircle(color = palette.energyColor, radius = 4f, center = Offset(center.x + maxRadius * 0.45f, center.y - maxRadius * 0.2f))
                drawCircle(color = palette.hpColor, radius = 5f, center = Offset(center.x - maxRadius * 0.5f, center.y + maxRadius * 0.35f))
                drawCircle(color = palette.primary, radius = 6f, center = center)
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "CURRENT GRID: ${sensorData.geoSector}",
                    color = palette.onBackground,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "GPS: Lat ${String.format("%.4f", sensorData.latitude)}, Lng ${String.format("%.4f", sensorData.longitude)}",
                    color = palette.onSurface.copy(alpha = 0.6f),
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // SCAN SECTOR BUTTON
        Button(
            onClick = onScanSectorClick,
            colors = ButtonDefaults.buttonColors(containerColor = palette.primary),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("scan_sector_button")
        ) {
            Icon(imageVector = Icons.Default.GpsFixed, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "SCAN LOCAL SECTOR FOR QUANTUM LOOT", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Black)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // SENSOR READOUT CARDS
        // 1. Ambient Light
        SensorReadoutCard(
            title = "AMBIENT PHOTON SENSOR",
            valueText = "${sensorData.ambientLux.toInt()} LUX",
            description = sensorData.lightBonusDescription,
            statusBadge = if (sensorData.ambientLux < 25f) "STEALTH BUFF" else if (sensorData.ambientLux > 400f) "SOLAR CHARGE" else "BALANCED",
            icon = Icons.Default.Bolt,
            palette = palette
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 2. Kinetic Accelerometer
        SensorReadoutCard(
            title = "KINETIC KINEMATIC HARVESTER",
            valueText = "${sensorData.kineticEnergyCharge.toInt()}% CHARGED",
            description = "Physical device movement and steps convert real-world kinetic motion into combat power. Shake phone to trigger emergency parry!",
            statusBadge = if (sensorData.isPhysicalMoving) "HARVESTING..." else "IDLE",
            icon = Icons.Default.FlashOn,
            palette = palette
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 3. Device Battery & Thermals
        SensorReadoutCard(
            title = "THERMAL & POWER GRID",
            valueText = "${sensorData.batteryLevel}% REMAINING",
            description = if (sensorData.isCharging) "Power grid connected! Unlimited coolant active (+15% Defense bonus)." else "Discharging from internal fuel cell.",
            statusBadge = if (sensorData.isCharging) "CHARGING" else "BATTERY",
            icon = Icons.Default.BatteryChargingFull,
            palette = palette
        )
    }
}

@Composable
fun SensorReadoutCard(
    title: String,
    valueText: String,
    description: String,
    statusBadge: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    palette: CyberPalette
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(palette.surface)
            .border(1.dp, palette.primary.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = icon, contentDescription = null, tint = palette.primary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = title, color = palette.onBackground, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Box(
                    modifier = Modifier
                        .background(palette.primary.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = statusBadge,
                        color = palette.primary,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = valueText,
                color = palette.energyColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )

            Text(
                text = description,
                color = palette.onSurface.copy(alpha = 0.7f),
                fontSize = 10.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
