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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Upgrade
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CraftingRecipe
import com.example.model.EquipmentSet
import com.example.model.FighterStats
import com.example.model.GameItem
import com.example.model.ItemRarity
import com.example.model.ItemType
import com.example.model.SocketChip
import com.example.ui.theme.CyberPalette

enum class ArsenalViewMode(val title: String) {
    INVENTORY("Kho Đồ"),
    SOCKET_CHIPS("Khảm Chip"),
    CRAFTING("Chế Tạo"),
    SETS("Bộ Trang Bị")
}

@Composable
fun InventoryArsenalView(
    items: List<GameItem>,
    playerStats: FighterStats,
    palette: CyberPalette,
    onEquipToggle: (GameItem) -> Unit,
    onUpgradeItem: (GameItem) -> Unit,
    onOpenLootCrate: () -> Unit,
    chips: List<SocketChip> = emptyList(),
    craftingRecipes: List<CraftingRecipe> = emptyList(),
    equipmentSets: List<EquipmentSet> = emptyList(),
    onSocketChip: (String, String) -> Unit = { _, _ -> },
    onCraftRecipe: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var viewMode by remember { mutableStateOf(ArsenalViewMode.INVENTORY) }
    var selectedFilter by remember { mutableStateOf<ItemType?>(null) }
    val filteredItems = if (selectedFilter == null) items else items.filter { it.type == selectedFilter }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(palette.background)
            .padding(12.dp)
            .testTag("inventory_arsenal_view")
    ) {
        // TOP ARSENAL BANNER & LOOT CRATE ACTION
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
                        text = "TACTICAL ARSENAL",
                        color = palette.onBackground,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Nanites: ${playerStats.nanites} | Credits: ${playerStats.credits}",
                        color = palette.primary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Button(
                    onClick = onOpenLootCrate,
                    enabled = playerStats.credits >= 150,
                    colors = ButtonDefaults.buttonColors(containerColor = palette.secondary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("open_loot_crate_button")
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Loot Crate (150₡)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // VIEW MODE SWITCHER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ArsenalViewMode.values().forEach { mode ->
                val isSelected = (mode == viewMode)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) palette.primary else palette.surface)
                        .border(1.dp, if (isSelected) palette.primary else palette.borderGlow.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                        .clickable { viewMode = mode }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = mode.title,
                        color = if (isSelected) palette.background else palette.onSurface,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        when (viewMode) {
            ArsenalViewMode.INVENTORY -> {
                // CATEGORY FILTER CHIPS
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item {
                        FilterChipItem(
                            title = "ALL",
                            isSelected = selectedFilter == null,
                            palette = palette,
                            onClick = { selectedFilter = null }
                        )
                    }
                    items(ItemType.values()) { type ->
                        FilterChipItem(
                            title = type.name,
                            isSelected = selectedFilter == type,
                            palette = palette,
                            onClick = { selectedFilter = type }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // ITEMS LIST
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredItems, key = { it.id }) { item ->
                        ArsenalItemCard(
                            item = item,
                            playerStats = playerStats,
                            palette = palette,
                            onEquipClick = { onEquipToggle(item) },
                            onUpgradeClick = { onUpgradeItem(item) }
                        )
                    }
                }
            }

            ArsenalViewMode.SOCKET_CHIPS -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = "VI MẠCH NÂNG CẤP (SOCKET CHIPS)",
                            color = palette.primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    items(chips) { chip ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(palette.surface)
                                .border(1.dp, palette.accent.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(chip.name, color = palette.accent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(chip.effectDescription, color = palette.onSurface.copy(alpha = 0.7f), fontSize = 11.sp)
                                    Text(
                                        text = if (chip.isSocketed) "Đã khảm vào: ${chip.socketedToItemId}" else "Chưa khảm",
                                        color = if (chip.isSocketed) palette.primary else palette.onSurface.copy(alpha = 0.5f),
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Button(
                                    onClick = {
                                        val firstWeapon = items.firstOrNull { it.type == ItemType.WEAPON }
                                        if (firstWeapon != null) {
                                            onSocketChip(chip.id, firstWeapon.id)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = palette.accent),
                                    modifier = Modifier.padding(start = 8.dp)
                                ) {
                                    Text(if (chip.isSocketed) "THÁO" else "KHẢM", color = palette.background, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }
                }
            }

            ArsenalViewMode.CRAFTING -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = "XƯỞNG ĐÚC NANO (CRAFTING FORGE)",
                            color = palette.primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    items(craftingRecipes) { recipe ->
                        val canCraft = playerStats.credits >= recipe.costCredits && playerStats.nanites >= recipe.costNanites

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(palette.surface)
                                .border(1.dp, palette.primary.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(recipe.recipeName, color = palette.primary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("${recipe.costCredits}₡ / ${recipe.costNanites} Nanites", color = palette.accent, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                }
                                Text("Tạo ra: ${recipe.resultItem.name} (${recipe.resultItem.rarity.name})", color = palette.onSurface.copy(alpha = 0.7f), fontSize = 11.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Button(
                                    onClick = { onCraftRecipe(recipe.id) },
                                    enabled = canCraft,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = palette.primary)
                                ) {
                                    Text(if (canCraft) "RÈN CHẾ TẠO NGAY" else "THIẾU NGUYÊN LIỆU", color = palette.background, fontWeight = FontWeight.Black, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            ArsenalViewMode.SETS -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Text(
                            text = "BỘ TRANG BỊ ĐỒNG HÓA (SET BONUSES)",
                            color = palette.primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    items(equipmentSets) { eqSet ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(palette.surface)
                                .border(1.dp, palette.primary.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(eqSet.name, color = palette.primary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Đang mặc: ${eqSet.currentEquippedCount}/4", color = palette.accent, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "• [2 Món]: ${eqSet.twoPieceBonus}",
                                    color = if (eqSet.currentEquippedCount >= 2) palette.primary else palette.onSurface.copy(alpha = 0.5f),
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "• [4 Món]: ${eqSet.fourPieceBonus}",
                                    color = if (eqSet.currentEquippedCount >= 4) palette.accent else palette.onSurface.copy(alpha = 0.5f),
                                    fontSize = 11.sp
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
fun FilterChipItem(
    title: String,
    isSelected: Boolean,
    palette: CyberPalette,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) palette.primary else palette.surface)
            .border(1.dp, if (isSelected) palette.primary else palette.borderGlow.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = title,
            color = if (isSelected) Color.Black else palette.onBackground,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun ArsenalItemCard(
    item: GameItem,
    playerStats: FighterStats,
    palette: CyberPalette,
    onEquipClick: () -> Unit,
    onUpgradeClick: () -> Unit
) {
    val rarityColor = Color(item.rarity.hexColor)
    val upgradeCostNanites = item.upgradeLevel * 10
    val upgradeCostCredits = item.upgradeLevel * 50
    val canUpgrade = playerStats.nanites >= upgradeCostNanites && playerStats.credits >= upgradeCostCredits

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(palette.surface)
            .border(
                1.dp,
                if (item.isEquipped) palette.primary else rarityColor.copy(alpha = 0.4f),
                RoundedCornerShape(8.dp)
            )
            .padding(10.dp)
            .testTag("item_${item.id}")
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.name,
                            color = palette.onBackground,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(rarityColor.copy(alpha = 0.2f), RoundedCornerShape(3.dp))
                                .border(1.dp, rarityColor.copy(alpha = 0.6f), RoundedCornerShape(3.dp))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "${item.rarity.label.uppercase()} +${item.upgradeLevel}",
                                color = rarityColor,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Text(
                        text = item.description,
                        color = palette.onSurface.copy(alpha = 0.7f),
                        fontSize = 10.sp,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }

                // Equip / Unequip Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (item.isEquipped) palette.primary else palette.surfaceVariant)
                        .border(1.dp, palette.primary, RoundedCornerShape(6.dp))
                        .clickable { onEquipClick() }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("equip_${item.id}")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (item.isEquipped) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                        }
                        Text(
                            text = if (item.isEquipped) "EQUIPPED" else "EQUIP",
                            color = if (item.isEquipped) Color.Black else palette.onBackground,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Stat Modifiers & Upgrade Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Stats Badges
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (item.attackBonus > 0) {
                        Text(text = "+${item.attackBonus} ATK", color = palette.accentCrit, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    if (item.defenseBonus > 0) {
                        Text(text = "+${item.defenseBonus} DEF", color = palette.hpColor, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    if (item.shieldBonus > 0) {
                        Text(text = "+${item.shieldBonus} SHD", color = palette.shieldColor, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    if (item.critBonusPercent > 0f) {
                        Text(text = "+${(item.critBonusPercent * 100).toInt()}% CRIT", color = palette.energyColor, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                }

                // Upgrade Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (canUpgrade) palette.primary.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.3f))
                        .border(1.dp, if (canUpgrade) palette.primary else Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                        .clickable(enabled = canUpgrade) { onUpgradeClick() }
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                        .testTag("upgrade_${item.id}")
                ) {
                    Text(
                        text = "UPGRADE (${upgradeCostNanites}◆ / ${upgradeCostCredits}₡)",
                        color = if (canUpgrade) palette.primary else Color.Gray,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
