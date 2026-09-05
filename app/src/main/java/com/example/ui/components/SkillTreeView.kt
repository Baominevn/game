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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SkillBranch
import com.example.model.SkillNode
import com.example.ui.theme.CyberPalette

@Composable
fun SkillTreeView(
    skillNodes: List<SkillNode>,
    skillPoints: Int,
    palette: CyberPalette,
    onUnlockNode: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedBranch by remember { mutableStateOf(SkillBranch.CYBER_NINJA) }

    val filteredNodes = skillNodes.filter { it.branch == selectedBranch }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(14.dp)
            .testTag("skill_tree_view")
    ) {
        // Points Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(palette.surface)
                .border(1.dp, palette.primary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "CÂY KỸ NĂNG LƯỢNG TỬ",
                        color = palette.primary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Mở khóa các đặc tính siêu việt để biến đổi phong cách chiến đấu",
                        color = palette.onSurface.copy(alpha = 0.6f),
                        fontSize = 11.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(palette.primary.copy(alpha = 0.15f))
                        .border(1.dp, palette.primary, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Stars, contentDescription = null, tint = palette.primary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$skillPoints SP",
                            color = palette.primary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Branch Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SkillBranch.values().forEach { branch ->
                val isSelected = (branch == selectedBranch)
                val branchColor = when (branch) {
                    SkillBranch.CYBER_NINJA -> palette.primary
                    SkillBranch.TITAN_MECHA -> palette.accent
                    SkillBranch.NETRUNNER -> palette.secondary
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) branchColor else palette.surface)
                        .border(1.dp, if (isSelected) branchColor else palette.borderGlow.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .clickable { selectedBranch = branch }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = branch.branchName,
                        color = if (isSelected) palette.background else palette.onSurface,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Skill Nodes List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredNodes) { node ->
                val canUnlock = skillPoints > 0 && !node.isUnlocked

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(palette.surface)
                        .border(
                            1.dp,
                            if (node.isUnlocked) palette.primary else palette.borderGlow.copy(alpha = 0.3f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Bậc ${node.tier}: ${node.name}",
                                    color = if (node.isUnlocked) palette.primary else palette.onSurface,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = node.description,
                                    color = palette.onSurface.copy(alpha = 0.7f),
                                    fontSize = 11.sp
                                )
                            }

                            Icon(
                                imageVector = if (node.isUnlocked) Icons.Default.CheckCircle else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (node.isUnlocked) palette.primary else palette.onSurface.copy(alpha = 0.4f),
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Stats Buffs Chips
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (node.bonusAttack > 0) {
                                StatBadge("ATK +${node.bonusAttack}", palette.primary)
                            }
                            if (node.bonusDefense > 0) {
                                StatBadge("DEF +${node.bonusDefense}", palette.accent)
                            }
                            if (node.bonusShield > 0) {
                                StatBadge("SHIELD +${node.bonusShield}", palette.secondary)
                            }
                            if (node.bonusCritRate > 0f) {
                                StatBadge("CRIT +${(node.bonusCritRate * 100).toInt()}%", palette.primary)
                            }
                        }

                        if (!node.isUnlocked) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { onUnlockNode(node.id) },
                                enabled = canUnlock,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = palette.primary,
                                    disabledContainerColor = palette.surface
                                )
                            ) {
                                Text(
                                    text = if (skillPoints > 0) "MỞ KHÓA (1 ĐIỂM SP)" else "KHÔNG ĐỦ ĐIỂM SP",
                                    color = if (canUnlock) palette.background else palette.onSurface.copy(alpha = 0.4f),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBadge(text: String, color: androidx.compose.ui.graphics.Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(text, color = color, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
    }
}
