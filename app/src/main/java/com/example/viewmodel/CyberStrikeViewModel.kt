package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.ai.GeminiAiService
import com.example.anticheat.AntiCheatEngine
import com.example.audio.SoundEngine
import com.example.combat.ActionEffectType
import com.example.combat.CombatEngine
import com.example.data.BattleHistoryEntity
import com.example.data.CloudSyncManager
import com.example.data.CloudSyncStatus
import com.example.data.CyberDatabase
import com.example.data.InventoryItemEntity
import com.example.data.PlayerEntity
import com.example.model.AntiCheatReport
import com.example.model.ArsenalCatalog
import com.example.model.CombatAction
import com.example.model.CombatLog
import com.example.model.CyberThemeMode
import com.example.model.EnemyFighter
import com.example.model.FighterStats
import com.example.model.GameItem
import com.example.model.ItemRarity
import com.example.model.ItemType
import com.example.model.LogType
import com.example.model.PerformanceMode
import com.example.model.RealWorldSensorData
import com.example.sensors.RealWorldSensorManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

import com.example.model.ChatMessage
import com.example.model.CompanionDrone
import com.example.model.DailyBounty
import com.example.model.EloRank
import com.example.model.EquipmentSet
import com.example.model.MarketplaceItem
import com.example.model.MasteryAchievement
import com.example.model.MatchTier
import com.example.model.PostMatchReport
import com.example.model.RpgCatalog
import com.example.model.SkillNode
import com.example.model.SocketChip
import com.example.model.SpectatorMatch
import com.example.model.SyndicateGuild
import com.example.model.WorldRaidBoss
import com.example.model.CraftingRecipe

enum class MainNavTab(val label: String) {
    ARENA("Đấu Trường"),
    PVP_LOBBY("PVP Mạng"),
    SKILL_TREE("Kỹ Năng"),
    ARSENAL("Kho & Rèn"),
    MARKETPLACE("Chợ MoMo"),
    SYNDICATE_RAID("Bang & Boss"),
    SETTINGS("Hệ Thống")
}

data class DamageIndicator(
    val id: Long = System.currentTimeMillis() + (0..9999).random(),
    val amount: Int,
    val isCrit: Boolean,
    val isPlayerTarget: Boolean
)

class CyberStrikeViewModel(application: Application) : AndroidViewModel(application) {

    val soundEngine = SoundEngine()
    val antiCheat = AntiCheatEngine()
    val sensorManager = RealWorldSensorManager(application)
    val cloudSync = CloudSyncManager(application)
    val aiService = GeminiAiService()
    private val combatEngine = CombatEngine(soundEngine, antiCheat)

    private val db = Room.databaseBuilder(
        application,
        CyberDatabase::class.java,
        "cyber_strike_neural.db"
    ).build()

    // UI States
    private val _currentTab = MutableStateFlow(MainNavTab.ARENA)
    val currentTab: StateFlow<MainNavTab> = _currentTab.asStateFlow()

    private val _themeMode = MutableStateFlow(CyberThemeMode.NEON_CYAN)
    val themeMode: StateFlow<CyberThemeMode> = _themeMode.asStateFlow()

    private val _performanceMode = MutableStateFlow(PerformanceMode.BALANCED)
    val performanceMode: StateFlow<PerformanceMode> = _performanceMode.asStateFlow()

    private val _playerStats = MutableStateFlow(FighterStats())
    val playerStats: StateFlow<FighterStats> = _playerStats.asStateFlow()

    private val _inventory = MutableStateFlow<List<GameItem>>(ArsenalCatalog.defaultItems)
    val inventory: StateFlow<List<GameItem>> = _inventory.asStateFlow()

    private val _currentStage = MutableStateFlow(1)
    val currentStage: StateFlow<Int> = _currentStage.asStateFlow()

    private val _currentEnemy = MutableStateFlow(combatEngine.generateNewEnemy(1))
    val currentEnemy: StateFlow<EnemyFighter> = _currentEnemy.asStateFlow()

