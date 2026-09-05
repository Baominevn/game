package com.example.combat

import com.example.anticheat.AntiCheatEngine
import com.example.audio.SoundEngine
import com.example.model.CombatAction
import com.example.model.CombatLog
import com.example.model.EnemyFighter
import com.example.model.FighterStats
import com.example.model.GameItem
import com.example.model.LogType
import com.example.model.RealWorldSensorData
import com.example.model.MatchTier
import com.example.model.PostMatchReport
import kotlin.random.Random

data class CombatResult(
    val playerStats: FighterStats,
    val enemy: EnemyFighter,
    val logs: List<CombatLog>,
    val damageDealt: Int,
    val damageTaken: Int,
    val isPlayerCrit: Boolean,
    val isEnemyCrit: Boolean,
    val actionEffectType: ActionEffectType,
    val isVictory: Boolean,
    val isDefeat: Boolean,
    val lootAwarded: GameItem? = null,
    val isBerserkNow: Boolean = false,
    val postMatchReport: PostMatchReport? = null
)

enum class ActionEffectType {
    NONE,
    SLASH_ATTACK,
    PLASMA_EXPLOSION,
    EMP_PULSE,
    SHIELD_PARRY,
    QUANTUM_BEAM,
    HEAL_BURST
}

class CombatEngine(
    private val soundEngine: SoundEngine,
    private val antiCheat: AntiCheatEngine
) {

    fun executePlayerTurn(
        action: CombatAction,
        currentStats: FighterStats,
        enemy: EnemyFighter,
        sensorData: RealWorldSensorData,
        equippedItems: List<GameItem>,
        stateSignature: String
    ): CombatResult {
        // Anti-cheat verification
        antiCheat.checkTimingIntegrity()
        antiCheat.verifyStateIntegrity(currentStats, stateSignature)

        var newStats = currentStats
        var newEnemy = enemy.copy()
        val newLogs = mutableListOf<CombatLog>()
        var damageDealt = 0
        var damageTaken = 0
        var isPlayerCrit = false
        var isEnemyCrit = false
        var effectType = ActionEffectType.NONE

        // Calculate item bonuses
        val totalBonusAtk = equippedItems.filter { it.isEquipped }.sumOf { it.attackBonus }
        val totalBonusDef = equippedItems.filter { it.isEquipped }.sumOf { it.defenseBonus }
        val totalBonusCrit = equippedItems.filter { it.isEquipped }.sumOf { (it.critBonusPercent * 100).toInt() } / 100f

        // Real-world sensor bonus calculation
        var sensorBonusAtk = 0
        var sensorCritBonus = 0f
        if (sensorData.ambientLux < 25f) {
            sensorCritBonus = 0.25f // Shadow stealth bonus
            newLogs.add(CombatLog(text = "REAL-WORLD SHADOW: Ambient darkness grant +25% Crit chance!", type = LogType.SENSOR_TRIGGER))
        } else if (sensorData.ambientLux > 400f && action == CombatAction.PLASMA_CANNON) {
            sensorBonusAtk = 20 // Solar overcharge
            newLogs.add(CombatLog(text = "REAL-WORLD SOLAR: High ambient light overcharges plasma (+20 ATK)!", type = LogType.SENSOR_TRIGGER))
        }

        // Kinetic motion bonus
        val kineticCharge = sensorData.kineticEnergyCharge
        val kineticDmgBonus = (kineticCharge * 0.4f).toInt()
        if (kineticDmgBonus > 5) {
            newLogs.add(CombatLog(text = "KINETIC ENERGY: Physical movement discharges +$kineticDmgBonus bonus damage!", type = LogType.SENSOR_TRIGGER))
        }

        // Execute Player Action
        when (action) {
            CombatAction.LIGHT_SLASH -> {
                effectType = ActionEffectType.SLASH_ATTACK
                soundEngine.playBladeSlash()

                val critChance = newStats.baseCritRate + totalBonusCrit + sensorCritBonus
                isPlayerCrit = Random.nextFloat() < critChance
                val critMultiplier = if (isPlayerCrit) 1.8f else 1.0f

                val rawDamage = ((newStats.baseAttack + totalBonusAtk + sensorBonusAtk + kineticDmgBonus) * critMultiplier).toInt()
                val netDamage = (rawDamage - (newEnemy.defensePower * 0.4f)).toInt().coerceAtLeast(18)
                damageDealt = netDamage

                // Apply to enemy shield first, then HP
                if (newEnemy.currentShield > 0) {
                    val shieldAbsorb = newEnemy.currentShield.coerceAtMost(damageDealt)
                    newEnemy.currentShield -= shieldAbsorb
                    val remainingDmg = damageDealt - shieldAbsorb
                    newEnemy.currentHp = (newEnemy.currentHp - remainingDmg).coerceAtLeast(0)
                } else {
                    newEnemy.currentHp = (newEnemy.currentHp - damageDealt).coerceAtLeast(0)
                }

                // Generates energy on basic slash
                newStats = newStats.copy(currentEnergy = (newStats.currentEnergy + 15).coerceAtMost(newStats.maxEnergy))

                val critText = if (isPlayerCrit) " [CRITICAL HIT!]" else ""
                newLogs.add(CombatLog(text = "You executed Cyber Slash dealing $damageDealt damage$critText!", type = if (isPlayerCrit) LogType.CRITICAL_HIT else LogType.PLAYER_ACTION))
            }

            CombatAction.PLASMA_CANNON -> {
                if (newStats.currentEnergy >= action.energyCost) {
                    effectType = ActionEffectType.PLASMA_EXPLOSION
                    soundEngine.playLaserShoot()
                    newStats = newStats.copy(currentEnergy = newStats.currentEnergy - action.energyCost)

                    val critChance = newStats.baseCritRate + totalBonusCrit + 0.1f
                    isPlayerCrit = Random.nextFloat() < critChance
                    val critMultiplier = if (isPlayerCrit) 2.0f else 1.0f

                    val rawDamage = ((newStats.baseAttack * 1.6f + totalBonusAtk * 1.5f + sensorBonusAtk + kineticDmgBonus) * critMultiplier).toInt()
                    // Plasma ignores 50% enemy defense
                    val netDamage = (rawDamage - (newEnemy.defensePower * 0.2f)).toInt().coerceAtLeast(35)
                    damageDealt = netDamage

                    if (newEnemy.currentShield > 0) {
                        val shieldAbsorb = newEnemy.currentShield.coerceAtMost(damageDealt)
                        newEnemy.currentShield -= shieldAbsorb
                        val remainingDmg = damageDealt - shieldAbsorb
                        newEnemy.currentHp = (newEnemy.currentHp - remainingDmg).coerceAtLeast(0)
                    } else {
                        newEnemy.currentHp = (newEnemy.currentHp - damageDealt).coerceAtLeast(0)
                    }

                    newLogs.add(CombatLog(text = "Plasma Cannon superheated ${newEnemy.name} for $damageDealt thermal damage!", type = LogType.PLAYER_ACTION))
                } else {
                    newLogs.add(CombatLog(text = "Not enough energy for Plasma Cannon!", type = LogType.PLAYER_ACTION))
                }
            }

            CombatAction.EMP_DISRUPT -> {
                if (newStats.currentEnergy >= action.energyCost) {
                    effectType = ActionEffectType.EMP_PULSE
                    soundEngine.playEmpShockwave()
                    newStats = newStats.copy(currentEnergy = newStats.currentEnergy - action.energyCost)

                    // Destroys shield instantly + minor damage
                    val shieldDestroyed = newEnemy.currentShield
                    newEnemy.currentShield = 0
                    damageDealt = (newStats.baseAttack * 0.8f + totalBonusAtk).toInt()
                    newEnemy.currentHp = (newEnemy.currentHp - damageDealt).coerceAtLeast(0)

                    newLogs.add(CombatLog(text = "EMP Shockwave annihilated $shieldDestroyed enemy shield points and dealt $damageDealt shock damage!", type = LogType.PLAYER_ACTION))
                } else {
                    newLogs.add(CombatLog(text = "Insufficient energy for EMP Shockwave!", type = LogType.PLAYER_ACTION))
                }
            }

            CombatAction.NANO_SHIELD -> {
                effectType = ActionEffectType.SHIELD_PARRY
                soundEngine.playShieldBlock()
                val restoreShieldAmount = 60 + (totalBonusDef * 2)
                newStats = newStats.copy(
                    currentShield = (newStats.currentShield + restoreShieldAmount).coerceAtMost(newStats.maxShield),
                    currentEnergy = (newStats.currentEnergy - action.energyCost).coerceAtLeast(0)
                )
                newLogs.add(CombatLog(text = "Nano Shield deployed! Recharged +$restoreShieldAmount shield points and set Parry matrix.", type = LogType.SHIELD_PARRY))
            }

            CombatAction.QUANTUM_OVERDRIVE -> {
                if (newStats.currentEnergy >= action.energyCost) {
                    effectType = ActionEffectType.QUANTUM_BEAM
                    soundEngine.playOverdriveUltimate()
                    newStats = newStats.copy(currentEnergy = newStats.currentEnergy - action.energyCost)

                    isPlayerCrit = true
                    damageDealt = ((newStats.baseAttack * 2.8f + totalBonusAtk * 2.5f + kineticDmgBonus * 2) * 1.5f).toInt()

                    // Completely ignores shields and hits direct hull
                    newEnemy.currentHp = (newEnemy.currentHp - damageDealt).coerceAtLeast(0)

                    newLogs.add(CombatLog(text = "QUANTUM OVERDRIVE UNLEASHED: Obliterated target hull for $damageDealt direct damage!", type = LogType.CRITICAL_HIT))
                } else {
                    newLogs.add(CombatLog(text = "Overdrive requires 60 Energy! Attack with Cyber Slash to build energy.", type = LogType.PLAYER_ACTION))
                }
            }

            CombatAction.STIM_HEAL -> {
                effectType = ActionEffectType.HEAL_BURST
                soundEngine.playUiBlip()
                val healHp = 120
                val healShield = 40
                newStats = newStats.copy(
                    currentHp = (newStats.currentHp + healHp).coerceAtMost(newStats.maxHp),
                    currentShield = (newStats.currentShield + healShield).coerceAtMost(newStats.maxShield),
                    currentEnergy = (newStats.currentEnergy - action.energyCost).coerceAtLeast(0)
                )
                newLogs.add(CombatLog(text = "Nano Stimpack synthesized! Restored +$healHp HP & +$healShield Shield.", type = LogType.PLAYER_ACTION))
            }
        }

        // Check if enemy is defeated
        if (newEnemy.currentHp <= 0) {
            soundEngine.playVictoryFanfare()
            soundEngine.setBerserkBgm(false)
            val gainedCredits = newEnemy.rewardCredits
            val gainedNanites = newEnemy.rewardNanites
            val gainedXp = newEnemy.rewardXp

            var level = newStats.level
            var currentXp = newStats.currentXp + gainedXp
            var xpNeeded = newStats.xpToNextLevel
            if (currentXp >= xpNeeded) {
                level++
                currentXp -= xpNeeded
                xpNeeded = (xpNeeded * 1.5f).toInt()
                soundEngine.playLevelUpFanfare()
                newLogs.add(CombatLog(text = "LEVEL UP! Reached Operative Level $level! Max HP & Attack increased.", type = LogType.VICTORY))
            }

            newStats = newStats.copy(
                credits = newStats.credits + gainedCredits,
                nanites = newStats.nanites + gainedNanites,
                level = level,
                currentXp = currentXp,
                xpToNextLevel = xpNeeded,
                baseAttack = newStats.baseAttack + (if (level > currentStats.level) 8 else 0),
                maxHp = newStats.maxHp + (if (level > currentStats.level) 40 else 0)
            )

            newLogs.add(CombatLog(text = "VICTORY! ${newEnemy.name} destroyed. Acquired +$gainedCredits Credits, +$gainedNanites Nanites!", type = LogType.VICTORY))

            val postReport = PostMatchReport(
                grade = if (newStats.currentHp > newStats.maxHp * 0.65f) "S+" else "A",
                totalDamageDealt = damageDealt + 280,
                totalDamageTaken = (newStats.maxHp - newStats.currentHp).coerceAtLeast(0),
                criticalHitsCount = if (isPlayerCrit) 2 else 1,
                tacticalAccuracyPercent = 94,
                aiEvaluationSummary = "Chiến thuật phối hợp xuất sắc. Phá vỡ phòng tuyến đối thủ và dứt điểm mục tiêu chuẩn xác.",
                eloChange = +28
            )

            return CombatResult(
                playerStats = newStats,
                enemy = newEnemy,
                logs = newLogs,
                damageDealt = damageDealt,
                damageTaken = 0,
                isPlayerCrit = isPlayerCrit,
                isEnemyCrit = false,
                actionEffectType = effectType,
                isVictory = true,
                isDefeat = false,
                isBerserkNow = false,
                postMatchReport = postReport
            )
        }

        // Check AI Boss Berserk Stage
        var isBerserkNow = newEnemy.isBerserk
        if (!newEnemy.isBerserk && newEnemy.currentHp > 0 && newEnemy.currentHp <= (newEnemy.maxHp * 0.28f)) {
            isBerserkNow = true
            newEnemy = newEnemy.copy(isBerserk = true)
            soundEngine.setBerserkBgm(true)
            soundEngine.playOverdriveUltimate()
            newLogs.add(CombatLog(text = "AI BERSERK PROTOCOL: ${newEnemy.name} activates Berserk Mode! ATK +50%, Synthwave BGM accelerated!", type = LogType.CRITICAL_HIT))
        }

        // Enemy Turn / Counter-Attack (if enemy is still alive)
        var enemyRawDmg = newEnemy.attackPower + Random.nextInt(-4, 5)
        if (isBerserkNow) {
            enemyRawDmg = (enemyRawDmg * 1.5f).toInt()
        }

        val isParrying = (action == CombatAction.NANO_SHIELD)
        val parryReduction = if (isParrying) 0.6f else 0.0f

        val effectiveDef = (newStats.baseDefense + totalBonusDef) * 0.5f
        damageTaken = ((enemyRawDmg * (1f - parryReduction)) - effectiveDef).toInt().coerceAtLeast(10)

        // Apply enemy damage to player shield first, then HP
        if (newStats.currentShield > 0) {
            val shieldSoak = newStats.currentShield.coerceAtMost(damageTaken)
            val hpDmg = damageTaken - shieldSoak
            newStats = newStats.copy(
                currentShield = newStats.currentShield - shieldSoak,
                currentHp = (newStats.currentHp - hpDmg).coerceAtLeast(0)
            )
            if (shieldSoak > 0) {
                soundEngine.playShieldBlock()
            }
        } else {
            soundEngine.playCritExplosion()
            newStats = newStats.copy(currentHp = (newStats.currentHp - damageTaken).coerceAtLeast(0))
        }

        val parryMsg = if (isParrying) " (Nano-Parried -60% damage!)" else ""
        val berserkTag = if (isBerserkNow) " [BERSERK BURST]" else ""
        newLogs.add(CombatLog(text = "${newEnemy.name} retaliated with ${newEnemy.aiStrategy}$berserkTag dealing $damageTaken damage$parryMsg!", type = LogType.ENEMY_ACTION))

        val isDefeat = newStats.currentHp <= 0
        if (isDefeat) {
            soundEngine.setBerserkBgm(false)
            newLogs.add(CombatLog(text = "CRITICAL FAILURE: Armor destroyed! System emergency recovery activated.", type = LogType.DEFEAT))
            newStats = newStats.copy(currentHp = 100, currentShield = 50)
        }

        val postReport = if (isDefeat) {
            PostMatchReport(
                grade = "C",
                totalDamageDealt = damageDealt,
                totalDamageTaken = currentStats.maxHp,
                criticalHitsCount = 0,
                tacticalAccuracyPercent = 64,
                aiEvaluationSummary = "Kẻ địch áp đảo. Nên gia cố giáp Nano, né đòn đúng nhịp và dùng khiên chắn kịp thời.",
                eloChange = -15
            )
        } else null

        return CombatResult(
            playerStats = newStats,
            enemy = newEnemy,
            logs = newLogs,
            damageDealt = damageDealt,
            damageTaken = damageTaken,
            isPlayerCrit = isPlayerCrit,
            isEnemyCrit = isEnemyCrit,
            actionEffectType = effectType,
            isVictory = false,
            isDefeat = isDefeat,
            isBerserkNow = isBerserkNow,
            postMatchReport = postReport
        )
    }

    fun generateEnemyForTier(tier: MatchTier, stage: Int): EnemyFighter {
        return when (tier) {
            MatchTier.ROOKIE_BOT -> EnemyFighter(
                id = "bot_rookie_$stage",
                name = "Drone Tân Binh (Bot Gà)",
                title = "Mục Tiêu Tập Huấn Tân Thủ",
                avatarType = "ANDROID",
                maxHp = 220 + (stage * 20),
                currentHp = 220 + (stage * 20),
                maxShield = 40 + (stage * 10),
                currentShield = 40 + (stage * 10),
                attackPower = 18 + (stage * 2),
                defensePower = 8 + stage,
                level = 1,
                weakness = "Dễ bị hạ gục bởi đòn chém cơ bản",
                aiStrategy = "Bắn thử nghiệm nhịp chậm",
                rewardCredits = 100,
                rewardNanites = 12,
                rewardXp = 60,
                tier = MatchTier.ROOKIE_BOT
            )
            MatchTier.INTERMEDIATE_BOT -> EnemyFighter(
                id = "bot_inter_$stage",
                name = "Viper Android (Bot Trung)",
                title = "Chiến Binh Huấn Luyện Tác Chiến",
                avatarType = "ANDROID",
                maxHp = 360 + (stage * 35),
                currentHp = 360 + (stage * 35),
                maxShield = 90 + (stage * 15),
                currentShield = 90 + (stage * 15),
                attackPower = 32 + (stage * 4),
                defensePower = 18 + (stage * 2),
                level = 3 + stage,
                weakness = "Sơ hở khi bị trúng xung điện EMP",
                aiStrategy = "Phản xạ tầm trung & Tấn công nhịp đều",
                rewardCredits = 200,
                rewardNanites = 25,
                rewardXp = 140,
                tier = MatchTier.INTERMEDIATE_BOT
            )
            MatchTier.ADVANCED_BOT -> EnemyFighter(
                id = "bot_adv_$stage",
                name = "Ares Shadow (Bot Mạnh)",
                title = "Sát Thủ Mạng Neural Thần Kinh",
                avatarType = "ASSASSIN",
                maxHp = 520 + (stage * 50),
                currentHp = 520 + (stage * 50),
                maxShield = 150 + (stage * 25),
                currentShield = 150 + (stage * 25),
                attackPower = 48 + (stage * 6),
                defensePower = 28 + (stage * 4),
                level = 6 + stage,
                weakness = "Phản đòn khiên Nano Parry ở thời khắc vàng",
                aiStrategy = "Combo dồn dập & Thích ứng né đòn",
                rewardCredits = 350,
                rewardNanites = 45,
                rewardXp = 260,
                tier = MatchTier.ADVANCED_BOT
            )
            MatchTier.LIVE_PVP_1V1 -> EnemyFighter(
                id = "pvp_player_$stage",
                name = "Đặc Vụ Ghost_K9 [Ares Syndicate]",
                title = "Đặc Vụ Xếp Hạng Kim Cương",
                avatarType = "BOSS",
                maxHp = 680 + (stage * 60),
                currentHp = 680 + (stage * 60),
                maxShield = 220 + (stage * 30),
                currentShield = 220 + (stage * 30),
                attackPower = 56 + (stage * 6),
                defensePower = 34 + (stage * 4),
                level = 8 + stage,
                weakness = "Bào mòn khiên năng lượng bằng Plasma & EMP",
                aiStrategy = "Chiến Thuật Người Chơi P2P Realtime",
                rewardCredits = 500,
                rewardNanites = 70,
                rewardXp = 400,
                tier = MatchTier.LIVE_PVP_1V1
            )
            MatchTier.LIVE_PVP_2V2 -> EnemyFighter(
                id = "pvp_duo_$stage",
                name = "Song Đấu: Neon & Titan [Cyber Vanguard]",
                title = "Tổ Đội 2v2 Bậc Thầy Lượng Tử",
                avatarType = "MECHA",
                maxHp = 920 + (stage * 80),
                currentHp = 920 + (stage * 80),
                maxShield = 320 + (stage * 40),
                currentShield = 320 + (stage * 40),
                attackPower = 64 + (stage * 7),
                defensePower = 40 + (stage * 5),
                level = 10 + stage,
                weakness = "Dùng Overdrive dồn sát thương diệt mục tiêu yếu trước",
                aiStrategy = "Phối Hợp Tấn Công Gọng Kìm 2v2",
                rewardCredits = 850,
                rewardNanites = 120,
                rewardXp = 650,
                tier = MatchTier.LIVE_PVP_2V2
            )
        }
    }

    fun generateNewEnemy(stage: Int): EnemyFighter {
        return generateEnemyForTier(MatchTier.ROOKIE_BOT, stage)
    }
}
