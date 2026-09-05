package com.example.model

enum class ItemRarity(val label: String, val hexColor: Long) {
    COMMON("Common", 0xFF9E9E9E),
    RARE("Rare", 0xFF2196F3),
    EPIC("Epic", 0xFF9C27B0),
    LEGENDARY("Legendary", 0xFFFF9800),
    MYTHIC("Mythic", 0xFF00E5FF)
}

enum class ItemType {
    WEAPON,
    ARMOR,
    SHIELD,
    TECH_CHIP,
    STIMPACK
}

data class GameItem(
    val id: String,
    val name: String,
    val description: String,
    val type: ItemType,
    val rarity: ItemRarity,
    val attackBonus: Int = 0,
    val defenseBonus: Int = 0,
    val shieldBonus: Int = 0,
    val critBonusPercent: Float = 0f,
    val energyRegenBonus: Float = 0f,
    val priceCredits: Int = 100,
    val isEquipped: Boolean = false,
    val upgradeLevel: Int = 1
)

data class FighterStats(
    val maxHp: Int = 500,
    val currentHp: Int = 500,
    val maxShield: Int = 200,
    val currentShield: Int = 200,
    val maxEnergy: Int = 100,
    val currentEnergy: Int = 50,
    val baseAttack: Int = 45,
    val baseDefense: Int = 20,
    val baseCritRate: Float = 0.15f,
    val level: Int = 1,
    val currentXp: Int = 0,
    val xpToNextLevel: Int = 100,
    val credits: Int = 350,
    val nanites: Int = 25
)

enum class CombatAction(val title: String, val energyCost: Int, val description: String) {
    LIGHT_SLASH("Cyber Slash", 0, "Fast kinetic blade strike with combo potential"),
    PLASMA_CANNON("Plasma Blast", 25, "High-temp plasma shot; pierces heavy armor"),
    EMP_DISRUPT("EMP Shockwave", 30, "Destabilizes shields and stuns opponent"),
    NANO_SHIELD("Nano Shield", 15, "Deploys barrier for parry and incoming damage reduction"),
    QUANTUM_OVERDRIVE("Overdrive Ult", 60, "Full-power quantum burst dealing massive damage"),
    STIM_HEAL("Nano Stim", 20, "Synthesizes nanites to restore HP and stabilize systems")
}

data class EnemyFighter(
    val id: String,
    val name: String,
    val title: String,
    val avatarType: String,
    val maxHp: Int,
    var currentHp: Int,
    val maxShield: Int,
    var currentShield: Int,
    val attackPower: Int,
    val defensePower: Int,
    val level: Int,
    val weakness: String,
    val aiStrategy: String,
    val rewardCredits: Int,
    val rewardNanites: Int,
    val rewardXp: Int,
    val isBerserk: Boolean = false,
    val tier: MatchTier = MatchTier.ROOKIE_BOT
)

data class CombatLog(
    val id: Long = System.currentTimeMillis(),
    val text: String,
    val type: LogType
)

enum class LogType {
    PLAYER_ACTION,
    ENEMY_ACTION,
    CRITICAL_HIT,
    SHIELD_PARRY,
    SENSOR_TRIGGER,
    AI_DIRECTIVE,
    VICTORY,
    DEFEAT,
    TACTICAL_INTEL
}

data class RealWorldSensorData(
    val ambientLux: Float = 50f,
    val lightBonusDescription: String = "Normal Ambient Light",
    val kineticEnergyCharge: Float = 0f,
    val isPhysicalMoving: Boolean = false,
    val batteryLevel: Int = 85,
    val isCharging: Boolean = false,
    val geoSector: String = "Sector 07-Alpha",
    val latitude: Double = 21.0285,
    val longitude: Double = 105.8542
)

data class AntiCheatReport(
    val isSecure: Boolean = true,
    val signatureToken: String = "",
    val memoryIntegrityValid: Boolean = true,
    val clockTimingValid: Boolean = true,
    val debuggerIsolated: Boolean = true,
    val lastValidationTime: Long = System.currentTimeMillis(),
    val threatLevel: String = "ZERO THREAT - SYSTEM OPTIMAL"
)

enum class CyberThemeMode(val title: String) {
    NEON_CYAN("Neon Cyan"),
    MATRIX_EMERALD("Matrix Emerald"),
    CRIMSON_OVERDRIVE("Crimson Overdrive"),
    OBSIDIAN_GOLD("Obsidian Gold")
}

enum class PerformanceMode(val title: String, val fpsLimit: Int, val description: String) {
    ULTRA("Ultra 120Hz", 120, "Full particle animations & max visual fidelity"),
    BALANCED("Balanced 60Hz", 60, "Standard 60fps with optimized battery draw"),
    BATTERY_SAVER("Power Saver", 30, "Darkened surfaces & minimal background particle draw")
}