    private val _combatLogs = MutableStateFlow<List<CombatLog>>(emptyList())
    val combatLogs: StateFlow<List<CombatLog>> = _combatLogs.asStateFlow()

    private val _activeEffect = MutableStateFlow(ActionEffectType.NONE)
    val activeEffect: StateFlow<ActionEffectType> = _activeEffect.asStateFlow()

    private val _damageIndicators = MutableStateFlow<List<DamageIndicator>>(emptyList())
    val damageIndicators: StateFlow<List<DamageIndicator>> = _damageIndicators.asStateFlow()

    private val _screenShakeTrigger = MutableStateFlow(0)
    val screenShakeTrigger: StateFlow<Int> = _screenShakeTrigger.asStateFlow()

    private val _aiTacticalAdvice = MutableStateFlow("NEURAL ADVISOR ONLINE: Scan complete. Weapons armed and synchronized.")
    val aiTacticalAdvice: StateFlow<String> = _aiTacticalAdvice.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    private val _antiCheatReport = MutableStateFlow(AntiCheatReport())
    val antiCheatReport: StateFlow<AntiCheatReport> = _antiCheatReport.asStateFlow()

    // PVP & Match Tier
    private val _matchTier = MutableStateFlow(MatchTier.ROOKIE_BOT)
    val matchTier: StateFlow<MatchTier> = _matchTier.asStateFlow()

    private val _eloRating = MutableStateFlow(1420)
    val eloRating: StateFlow<Int> = _eloRating.asStateFlow()

    private val _actionHistory = MutableStateFlow<List<CombatAction>>(emptyList())
    val actionHistory: StateFlow<List<CombatAction>> = _actionHistory.asStateFlow()

    // RPG Progression & Skill Tree
    private val _skillNodes = MutableStateFlow(RpgCatalog.defaultSkillNodes)
    val skillNodes: StateFlow<List<SkillNode>> = _skillNodes.asStateFlow()

    private val _skillPoints = MutableStateFlow(3)
    val skillPoints: StateFlow<Int> = _skillPoints.asStateFlow()

    private val _socketChips = MutableStateFlow(RpgCatalog.defaultSocketChips)
    val socketChips: StateFlow<List<SocketChip>> = _socketChips.asStateFlow()

    private val _equipmentSets = MutableStateFlow(RpgCatalog.defaultEquipmentSets)
    val equipmentSets: StateFlow<List<EquipmentSet>> = _equipmentSets.asStateFlow()

    private val _dailyBounties = MutableStateFlow(RpgCatalog.defaultDailyBounties)
    val dailyBounties: StateFlow<List<DailyBounty>> = _dailyBounties.asStateFlow()

    private val _achievements = MutableStateFlow(RpgCatalog.defaultAchievements)
    val achievements: StateFlow<List<MasteryAchievement>> = _achievements.asStateFlow()

    // Multiplayer, Syndicate & World Raid
    private val _syndicate = MutableStateFlow(RpgCatalog.defaultSyndicate)
    val syndicate: StateFlow<SyndicateGuild> = _syndicate.asStateFlow()

    private val _raidBoss = MutableStateFlow(RpgCatalog.defaultRaidBoss)
    val raidBoss: StateFlow<WorldRaidBoss> = _raidBoss.asStateFlow()

    private val _marketplaceListings = MutableStateFlow(RpgCatalog.defaultMarketplaceItems)
    val marketplaceListings: StateFlow<List<MarketplaceItem>> = _marketplaceListings.asStateFlow()

    private val _selectedMomoItem = MutableStateFlow<MarketplaceItem?>(null)
    val selectedMomoItem: StateFlow<MarketplaceItem?> = _selectedMomoItem.asStateFlow()

    private val _momoSuccessNotification = MutableStateFlow<String?>(null)
    val momoSuccessNotification: StateFlow<String?> = _momoSuccessNotification.asStateFlow()

