package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.model.EnemyFighter
import com.example.model.FighterStats
import com.example.ui.theme.CyberPalette

@Composable
fun AiTacticalView(
    aiAdvice: String,
    isAiThinking: Boolean,
    enemy: EnemyFighter,
    stats: FighterStats,
    palette: CyberPalette,
    onRefreshAdvice: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background)
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
            .testTag("ai_tactical_view")
    ) {
        // TOP AI HEADER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(palette.surface)
                .border(1.dp, palette.primary.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "NEURAL AI COMBAT CO-PILOT",
                        color = palette.onBackground,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Model: Gemini 3.5 Flash Engine / Neural Heuristics",
                        color = palette.primary,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = palette.primary,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // AI LIVE DIRECTIVE CARD
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(palette.surfaceVariant)
                .border(1.dp, palette.borderGlow, RoundedCornerShape(10.dp))
                .padding(14.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isAiThinking) palette.energyColor else palette.hpColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isAiThinking) "ANALYZING TACTICAL MATRIX..." else "TACTICAL ADVICE [LIVE FEED]",
                            color = palette.primary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    if (isAiThinking) {
                        CircularProgressIndicator(
                            color = palette.primary,
                            modifier = Modifier.size(14.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = aiAdvice,
                    color = palette.onBackground,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // REFRESH ADVICE BUTTON
        Button(
            onClick = onRefreshAdvice,
            enabled = !isAiThinking,
            colors = ButtonDefaults.buttonColors(containerColor = palette.surfaceVariant),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, palette.primary, RoundedCornerShape(8.dp))
                .testTag("refresh_ai_advice_button")
        ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = palette.primary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "REQUEST DEEP NEURAL TACTICAL RE-EVALUATION", color = palette.primary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(14.dp))

        // TARGET COMBAT INTELLIGENCE CARD
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(palette.surface)
                .border(1.dp, palette.accentCrit.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                .padding(10.dp)
        ) {
            Column {
                Text(
                    text = "TARGET EXPLOIT MATRIX: ${enemy.name}",
                    color = palette.accentCrit,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "• Enemy Weakness: ${enemy.weakness}", color = palette.onBackground, fontSize = 10.sp)
                Text(text = "• Neural Strategy: ${enemy.aiStrategy}", color = palette.onBackground, fontSize = 10.sp)
                Text(text = "• Shield Frequency: ${if (enemy.currentShield > 0) "Active (${enemy.currentShield} SHD)" else "Breached (Vulnerable)"}", color = palette.shieldColor, fontSize = 10.sp)
                Text(text = "• Reward Yield: ${enemy.rewardCredits} Credits, ${enemy.rewardNanites} Nanites, ${enemy.rewardXp} XP", color = palette.energyColor, fontSize = 10.sp)
            }
        }
    }
}
