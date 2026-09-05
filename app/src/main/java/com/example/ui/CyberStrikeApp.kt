package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.CombatArenaView
import com.example.ui.components.CyberBottomNav
import com.example.ui.components.InventoryArsenalView
import com.example.ui.components.MarketplaceView
import com.example.ui.components.PvpLobbyView
import com.example.ui.components.SettingsView
import com.example.ui.components.SkillTreeView
import com.example.ui.components.SyndicateRaidView
import com.example.ui.components.TopStatusBar
import com.example.ui.theme.CyberStrikeTheme
import com.example.ui.theme.getCyberPalette
import com.example.viewmodel.CyberStrikeViewModel
import com.example.viewmodel.MainNavTab

@Composable
fun CyberStrikeApp(
    viewModel: CyberStrikeViewModel = viewModel()
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val palette = getCyberPalette(themeMode)

    val currentTab by viewModel.currentTab.collectAsState()
    val playerStats by viewModel.playerStats.collectAsState()
    val inventory by viewModel.inventory.collectAsState()
    val currentStage by viewModel.currentStage.collectAsState()
    val currentEnemy by viewModel.currentEnemy.collectAsState()
    val combatLogs by viewModel.combatLogs.collectAsState()
    val activeEffect by viewModel.activeEffect.collectAsState()
    val damageIndicators by viewModel.damageIndicators.collectAsState()
    val screenShakeTrigger by viewModel.screenShakeTrigger.collectAsState()

    val currentTier by viewModel.currentTier.collectAsState()
    val postMatchReport by viewModel.postMatchReport.collectAsState()
    val eloRating by viewModel.eloRating.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val spectatorMatches by viewModel.spectatorMatches.collectAsState()
    val skillNodes by viewModel.skillNodes.collectAsState()
    val skillPoints by viewModel.skillPoints.collectAsState()
    val marketplaceListings by viewModel.marketplaceListings.collectAsState()
    val selectedMomoItem by viewModel.selectedMomoItem.collectAsState()
    val syndicateGuild by viewModel.syndicateGuild.collectAsState()
    val worldRaidBoss by viewModel.worldRaidBoss.collectAsState()
    val dailyBounties by viewModel.dailyBounties.collectAsState()
    val achievements by viewModel.achievements.collectAsState()
    val socketChips by viewModel.socketChips.collectAsState()
    val craftingRecipes by viewModel.craftingRecipes.collectAsState()
    val equipmentSets by viewModel.equipmentSets.collectAsState()
    val isOledBlack by viewModel.isOledBlack.collectAsState()

    val sensorData by viewModel.sensorManager.sensorData.collectAsState()
    val cloudSyncStatus by viewModel.cloudSync.syncStatus.collectAsState()
    val antiCheatReport by viewModel.antiCheatReport.collectAsState()
    val perfMode by viewModel.performanceMode.collectAsState()

    CyberStrikeTheme(mode = themeMode) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isOledBlack) androidx.compose.ui.graphics.Color.Black else palette.background)
                .testTag("cyber_strike_root"),
            topBar = {
                Column(modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)) {
                    TopStatusBar(
                        stats = playerStats,
                        cloudSyncStatus = cloudSyncStatus,
                        antiCheatReport = antiCheatReport,
                        palette = palette,
                        onSecurityClick = { viewModel.setNavTab(MainNavTab.SETTINGS) },
                        onSyncClick = { viewModel.setNavTab(MainNavTab.SETTINGS) }
                    )
                }
            },
            bottomBar = {
                CyberBottomNav(
                    currentTab = currentTab,
                    palette = palette,
                    onTabSelected = { viewModel.setNavTab(it) }
                )
            },
            containerColor = if (isOledBlack) androidx.compose.ui.graphics.Color.Black else palette.background
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(if (isOledBlack) androidx.compose.ui.graphics.Color.Black else palette.background)
            ) {
                when (currentTab) {
                    MainNavTab.ARENA -> {
                        CombatArenaView(
                            stats = playerStats,
                            enemy = currentEnemy,
                            stage = currentStage,
                            sensorData = sensorData,
                            combatLogs = combatLogs,
                            activeEffect = activeEffect,
                            damageIndicators = damageIndicators,
                            screenShakeTrigger = screenShakeTrigger,
                            palette = palette,
                            onActionClick = { viewModel.performCombatAction(it) },
                            matchTier = currentTier,
                            onSelectTier = { viewModel.selectMatchTier(it) },
                            postMatchReport = postMatchReport,
                            onDismissPostMatchReport = { viewModel.dismissPostMatchReport() }
                        )
                    }
                    MainNavTab.PVP_LOBBY -> {
                        PvpLobbyView(
                            eloRating = eloRating,
                            chatMessages = chatMessages,
                            spectatorMatches = spectatorMatches,
                            palette = palette,
                            onStartPvp1v1 = { viewModel.startMatchmaking1v1() },
                            onStartPvp2v2 = { viewModel.startMatchmaking2v2() },
                            onSendMessage = { viewModel.sendChatMessage(it) }
                        )
                    }
                    MainNavTab.SKILL_TREE -> {
                        SkillTreeView(
                            skillNodes = skillNodes,
                            skillPoints = skillPoints,
                            palette = palette,
                            onUnlockNode = { viewModel.unlockSkillNode(it) }
                        )
                    }
                    MainNavTab.ARSENAL -> {
                        InventoryArsenalView(
                            items = inventory,
                            playerStats = playerStats,
                            palette = palette,
                            onEquipToggle = { viewModel.equipItem(it) },
                            onUpgradeItem = { viewModel.upgradeItem(it) },
                            onOpenLootCrate = { viewModel.openLootCrate() },
                            chips = socketChips,
                            craftingRecipes = craftingRecipes,
                            equipmentSets = equipmentSets,
                            onSocketChip = { chipId, itemId -> viewModel.socketChipToItem(chipId, itemId) },
                            onCraftRecipe = { viewModel.craftItem(it) }
                        )
                    }
                    MainNavTab.MARKETPLACE -> {
                        MarketplaceView(
                            listings = marketplaceListings,
                            playerCredits = playerStats.credits,
                            selectedMomoItem = selectedMomoItem,
                            palette = palette,
                            onBuyWithCredits = { viewModel.buyMarketplaceItemCredits(it) },
                            onInitiateMomo = { viewModel.initiateMomoPayment(it) },
                            onConfirmMomo = { viewModel.confirmMomoPaymentCompleted(it) },
                            onDismissMomo = { viewModel.dismissMomoPayment() }
                        )
                    }
                    MainNavTab.SYNDICATE_RAID -> {
                        SyndicateRaidView(
                            syndicate = syndicateGuild,
                            raidBoss = worldRaidBoss,
                            bounties = dailyBounties,
                            achievements = achievements,
                            palette = palette,
                            onAttackRaidBoss = { viewModel.attackWorldRaidBoss(it) },
                            onClaimBounty = { viewModel.claimBounty(it) }
                        )
                    }
                    MainNavTab.SETTINGS -> {
                        SettingsView(
                            currentTheme = themeMode,
                            currentPerfMode = perfMode,
                            cloudSyncStatus = cloudSyncStatus,
                            palette = palette,
                            isSoundEnabled = viewModel.soundEngine.isSoundEnabled,
                            onThemeChange = { viewModel.setThemeMode(it) },
                            onPerfModeChange = { viewModel.setPerformanceMode(it) },
                            onSoundToggle = { viewModel.toggleSound(it) },
                            onManualSync = { viewModel.cloudSync.triggerAutoSync() },
                            onExportBackup = { viewModel.exportEncryptedBackupSnapshot() },
                            isNightVisionAuto = true,
                            isThermalGuardEnabled = true,
                            isOledBlackEnabled = isOledBlack,
                            onNightVisionToggle = {},
                            onThermalGuardToggle = {},
                            onOledBlackToggle = { viewModel.toggleOledBlack(it) }
                        )
                    }
                }
            }
        }
    }
}

