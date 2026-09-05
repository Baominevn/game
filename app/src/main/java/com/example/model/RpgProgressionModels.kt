package com.example.model

enum class StatusEffectType(val label: String, val hexColor: Long) {
    BURN("Plasma Cháy Bỏng", 0xFFFF5722),
    FREEZE("Đóng Băng Nitơ", 0xFF00E5FF),
    SHOCK("Sốc Điện Tê Liệt", 0xFFFFD600),
    VIRUS_DOT("Nhiễm Mã Độc", 0xFF00E676)
}

data class ActiveStatusEffect(
    val type: StatusEffectType,
    val remainingTurns: Int,
    val damagePerTurn: Int
)

data class SocketChip(
    val id: String,
    val name: String,
    val rarity: ItemRarity,
    val bonusStatDescription: String,
    val attackBonus: Int = 0,
    val defenseBonus: Int = 0,
    val lifestealPercent: Float = 0f,
    val isSocketed: Boolean = false,
    val socketedItemName: String = ""
) {
    val effectDescription: String get() = bonusStatDescription
    val socketedToItemId: String get() = socketedItemName
}

data class CraftingMaterial(
    val id: String,
    val name: String,
    val description: String,
    val count: Int
)

data class CraftingRecipe(
    val id: String,
    val recipeName: String,
    val costCredits: Int,
    val costNanites: Int,
    val resultItem: GameItem
)

data class EquipmentSet(
    val id: String,
    val setName: String,
    val piecesRequired2: String,
    val piecesRequired4: String,
    val activePiecesCount: Int
) {
    val name: String get() = setName
    val twoPieceBonus: String get() = piecesRequired2
    val fourPieceBonus: String get() = piecesRequired4
    val currentEquippedCount: Int get() = activePiecesCount
}

data class TowerFloor(
    val floorNumber: Int,
    val enemyName: String,
    val bossPowerLevel: Int,
    val rewardCredits: Int,
    val rewardNanites: Int,
    val isCleared: Boolean = false
)

data class DailyBounty(
    val id: String,
    val title: String,
    val description: String,
    val currentProgress: Int,
    val maxProgress: Int,
    val rewardCredits: Int,
    val rewardNanites: Int,
    val isClaimed: Boolean = false
)

data class MasteryAchievement(
    val id: String,
    val title: String,
    val description: String,
    val rewardTitle: String,
    val isUnlocked: Boolean = false
)

data class CompanionDrone(
    val name: String = "Apex Scout-7",
    val level: Int = 1,
    val reconEfficiencyPercent: Int = 45,
    val isAutoScouting: Boolean = true,
    val harvestedMaterialsCount: Int = 18,
    val activeBuffText: String = "Quét điểm yếu (+15% sát thương chuẩn)"
)

data class PostMatchReport(
    val grade: String,
    val totalDamageDealt: Int,
    val totalDamageTaken: Int,
    val criticalHitsCount: Int,
    val tacticalAccuracyPercent: Int,
    val aiEvaluationSummary: String,
    val eloChange: Int
)
