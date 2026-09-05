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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.model.DailyBounty
import com.example.model.MasteryAchievement
import com.example.model.SyndicateGuild
import com.example.model.WorldRaidBoss
import com.example.ui.theme.CyberPalette

enum class GuildSubTab(val title: String) {
    SYNDICATE("Bang Hội"),
    RAID_BOSS("Boss Thế Giới"),
    BOUNTIES("Nhiệm Vụ")
}

@Composable
fun SyndicateRaidView(
    syndicate: SyndicateGuild,
    raidBoss: WorldRaidBoss,
    bounties: List<DailyBounty>,
    achievements: List<MasteryAchievement>,
    palette: CyberPalette,
    onAttackRaidBoss: (Long) -> Unit,
    onClaimBounty: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(GuildSubTab.RAID_BOSS) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(14.dp)
            .testTag("syndicate_raid_view")
    ) {
        // Tab Selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GuildSubTab.values().forEach { tab ->
                val isSelected = (tab == activeTab)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) palette.primary else palette.surface)
                        .border(1.dp, if (isSelected) palette.primary else palette.borderGlow.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .clickable { activeTab = tab }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab.title,
                        color = if (isSelected) palette.background else palette.onSurface,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (activeTab) {
            GuildSubTab.RAID_BOSS -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        // World Raid Boss Card
                        val hpPercent = (raidBoss.currentHp.toFloat() / raidBoss.maxHp.toFloat()).coerceIn(0f, 1f)

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(palette.surface)
                                .border(1.dp, palette.danger.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = raidBoss.name,
                                            color = palette.danger,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text(
                                            text = raidBoss.title,
                                            color = palette.onSurface.copy(alpha = 0.6f),
                                            fontSize = 11.sp
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(palette.danger.copy(alpha = 0.2f))
                                            .border(1.dp, palette.danger, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("${raidBoss.participantsCount} ĐẶC VỤ", color = palette.danger, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("LƯỢNG TỬ SINH LỰC HP", color = palette.onSurface.copy(alpha = 0.7f), fontSize = 10.sp)
                                    Text("${raidBoss.currentHp} / ${raidBoss.maxHp}", color = palette.danger, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { hpPercent },
                                    modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                                    color = palette.danger,
                                    trackColor = palette.surface,
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Điểm yếu chiến thuật: ${raidBoss.weaknessElement}",
                                    color = palette.accent,
                                    fontSize = 11.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { onAttackRaidBoss(35000L) },
                                    modifier = Modifier.fillMaxWidth().testTag("btn_attack_raid_boss"),
                                    colors = ButtonDefaults.buttonColors(containerColor = palette.danger)
                                ) {
                                    Icon(Icons.Default.FlashOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("TẤN CÔNG BOSS THẾ GIỚI (CO-OP)", color = Color.White, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = "BẢNG XẾP HẠNG SÁT THƯƠNG CO-OP",
                            color = palette.primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    items(raidBoss.topDamagers) { participant ->
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
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("#${participant.rank}", color = palette.primary, fontWeight = FontWeight.Black, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(participant.callsign, color = palette.onSurface, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("Phần thưởng: ${participant.rewardTier}", color = palette.accent, fontSize = 10.sp)
                                    }
                                }
                                Text("${participant.damageContributed} DMG", color = palette.danger, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            GuildSubTab.SYNDICATE -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(palette.surface)
                                .border(1.dp, palette.primary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Groups, contentDescription = null, tint = palette.primary, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "${syndicate.name} [${syndicate.tag}]",
                                            color = palette.primary,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text(
                                            text = "Bang Hội Cấp ${syndicate.level} • ${syndicate.memberCount}/${syndicate.maxMembers} Thành Viên",
                                            color = palette.onSurface.copy(alpha = 0.6f),
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(palette.primary.copy(alpha = 0.1f))
                                        .border(1.dp, palette.primary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Column {
                                        Text("BÙA LỢI BANG HỘI:", color = palette.primary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        Text(syndicate.perkDescription, color = palette.onSurface, fontSize = 12.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Thông báo: ${syndicate.announcement}",
                                    color = palette.accent,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            GuildSubTab.BOUNTIES -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = "NHIỆM VỤ HÀNG NGÀY (DAILY BOUNTIES)",
                            color = palette.primary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    items(bounties) { bounty ->
                        val isComplete = bounty.currentProgress >= bounty.maxProgress

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(palette.surface)
                                .border(
                                    1.dp,
                                    if (bounty.isClaimed) palette.borderGlow.copy(alpha = 0.2f) else if (isComplete) palette.primary else palette.borderGlow.copy(alpha = 0.3f),
                                    RoundedCornerShape(10.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = bounty.title,
                                        color = if (bounty.isClaimed) palette.onSurface.copy(alpha = 0.5f) else palette.onSurface,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = "${bounty.currentProgress}/${bounty.maxProgress}",
                                        color = palette.primary,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp
                                    )
                                }

                                Text(
                                    text = bounty.description,
                                    color = palette.onSurface.copy(alpha = 0.6f),
                                    fontSize = 11.sp
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Thưởng: +${bounty.rewardCredits} Credits, +${bounty.rewardNanites} Nanites",
                                        color = palette.accent,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    if (!bounty.isClaimed) {
                                        Button(
                                            onClick = { onClaimBounty(bounty.id) },
                                            enabled = isComplete,
                                            colors = ButtonDefaults.buttonColors(containerColor = palette.primary),
                                            modifier = Modifier.height(32.dp).testTag("btn_claim_${bounty.id}")
                                        ) {
                                            Text("NHẬN", color = palette.background, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                        }
                                    } else {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = palette.primary, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("ĐÃ NHẬN", color = palette.primary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
