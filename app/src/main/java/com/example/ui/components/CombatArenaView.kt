package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.combat.ActionEffectType
import com.example.model.CombatAction
import com.example.model.CombatLog
import com.example.model.EnemyFighter
import com.example.model.FighterStats
import com.example.model.LogType
import com.example.model.MatchTier
import com.example.model.PostMatchReport
import com.example.model.RealWorldSensorData
import com.example.ui.theme.CyberPalette
import com.example.viewmodel.DamageIndicator
import kotlin.math.sin

@Composable
fun CombatArenaView(
    stats: FighterStats,
    enemy: EnemyFighter,
    stage: Int,
    sensorData: RealWorldSensorData,
    combatLogs: List<CombatLog>,
    activeEffect: ActionEffectType,
    damageIndicators: List<DamageIndicator>,
    screenShakeTrigger: Int,
    palette: CyberPalette,
    onActionClick: (CombatAction) -> Unit,
    matchTier: MatchTier = MatchTier.ROOKIE_BOT,
    onSelectTier: (MatchTier) -> Unit = {},
    postMatchReport: PostMatchReport? = null,
    onDismissPostMatchReport: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Screen shake animation
    var shakeOffset by remember { mutableStateOf(0f) }
    LaunchedEffect(screenShakeTrigger) {
        if (screenShakeTrigger > 0) {
            shakeOffset = 14f
            kotlinx.coroutines.delay(40)
            shakeOffset = -12f
            kotlinx.coroutines.delay(40)
            shakeOffset = 8f
            kotlinx.coroutines.delay(40)
            shakeOffset = 0f
        }
    }

    // Infinite breathing / laser scan line animation
    val infiniteTransition = rememberInfiniteTransition(label = "cyber_grid")
    val gridScanLine by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scan_line"
    )
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_glow"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .offset { IntOffset(x = shakeOffset.toInt(), y = 0) }
            .background(palette.background)
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .verticalScroll(rememberScrollState())
            .testTag("combat_arena_view")
    ) {
        // TIER PROGRESSION SELECTOR
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf(
                MatchTier.ROOKIE_BOT to "Bot Gà",
                MatchTier.INTERMEDIATE_BOT to "Bot Trung",
                MatchTier.ADVANCED_BOT to "Bot Mạnh",
                MatchTier.LIVE_PVP_1V1 to "PVP 1v1",
                MatchTier.LIVE_PVP_2V2 to "PVP 2v2"
            ).forEach { (tier, label) ->
                val isSelected = (matchTier == tier)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) palette.primary else palette.surface)
                        .border(1.dp, if (isSelected) palette.primary else palette.borderGlow.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                        .clickable { onSelectTier(tier) }
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) palette.background else palette.onSurface,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // ENEMY COMBATANT HUD
        EnemyHudCard(
            enemy = enemy,
            stage = stage,
            palette = palette,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // CENTER ANIMATED BATTLEFIELD CANVASES & DAMAGE POPUPS
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(190.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(palette.surface)
                .border(1.dp, palette.borderGlow.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
        ) {
            // Cyber Grid & Laser Scan Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                // Perspective grid
                val gridColor = palette.primary.copy(alpha = 0.12f)
                val vanishingY = height * 0.25f

                for (i in 0..10) {
                    val x = (width / 10) * i
                    drawLine(
                        color = gridColor,
                        start = Offset(x, height),
                        end = Offset(width * 0.5f + (x - width * 0.5f) * 0.3f, vanishingY),
                        strokeWidth = 1.2f
                    )
                }

                // Horizontal grid lines
                for (j in 1..6) {
                    val y = vanishingY + (height - vanishingY) * (j.toFloat() / 6f)
                    drawLine(
                        color = gridColor,
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1f
                    )
                }

                // Animated scan line
                val scanY = height * gridScanLine
                drawLine(
                    color = palette.primary.copy(alpha = 0.35f),
                    start = Offset(0f, scanY),
                    end = Offset(width, scanY),
                    strokeWidth = 2.5f
                )

                // Combatant Holographic silhouettes
                // Player Avatar (Left side)
                val playerCenterX = width * 0.24f
                val playerCenterY = height * 0.62f
                drawCircle(
                    color = palette.primary.copy(alpha = 0.18f * pulseGlow),
                    radius = 36f,
                    center = Offset(playerCenterX, playerCenterY)
                )
                drawCircle(
                    color = palette.primary,
                    radius = 28f,
                    center = Offset(playerCenterX, playerCenterY),
                    style = Stroke(width = 2.5f)
                )

                // Enemy Avatar (Right side)
                val enemyCenterX = width * 0.76f
                val enemyCenterY = height * 0.48f
                drawCircle(
                    color = palette.accentCrit.copy(alpha = 0.2f * pulseGlow),
                    radius = 42f,
                    center = Offset(enemyCenterX, enemyCenterY)
                )
                drawCircle(
                    color = palette.accentCrit,
                    radius = 34f,
                    center = Offset(enemyCenterX, enemyCenterY),
                    style = Stroke(width = 3f)
                )

                // Render Dynamic Combat Action VFX
                when (activeEffect) {
                    ActionEffectType.SLASH_ATTACK -> {
                        // Neon Blade Slash Arc
                        val slashPath = Path().apply {
                            moveTo(enemyCenterX - 50f, enemyCenterY - 45f)
                            quadraticTo(enemyCenterX, enemyCenterY, enemyCenterX + 55f, enemyCenterY + 45f)
                        }
                        drawPath(
                            path = slashPath,
                            color = palette.primary,
                            style = Stroke(width = 6f)
                        )
                        drawCircle(color = Color.White, radius = 18f, center = Offset(enemyCenterX, enemyCenterY))
                    }
                    ActionEffectType.PLASMA_EXPLOSION -> {
                        // Massive plasma blast ring
                        drawCircle(
                            color = palette.energyColor.copy(alpha = 0.8f),
                            radius = 65f,
                            center = Offset(enemyCenterX, enemyCenterY),
                            style = Stroke(width = 8f)
                        )
                        drawCircle(
                            color = palette.primary.copy(alpha = 0.5f),
                            radius = 45f,
                            center = Offset(enemyCenterX, enemyCenterY)
                        )
                    }
                    ActionEffectType.EMP_PULSE -> {
                        // Concentric expanding shockwaves
                        drawCircle(color = palette.shieldColor.copy(alpha = 0.8f), radius = 75f, center = Offset(enemyCenterX, enemyCenterY), style = Stroke(width = 4f))
                        drawCircle(color = Color.White.copy(alpha = 0.9f), radius = 45f, center = Offset(enemyCenterX, enemyCenterY), style = Stroke(width = 3f))
                    }
                    ActionEffectType.SHIELD_PARRY -> {
                        // Hexagonal barrier pulse over player
                        drawCircle(color = palette.shieldColor.copy(alpha = 0.8f), radius = 55f, center = Offset(playerCenterX, playerCenterY), style = Stroke(width = 5f))
                    }
                    ActionEffectType.QUANTUM_BEAM -> {
                        // Screen-wide laser beam from player to enemy
                        drawLine(
                            color = palette.primary,
                            start = Offset(playerCenterX, playerCenterY),
                            end = Offset(enemyCenterX, enemyCenterY),
                            strokeWidth = 14f
                        )
                        drawLine(
                            color = Color.White,
                            start = Offset(playerCenterX, playerCenterY),
                            end = Offset(enemyCenterX, enemyCenterY),
                            strokeWidth = 6f
                        )
                    }
                    ActionEffectType.HEAL_BURST -> {
                        drawCircle(color = palette.hpColor.copy(alpha = 0.6f), radius = 50f, center = Offset(playerCenterX, playerCenterY), style = Stroke(width = 4f))
                    }
                    ActionEffectType.NONE -> {}
                }
            }

            // Floating Combatant Labels on Battlefield
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "OPERATIVE [YOU]",
                    color = palette.primary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "TARGET: ${enemy.name}",
                    color = palette.accentCrit,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Dynamic Floating Damage Numbers
            damageIndicators.takeLast(4).forEach { indicator ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 40.dp),
                    contentAlignment = if (indicator.isPlayerTarget) Alignment.BottomStart else Alignment.TopEnd
                ) {
                    Text(
                        text = if (indicator.isCrit) "CRIT -${indicator.amount}!" else "-${indicator.amount}",
                        color = if (indicator.isCrit) palette.energyColor else if (indicator.isPlayerTarget) palette.accentCrit else Color.White,
                        fontSize = if (indicator.isCrit) 20.sp else 16.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // REAL-WORLD SENSORY BONUS CHIPS
        RealWorldBuffsRow(sensorData = sensorData, palette = palette)

        Spacer(modifier = Modifier.height(8.dp))

        // PLAYER VITALS HUD (HP, Shield, Energy)
        PlayerVitalsCard(stats = stats, palette = palette)

        Spacer(modifier = Modifier.height(10.dp))

        // COMBAT ACTION DECK
        CombatActionDeck(
            currentEnergy = stats.currentEnergy,
            palette = palette,
            onActionClick = onActionClick
        )

        Spacer(modifier = Modifier.height(8.dp))

        // RECENT COMBAT LOG TICKER
        CombatLogTicker(logs = combatLogs.take(5), palette = palette)

        // POST MATCH REPORT DIALOG
        if (postMatchReport != null) {
            AlertDialog(
                onDismissRequest = onDismissPostMatchReport,
                containerColor = palette.surface,
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PHÂN TÍCH HẬU CHIẾN",
                            color = palette.primary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(palette.primary.copy(alpha = 0.2f))
                                .border(1.dp, palette.primary, RoundedCornerShape(6.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "HẠNG ${postMatchReport.grade}",
                                color = palette.primary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                },
                text = {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Sát Thương Gây Ra:", color = palette.onSurface.copy(alpha = 0.7f), fontSize = 12.sp)
                            Text("${postMatchReport.totalDamageDealt} DMG", color = palette.primary, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Sát Thương Hứng Chịu:", color = palette.onSurface.copy(alpha = 0.7f), fontSize = 12.sp)
                            Text("${postMatchReport.totalDamageTaken} DMG", color = palette.danger, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Đòn Bạo Kích:", color = palette.onSurface.copy(alpha = 0.7f), fontSize = 12.sp)
                            Text("${postMatchReport.criticalHitsCount} Lần", color = palette.accent, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Độ Chính Xác Chiến Thuật:", color = palette.onSurface.copy(alpha = 0.7f), fontSize = 12.sp)
                            Text("${postMatchReport.tacticalAccuracyPercent}%", color = palette.primary, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Thay Đổi Xếp Hạng ELO:", color = palette.onSurface.copy(alpha = 0.7f), fontSize = 12.sp)
                            val eloSign = if (postMatchReport.eloChange >= 0) "+${postMatchReport.eloChange}" else "${postMatchReport.eloChange}"
                            Text(eloSign, color = if (postMatchReport.eloChange >= 0) palette.primary else palette.danger, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, fontSize = 13.sp)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(palette.primary.copy(alpha = 0.1f))
                                .border(1.dp, palette.primary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        ) {
                            Column {
                                Text("ĐÁNH GIÁ CỦA AI THẦN KINH:", color = palette.primary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(postMatchReport.aiEvaluationSummary, color = palette.onSurface, fontSize = 11.sp)
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = onDismissPostMatchReport,
                        colors = ButtonDefaults.buttonColors(containerColor = palette.primary)
                    ) {
                        Text("TIẾP TỤC CHIẾN ĐẤU", color = palette.background, fontWeight = FontWeight.Black)
                    }
                }
            )
        }
    }
}

@Composable
fun EnemyHudCard(
    enemy: EnemyFighter,
    stage: Int,
    palette: CyberPalette,
    modifier: Modifier = Modifier
) {
    val animatedHp by animateFloatAsState(
        targetValue = enemy.currentHp.toFloat() / enemy.maxHp.toFloat().coerceAtLeast(1f),
        animationSpec = tween(400),
        label = "enemy_hp"
    )
    val animatedShield by animateFloatAsState(
        targetValue = if (enemy.maxShield > 0) enemy.currentShield.toFloat() / enemy.maxShield.toFloat() else 0f,
        animationSpec = tween(400),
        label = "enemy_shield"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(palette.surface)
            .border(1.dp, palette.accentCrit.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = enemy.name,
                            color = palette.onBackground,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(palette.accentCrit.copy(alpha = 0.2f), RoundedCornerShape(3.dp))
                                .border(1.dp, palette.accentCrit.copy(alpha = 0.5f), RoundedCornerShape(3.dp))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "STAGE $stage",
                                color = palette.accentCrit,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        if (enemy.isBerserk) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .background(palette.danger.copy(alpha = 0.3f), RoundedCornerShape(3.dp))
                                    .border(1.dp, palette.danger, RoundedCornerShape(3.dp))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "BERSERK",
                                    color = palette.danger,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                    Text(
                        text = enemy.title,
                        color = palette.onSurface.copy(alpha = 0.7f),
                        fontSize = 10.sp
                    )
                }

                Text(
                    text = "${enemy.currentHp}/${enemy.maxHp} HP",
                    color = palette.accentCrit,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Enemy HP Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = animatedHp.coerceIn(0f, 1f))
                        .height(8.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(palette.accentCrit, Color(0xFFFF5252))
                            )
                        )
                )
            }

            // Enemy Shield Bar (if has shield)
            if (enemy.maxShield > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "SHIELD MATRIX", color = palette.shieldColor, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                    Text(text = "${enemy.currentShield}/${enemy.maxShield}", color = palette.shieldColor, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = animatedShield.coerceIn(0f, 1f))
                            .height(4.dp)
                            .background(palette.shieldColor)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Weakness & Strategy Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Weakness: ${enemy.weakness}",
                    color = palette.energyColor,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "ATK: ${enemy.attackPower} | DEF: ${enemy.defensePower}",
                    color = palette.onSurface.copy(alpha = 0.6f),
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun RealWorldBuffsRow(
    sensorData: RealWorldSensorData,
    palette: CyberPalette
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        item {
            // Light sensor bonus badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(palette.surfaceVariant.copy(alpha = 0.8f))
                    .border(1.dp, palette.borderGlow.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = palette.energyColor, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${sensorData.ambientLux.toInt()} LUX: ${sensorData.lightBonusDescription}",
                        color = palette.onBackground,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        item {
            // Kinetic motion charge badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(palette.surfaceVariant.copy(alpha = 0.8f))
                    .border(1.dp, palette.primary.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.FlashOn, contentDescription = null, tint = palette.primary, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "KINETIC: ${sensorData.kineticEnergyCharge.toInt()}% (+${(sensorData.kineticEnergyCharge * 0.4f).toInt()} ATK)",
                        color = palette.primary,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        item {
            // Battery Telemetry
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(palette.surfaceVariant.copy(alpha = 0.8f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "PWR: ${sensorData.batteryLevel}% ${if (sensorData.isCharging) "[GRID+15% DEF]" else ""}",
                    color = palette.hpColor,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun PlayerVitalsCard(
    stats: FighterStats,
    palette: CyberPalette
) {
    val animatedHp by animateFloatAsState(
        targetValue = stats.currentHp.toFloat() / stats.maxHp.toFloat().coerceAtLeast(1f),
        animationSpec = tween(300),
        label = "player_hp"
    )
    val animatedShield by animateFloatAsState(
        targetValue = if (stats.maxShield > 0) stats.currentShield.toFloat() / stats.maxShield.toFloat() else 0f,
        animationSpec = tween(300),
        label = "player_shield"
    )
    val animatedEnergy by animateFloatAsState(
        targetValue = stats.currentEnergy.toFloat() / stats.maxEnergy.toFloat().coerceAtLeast(1f),
        animationSpec = tween(300),
        label = "player_energy"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(palette.surface)
            .border(1.dp, palette.primary.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Column {
            // HP Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "HULL INTEGRITY", color = palette.hpColor, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                Text(text = "${stats.currentHp}/${stats.maxHp} HP", color = palette.hpColor, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            }
            Spacer(modifier = Modifier.height(3.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(9.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction = animatedHp.coerceIn(0f, 1f))
                        .height(9.dp)
                        .background(palette.hpColor)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Shield Bar & Energy Bar Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Shield
                Column(modifier = Modifier.weight(1f)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "NANO SHIELD", color = palette.shieldColor, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        Text(text = "${stats.currentShield}/${stats.maxShield}", color = palette.shieldColor, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color.Black.copy(alpha = 0.5f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = animatedShield.coerceIn(0f, 1f))
                                .height(6.dp)
                                .background(palette.shieldColor)
                        )
                    }
                }

                // Energy Capacitor
                Column(modifier = Modifier.weight(1f)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "ENERGY CORE", color = palette.energyColor, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        Text(text = "${stats.currentEnergy}/${stats.maxEnergy}", color = palette.energyColor, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color.Black.copy(alpha = 0.5f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = animatedEnergy.coerceIn(0f, 1f))
                                .height(6.dp)
                                .background(palette.energyColor)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CombatActionDeck(
    currentEnergy: Int,
    palette: CyberPalette,
    onActionClick: (CombatAction) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "COMBAT ACTION MATRIX",
            color = palette.onSurface.copy(alpha = 0.7f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // Row 1: Primary Attacks
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Cyber Slash
            ActionButtonTile(
                action = CombatAction.LIGHT_SLASH,
                isAvailable = true,
                badgeText = "+15 NRG",
                tileColor = palette.primary,
                palette = palette,
                onClick = { onActionClick(CombatAction.LIGHT_SLASH) },
                modifier = Modifier.weight(1f)
            )

            // Plasma Cannon
            ActionButtonTile(
                action = CombatAction.PLASMA_CANNON,
                isAvailable = currentEnergy >= CombatAction.PLASMA_CANNON.energyCost,
                badgeText = "25 NRG",
                tileColor = palette.energyColor,
                palette = palette,
                onClick = { onActionClick(CombatAction.PLASMA_CANNON) },
                modifier = Modifier.weight(1f)
            )

            // EMP Shockwave
            ActionButtonTile(
                action = CombatAction.EMP_DISRUPT,
                isAvailable = currentEnergy >= CombatAction.EMP_DISRUPT.energyCost,
                badgeText = "30 NRG",
                tileColor = palette.shieldColor,
                palette = palette,
                onClick = { onActionClick(CombatAction.EMP_DISRUPT) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Row 2: Tactical & Ultimate
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Nano Shield (Parry)
            ActionButtonTile(
                action = CombatAction.NANO_SHIELD,
                isAvailable = currentEnergy >= CombatAction.NANO_SHIELD.energyCost,
                badgeText = "15 NRG",
                tileColor = palette.shieldColor,
                palette = palette,
                onClick = { onActionClick(CombatAction.NANO_SHIELD) },
                modifier = Modifier.weight(1f)
            )

            // Quantum Overdrive
            ActionButtonTile(
                action = CombatAction.QUANTUM_OVERDRIVE,
                isAvailable = currentEnergy >= CombatAction.QUANTUM_OVERDRIVE.energyCost,
                badgeText = "60 NRG",
                tileColor = palette.secondary,
                palette = palette,
                onClick = { onActionClick(CombatAction.QUANTUM_OVERDRIVE) },
                modifier = Modifier.weight(1f)
            )

            // Nano Stim Heal
            ActionButtonTile(
                action = CombatAction.STIM_HEAL,
                isAvailable = currentEnergy >= CombatAction.STIM_HEAL.energyCost,
                badgeText = "20 NRG",
                tileColor = palette.hpColor,
                palette = palette,
                onClick = { onActionClick(CombatAction.STIM_HEAL) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ActionButtonTile(
    action: CombatAction,
    isAvailable: Boolean,
    badgeText: String,
    tileColor: Color,
    palette: CyberPalette,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val alpha = if (isAvailable) 1.0f else 0.35f
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isAvailable) palette.surfaceVariant else Color.Black.copy(alpha = 0.4f))
            .border(
                1.dp,
                if (isAvailable) tileColor.copy(alpha = 0.8f) else Color.Gray.copy(alpha = 0.2f),
                RoundedCornerShape(8.dp)
            )
            .clickable(enabled = isAvailable) { onClick() }
            .padding(horizontal = 8.dp, vertical = 10.dp)
            .testTag("action_${action.name.lowercase()}"),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = action.title,
                color = if (isAvailable) palette.onBackground else Color.Gray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .background(tileColor.copy(alpha = 0.15f * alpha), RoundedCornerShape(3.dp))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(
                    text = badgeText,
                    color = tileColor.copy(alpha = alpha),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun CombatLogTicker(
    logs: List<CombatLog>,
    palette: CyberPalette
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(palette.surface.copy(alpha = 0.7f))
            .border(1.dp, palette.borderGlow.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Column {
            Text(
                text = "NEURAL COMBAT TELEMETRY",
                color = palette.onSurface.copy(alpha = 0.5f),
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(4.dp))
            if (logs.isEmpty()) {
                Text(
                    text = "Awaiting engagement protocol. Select attack action above.",
                    color = palette.onSurface.copy(alpha = 0.5f),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            } else {
                logs.forEach { log ->
                    val color = when (log.type) {
                        LogType.CRITICAL_HIT -> palette.energyColor
                        LogType.VICTORY -> palette.hpColor
                        LogType.DEFEAT -> palette.accentCrit
                        LogType.ENEMY_ACTION -> palette.accentCrit.copy(alpha = 0.8f)
                        LogType.SENSOR_TRIGGER -> palette.primary
                        else -> palette.onSurface
                    }
                    Text(
                        text = "• ${log.text}",
                        color = color,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(vertical = 1.dp)
                    )
                }
            }
        }
    }
}