    private val _chatMessages = MutableStateFlow(
        listOf(
            ChatMessage("c1", "System", "BOT", "Chào mừng đặc vụ đến với Chiến Trường Lượng Tử Realtime!", "10:00", isSystem = true),
            ChatMessage("c2", "Ghost_K9", "Kim Cương", "Ai solo 1v1 phòng riêng không? Đang test build Plasma mới.", "10:02"),
            ChatMessage("c3", "Cipher_Zero", "Bạch Kim", "Tối nay bang Ares mở Raid Boss ARES-TITAN lúc 20:00 nhé anh em!", "10:05", isSyndicate = true)
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _spectatorMatches = MutableStateFlow(
        listOf(
            SpectatorMatch("s1", "Valkyrie_Top1", "Shadow_Lord", 0.72f, 0.45f, 128, 3, "Chung Kết Kim Cương 1v1"),
            SpectatorMatch("s2", "Neon_Blade", "Cyber_Titan", 0.38f, 0.82f, 94, 2, "Bán Kết 2v2 Đồng Đội")
        )
    )
    val spectatorMatches: StateFlow<List<SpectatorMatch>> = _spectatorMatches.asStateFlow()

    private val _currentTowerFloor = MutableStateFlow(1)
    val currentTowerFloor: StateFlow<Int> = _currentTowerFloor.asStateFlow()

    private val _companionDrone = MutableStateFlow(CompanionDrone())
    val companionDrone: StateFlow<CompanionDrone> = _companionDrone.asStateFlow()

    private val _postMatchReport = MutableStateFlow<PostMatchReport?>(null)
    val postMatchReport: StateFlow<PostMatchReport?> = _postMatchReport.asStateFlow()

    // Hardware & Energy efficiency
    private val _isNightVisionActive = MutableStateFlow(false)
    val isNightVisionActive: StateFlow<Boolean> = _isNightVisionActive.asStateFlow()

    private val _isThermalThrottlingActive = MutableStateFlow(false)
    val isThermalThrottlingActive: StateFlow<Boolean> = _isThermalThrottlingActive.asStateFlow()

    private val _isOledTrueBlackEnabled = MutableStateFlow(false)
    val isOledTrueBlackEnabled: StateFlow<Boolean> = _isOledTrueBlackEnabled.asStateFlow()

    private val _craftingRecipes = MutableStateFlow(
        listOf(
            CraftingRecipe("craft_plasma", "Đúc Pháo Plasma Tối Thượng", 400, 30, ArsenalCatalog.defaultItems[1]),
            CraftingRecipe("craft_disrupter", "Chế Tạo Súng Sốc Điện Arc", 650, 45, ArsenalCatalog.defaultItems[2]),
            CraftingRecipe("craft_titan", "Gia Cố Giáp Hạng Nặng Ares", 550, 40, ArsenalCatalog.defaultItems[4])
        )
    )
    val craftingRecipes: StateFlow<List<CraftingRecipe>> = _craftingRecipes.asStateFlow()

    val currentTier: StateFlow<MatchTier> get() = matchTier
    val syndicateGuild: StateFlow<SyndicateGuild> get() = syndicate
    val worldRaidBoss: StateFlow<WorldRaidBoss> get() = raidBoss
    val isOledBlack: StateFlow<Boolean> get() = isOledTrueBlackEnabled

    private var storedSignature: String = ""

    init {
        sensorManager.startListening()
        cloudSync.init()

        // Setup physical shake to activate instant counter-shield
        sensorManager.onPhysicalShakeDetected = {
            if (_playerStats.value.currentEnergy >= 15) {
                performCombatAction(CombatAction.NANO_SHIELD)
            }
        }

        // Load local persistence and verify signature
        viewModelScope.launch {
            loadPersistedData()
            runAntiCheatScan()
            cloudSync.triggerAutoSync()
        }
    }

    override fun onCleared() {
        super.onCleared()
        sensorManager.stopListening()
        cloudSync.cleanup()
    }

    private suspend fun loadPersistedData() {
        val profile = db.cyberDao().getPlayerProfileSync()
        if (profile != null) {
            _playerStats.value = FighterStats(
                maxHp = profile.maxHp,
                currentHp = profile.currentHp,
                maxShield = profile.maxShield,
                currentShield = profile.currentShield,
                baseAttack = profile.baseAttack,
                baseDefense = profile.baseDefense,
                level = profile.level,
                currentXp = profile.currentXp,
                credits = profile.credits,
                nanites = profile.nanites
            )
            storedSignature = profile.stateSignature
        } else {
            // First run: save default state
            storedSignature = antiCheat.generateStateSignature(_playerStats.value)
            saveCurrentPlayerProfile()
        }

        val dbItems = db.cyberDao().getAllInventorySync()
        if (dbItems.isNotEmpty()) {
            val loadedItems = dbItems.map { entity ->
                val base = ArsenalCatalog.defaultItems.find { it.id == entity.itemId }
                GameItem(
                    id = entity.itemId,
                    name = entity.name,
                    description = base?.description ?: "",
                    type = try { ItemType.valueOf(entity.type) } catch (_: Exception) { ItemType.WEAPON },
                    rarity = try { ItemRarity.valueOf(entity.rarity) } catch (_: Exception) { ItemRarity.COMMON },
                    attackBonus = entity.attackBonus,
                    defenseBonus = entity.defenseBonus,
                    shieldBonus = entity.shieldBonus,
                    critBonusPercent = entity.critBonusPercent,
                    priceCredits = base?.priceCredits ?: 100,
                    isEquipped = entity.isEquipped,
                    upgradeLevel = entity.upgradeLevel
                )
            }
            _inventory.value = loadedItems
        } else {
            // Persist defaults
            persistInventory(_inventory.value)
        }
    }

    private fun saveCurrentPlayerProfile() {
        viewModelScope.launch {
            storedSignature = antiCheat.generateStateSignature(_playerStats.value)
            val entity = PlayerEntity(
                id = 1,
                level = _playerStats.value.level,
                currentXp = _playerStats.value.currentXp,
                credits = _playerStats.value.credits,
                nanites = _playerStats.value.nanites,
                maxHp = _playerStats.value.maxHp,
                currentHp = _playerStats.value.currentHp,
                maxShield = _playerStats.value.maxShield,
                currentShield = _playerStats.value.currentShield,
                baseAttack = _playerStats.value.baseAttack,
                baseDefense = _playerStats.value.baseDefense,
                stateSignature = storedSignature,
                lastSyncTimestamp = System.currentTimeMillis()
            )
            db.cyberDao().savePlayerProfile(entity)
        }
    }

    private fun persistInventory(items: List<GameItem>) {
        viewModelScope.launch {
            val entities = items.map {
                InventoryItemEntity(
                    itemId = it.id,
                    name = it.name,
                    type = it.type.name,
                    rarity = it.rarity.name,
                    attackBonus = it.attackBonus,
                    defenseBonus = it.defenseBonus,
                    shieldBonus = it.shieldBonus,
                    critBonusPercent = it.critBonusPercent,
                    upgradeLevel = it.upgradeLevel,
                    isEquipped = it.isEquipped
                )
            }
            db.cyberDao().saveInventoryItems(entities)
        }
    }

    fun setNavTab(tab: MainNavTab) {
        soundEngine.playUiBlip()
        _currentTab.value = tab
    }

    fun setThemeMode(mode: CyberThemeMode) {
        soundEngine.playUiBlip()
        _themeMode.value = mode
    }

    fun setPerformanceMode(mode: PerformanceMode) {
        soundEngine.playUiBlip()
        _performanceMode.value = mode
    }

    fun toggleSound(enabled: Boolean) {
        soundEngine.isSoundEnabled = enabled
    }

    fun performCombatAction(action: CombatAction) {
        viewModelScope.launch {
            val sensorData = sensorManager.sensorData.value
            val result = combatEngine.executePlayerTurn(
                action = action,
                currentStats = _playerStats.value,
                enemy = _currentEnemy.value,
                sensorData = sensorData,
                equippedItems = _inventory.value,
                stateSignature = storedSignature
            )

            // Update combat stats & enemy
            _playerStats.value = result.playerStats
            _currentEnemy.value = result.enemy
            _combatLogs.value = (result.logs + _combatLogs.value).take(40)
            _activeEffect.value = result.actionEffectType

            // Floating damage numbers
            if (result.damageDealt > 0) {
                val dmg = DamageIndicator(amount = result.damageDealt, isCrit = result.isPlayerCrit, isPlayerTarget = false)
                _damageIndicators.value = _damageIndicators.value + dmg
            }
            if (result.damageTaken > 0) {
                val dmg = DamageIndicator(amount = result.damageTaken, isCrit = result.isEnemyCrit, isPlayerTarget = true)
                _damageIndicators.value = _damageIndicators.value + dmg
                _screenShakeTrigger.value = _screenShakeTrigger.value + 1
            }

            // Consume kinetic charge if used
            if (sensorData.kineticEnergyCharge > 5f) {
                sensorManager.consumeKineticCharge()
            }

            // Save and sync
            saveCurrentPlayerProfile()
            cloudSync.triggerAutoSync()

            // Update Post Match Report and ELO if match ended
            if (result.postMatchReport != null) {
                _postMatchReport.value = result.postMatchReport
                val newElo = (_eloRating.value + result.postMatchReport.eloChange).coerceAtLeast(1000)
                _eloRating.value = newElo
            }

            // Check victory
            if (result.isVictory) {
                db.cyberDao().insertBattleRecord(
                    BattleHistoryEntity(
                        enemyName = result.enemy.name,
                        outcome = "VICTORY",
                        creditsGained = result.enemy.rewardCredits,
                        xpGained = result.enemy.rewardXp
                    )
                )

                // Update daily bounties progress
                _dailyBounties.value = _dailyBounties.value.map { b ->
                    if (b.id == "bounty_1") b.copy(currentProgress = (b.currentProgress + 1).coerceAtMost(b.maxProgress))
                    else if (b.id == "bounty_3" && result.isPlayerCrit) b.copy(currentProgress = (b.currentProgress + 1).coerceAtMost(b.maxProgress))
                    else b
                }

                delay(1200)
                _currentStage.value = _currentStage.value + 1
                _currentEnemy.value = combatEngine.generateEnemyForTier(_matchTier.value, _currentStage.value)
            }

            // Trigger Tactical AI Advice update
            requestAiTacticalDirective(action.title)

            // Reset visual effect after delay
            delay(400)
            _activeEffect.value = ActionEffectType.NONE
        }
    }

    fun selectMatchTier(tier: MatchTier) {
        soundEngine.playUiBlip()
        _matchTier.value = tier
        _currentEnemy.value = combatEngine.generateEnemyForTier(tier, _currentStage.value)
        _combatLogs.value = listOf(
            CombatLog(
                text = "HỆ THỐNG GHÉP ĐẤU: Đã chuyển sang phân cấp ${tier.name}. Đối thủ mới đã kết nối!",
                type = LogType.TACTICAL_INTEL
            )
        ) + _combatLogs.value
    }

    fun unlockSkillNode(nodeId: String) {
        if (_skillPoints.value <= 0) return
        val node = _skillNodes.value.find { it.id == nodeId } ?: return
        if (node.isUnlocked) return

        soundEngine.playOverdriveUltimate()
        _skillPoints.value = _skillPoints.value - 1
        _skillNodes.value = _skillNodes.value.map {
            if (it.id == nodeId) it.copy(isUnlocked = true) else it
        }

        // Apply stat bonuses
        _playerStats.value = _playerStats.value.copy(
            baseAttack = _playerStats.value.baseAttack + node.bonusAttack,
            baseDefense = _playerStats.value.baseDefense + node.bonusDefense,
            maxShield = _playerStats.value.maxShield + node.bonusShield,
            baseCritRate = _playerStats.value.baseCritRate + node.bonusCritRate
        )
        saveCurrentPlayerProfile()
    }

    fun socketChipToItem(chipId: String, itemId: String) {
        val chip = _socketChips.value.find { it.id == chipId } ?: return
        val item = _inventory.value.find { it.id == itemId } ?: return

        soundEngine.playOverdriveUltimate()
        _inventory.value = _inventory.value.map {
            if (it.id == itemId) {
                it.copy(
                    attackBonus = it.attackBonus + chip.attackBonus,
                    defenseBonus = it.defenseBonus + chip.defenseBonus,
                    description = "${it.description} [Đã khảm: ${chip.name}]"
                )
            } else it
        }
        persistInventory(_inventory.value)
    }

    fun craftCustomWeapon(name: String, branch: String) {
        val costNanites = 40
        val costCredits = 150
        if (_playerStats.value.nanites >= costNanites && _playerStats.value.credits >= costCredits) {
            soundEngine.playOverdriveUltimate()
            _playerStats.value = _playerStats.value.copy(
                nanites = _playerStats.value.nanites - costNanites,
                credits = _playerStats.value.credits - costCredits
            )
            val crafted = GameItem(
                id = "craft_${System.currentTimeMillis()}",
                name = name.ifBlank { "Vũ Khí Lượng Tử Tự Chế" },
                description = "Vũ khí chế tạo thủ công nhánh $branch với vi mạch nano siêu dẫn.",
                type = ItemType.WEAPON,
                rarity = ItemRarity.EPIC,
                attackBonus = 35 + (0..15).random(),
                shieldBonus = 20,
                critBonusPercent = 0.15f,
                priceCredits = 300,
                isEquipped = false
            )
            _inventory.value = _inventory.value + crafted
            persistInventory(_inventory.value)
            saveCurrentPlayerProfile()
        }
    }

    fun attackWorldRaidBoss(damage: Long) {
        soundEngine.playCritExplosion()
        val current = _raidBoss.value
        val newHp = (current.currentHp - damage).coerceAtLeast(0L)
        _raidBoss.value = current.copy(
            currentHp = newHp,
            participantsCount = current.participantsCount + 1
        )
        _playerStats.value = _playerStats.value.copy(
            credits = _playerStats.value.credits + 80,
            nanites = _playerStats.value.nanites + 15
        )
        saveCurrentPlayerProfile()
    }

    fun sendChatMessage(text: String, isSyndicate: Boolean = false) {
        if (text.isBlank()) return
        soundEngine.playUiBlip()
        val msg = ChatMessage(
            id = "msg_${System.currentTimeMillis()}",
            senderName = "Bạn (Operative)",
            rankTitle = "Kim Cương",
            content = text,
            timestamp = "Vừa xong",
            isSyndicate = isSyndicate
        )
        _chatMessages.value = _chatMessages.value + msg
    }

    fun initiateMomoPayment(item: MarketplaceItem) {
        soundEngine.playUiBlip()
        _selectedMomoItem.value = item
    }

    fun dismissMomoDialog() {
        _selectedMomoItem.value = null
    }

    fun confirmMomoPaymentSuccess(item: MarketplaceItem) {
        soundEngine.playVictoryFanfare()
        _inventory.value = _inventory.value + item.item
        persistInventory(_inventory.value)
        _selectedMomoItem.value = null
        _momoSuccessNotification.value = "Giao dịch MoMo (${item.momoPhoneTarget}) thành công! Đã nhận: ${item.item.name}"
    }

    fun dismissMomoNotification() {
        _momoSuccessNotification.value = null
    }

    fun purchaseMarketplaceWithCredits(item: MarketplaceItem) {
        if (_playerStats.value.credits >= item.priceCredits) {
            soundEngine.playVictoryFanfare()
            _playerStats.value = _playerStats.value.copy(credits = _playerStats.value.credits - item.priceCredits)
            _inventory.value = _inventory.value + item.item
            persistInventory(_inventory.value)
            saveCurrentPlayerProfile()
            _momoSuccessNotification.value = "Đã mua ${item.item.name} bằng ${item.priceCredits} Credits!"
        }
    }

    fun claimBountyReward(bountyId: String) {
        val bounty = _dailyBounties.value.find { it.id == bountyId } ?: return
        if (bounty.isClaimed || bounty.currentProgress < bounty.maxProgress) return

        soundEngine.playVictoryFanfare()
        _playerStats.value = _playerStats.value.copy(
            credits = _playerStats.value.credits + bounty.rewardCredits,
            nanites = _playerStats.value.nanites + bounty.rewardNanites
        )
        _dailyBounties.value = _dailyBounties.value.map {
            if (it.id == bountyId) it.copy(isClaimed = true) else it
        }
        saveCurrentPlayerProfile()
    }

    fun advanceTowerFloor() {
        soundEngine.playBladeSlash()
        val nextFloor = _currentTowerFloor.value + 1
        _currentTowerFloor.value = nextFloor
        _currentEnemy.value = combatEngine.generateEnemyForTier(MatchTier.ADVANCED_BOT, nextFloor)
        _combatLogs.value = listOf(
            CombatLog(
                text = "THÁP THẦN KINH INFINITE: Đã tiến lên Tầng $nextFloor. Kẻ gác cổng mới xuất hiện!",
                type = LogType.TACTICAL_INTEL
            )
        ) + _combatLogs.value
    }

    fun toggleOledTrueBlack(enabled: Boolean) {
        soundEngine.playUiBlip()
        _isOledTrueBlackEnabled.value = enabled
    }

    fun toggleOledBlack(enabled: Boolean) {
        toggleOledTrueBlack(enabled)
    }

    fun startMatchmaking1v1() {
        selectMatchTier(MatchTier.LIVE_PVP_1V1)
        setNavTab(MainNavTab.ARENA)
    }

    fun startMatchmaking2v2() {
        selectMatchTier(MatchTier.LIVE_PVP_2V2)
        setNavTab(MainNavTab.ARENA)
    }

    fun craftItem(recipeId: String) {
        val recipe = _craftingRecipes.value.find { it.id == recipeId } ?: return
        if (_playerStats.value.credits >= recipe.costCredits && _playerStats.value.nanites >= recipe.costNanites) {
            soundEngine.playOverdriveUltimate()
            _playerStats.value = _playerStats.value.copy(
                credits = _playerStats.value.credits - recipe.costCredits,
                nanites = _playerStats.value.nanites - recipe.costNanites
            )
            val newItem = recipe.resultItem.copy(id = "crafted_${System.currentTimeMillis()}")
            _inventory.value = _inventory.value + newItem
            persistInventory(_inventory.value)
            saveCurrentPlayerProfile()
        }
    }

    fun buyMarketplaceItemCredits(item: MarketplaceItem) {
        purchaseMarketplaceWithCredits(item)
    }

    fun confirmMomoPaymentCompleted(item: MarketplaceItem) {
        confirmMomoPaymentSuccess(item)
    }

    fun dismissMomoPayment() {
        dismissMomoDialog()
    }

    fun claimBounty(bountyId: String) {
        claimBountyReward(bountyId)
    }

    fun toggleNightVision(enabled: Boolean) {
        soundEngine.playUiBlip()
        _isNightVisionActive.value = enabled
    }

    fun toggleThermalGuard(enabled: Boolean) {
        soundEngine.playUiBlip()
        _isThermalThrottlingActive.value = enabled
    }

    fun toggleBgm(enabled: Boolean) {
        if (enabled) soundEngine.startDynamicSynthwaveBgm()
        else soundEngine.stopDynamicSynthwaveBgm()
    }

    fun dismissPostMatchReport() {
        _postMatchReport.value = null
    }

    fun requestAiTacticalDirective(recentAction: String = "Combat Engage") {
        viewModelScope.launch {
            _isAiThinking.value = true
            val advice = aiService.getCombatTacticalAdvice(
                playerStats = _playerStats.value,
                enemy = _currentEnemy.value,
                sensorData = sensorManager.sensorData.value,
                recentAction = recentAction
            )
            _aiTacticalAdvice.value = advice
            _isAiThinking.value = false
        }
    }

    fun runAntiCheatScan() {
        soundEngine.playUiBlip()
        val report = antiCheat.runFullIntegrityScan(_playerStats.value, storedSignature)
        _antiCheatReport.value = report
    }

    fun equipItem(item: GameItem) {
        soundEngine.playBladeSlash()
        val updated = _inventory.value.map { current ->
            if (current.type == item.type) {
                if (current.id == item.id) current.copy(isEquipped = !current.isEquipped)
                else current.copy(isEquipped = false) // Unequip other in same slot
            } else {
                current
            }
        }
        _inventory.value = updated
        persistInventory(updated)
    }

    fun upgradeItem(item: GameItem) {
        val costNanites = item.upgradeLevel * 10
        val costCredits = item.upgradeLevel * 50
        if (_playerStats.value.nanites >= costNanites && _playerStats.value.credits >= costCredits) {
            soundEngine.playOverdriveUltimate()
            _playerStats.value = _playerStats.value.copy(
                nanites = _playerStats.value.nanites - costNanites,
                credits = _playerStats.value.credits - costCredits
            )
            val updated = _inventory.value.map { current ->
                if (current.id == item.id) {
                    current.copy(
                        upgradeLevel = current.upgradeLevel + 1,
                        attackBonus = if (current.attackBonus > 0) current.attackBonus + 6 else 0,
                        defenseBonus = if (current.defenseBonus > 0) current.defenseBonus + 5 else 0,
                        shieldBonus = if (current.shieldBonus > 0) current.shieldBonus + 15 else 0
                    )
                } else current
            }
            _inventory.value = updated
            persistInventory(updated)
            saveCurrentPlayerProfile()
        }
    }

    fun openLootCrate(): GameItem? {
        val costCredits = 150
        if (_playerStats.value.credits >= costCredits) {
            soundEngine.playOverdriveUltimate()
            _playerStats.value = _playerStats.value.copy(credits = _playerStats.value.credits - costCredits)

            val unowned = ArsenalCatalog.defaultItems.filter { defaultItem ->
                _inventory.value.none { it.id == defaultItem.id }
            }
            val awardItem = unowned.randomOrNull() ?: ArsenalCatalog.defaultItems.random().copy(
                id = "loot_${System.currentTimeMillis()}",
                name = "Overclocked Variant",
                attackBonus = (15..40).random(),
                defenseBonus = (10..30).random()
            )

            val updated = _inventory.value + awardItem
            _inventory.value = updated
            persistInventory(updated)
            saveCurrentPlayerProfile()
            return awardItem
        }
        return null
    }

    fun scanGeoSector(): String {
        soundEngine.playLaserShoot()
        val sector = sensorManager.scanRealWorldGeoSector()
        val bonusNanites = (10..35).random()
        val bonusCredits = (40..120).random()

        _playerStats.value = _playerStats.value.copy(
            credits = _playerStats.value.credits + bonusCredits,
            nanites = _playerStats.value.nanites + bonusNanites
        )
        _combatLogs.value = listOf(
            CombatLog(
                text = "GEO-RADAR: Scanned $sector. Uncovered +$bonusNanites Nanites and +$bonusCredits Credits!",
                type = LogType.SENSOR_TRIGGER
            )
        ) + _combatLogs.value
        saveCurrentPlayerProfile()
        return sector
    }

    fun exportEncryptedBackupSnapshot(): String {
        val stats = _playerStats.value
        val entity = PlayerEntity(
            id = 1,
            level = stats.level,
            currentXp = stats.currentXp,
            credits = stats.credits,
            nanites = stats.nanites,
            maxHp = stats.maxHp,
            currentHp = stats.currentHp,
            maxShield = stats.maxShield,
            currentShield = stats.currentShield,
            baseAttack = stats.baseAttack,
            baseDefense = stats.baseDefense,
            stateSignature = storedSignature,
            lastSyncTimestamp = System.currentTimeMillis()
        )
        val items = _inventory.value.map {
            InventoryItemEntity(
                itemId = it.id,
                name = it.name,
                type = it.type.name,
                rarity = it.rarity.name,
                attackBonus = it.attackBonus,
                defenseBonus = it.defenseBonus,
                shieldBonus = it.shieldBonus,
                critBonusPercent = it.critBonusPercent,
                upgradeLevel = it.upgradeLevel,
                isEquipped = it.isEquipped
            )
        }
        return cloudSync.exportEncryptedBackupPayload(entity, items)
    }
}
