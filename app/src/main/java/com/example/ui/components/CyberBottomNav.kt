package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberPalette
import com.example.viewmodel.MainNavTab

@Composable
fun CyberBottomNav(
    currentTab: MainNavTab,
    palette: CyberPalette,
    onTabSelected: (MainNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(palette.surface.copy(alpha = 0.96f))
            .border(width = 1.dp, color = palette.borderGlow.copy(alpha = 0.3f))
            .navigationBarsPadding() // Mandatory safe insets padding
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag("cyber_bottom_nav")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MainNavTab.values().forEach { tab ->
                val isSelected = (tab == currentTab)
                val icon = when (tab) {
                    MainNavTab.ARENA -> Icons.Default.FlashOn
                    MainNavTab.PVP_LOBBY -> Icons.Default.SportsEsports
                    MainNavTab.SKILL_TREE -> Icons.Default.AccountTree
                    MainNavTab.ARSENAL -> Icons.Default.Inventory2
                    MainNavTab.MARKETPLACE -> Icons.Default.Storefront
                    MainNavTab.SYNDICATE_RAID -> Icons.Default.Shield
                    MainNavTab.SETTINGS -> Icons.Default.Settings
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) palette.primary.copy(alpha = 0.2f) else Color.Transparent)
                        .clickable { onTabSelected(tab) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("nav_tab_${tab.name.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = icon,
                            contentDescription = tab.label,
                            tint = if (isSelected) palette.primary else palette.onSurface.copy(alpha = 0.5f),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = tab.label,
                            color = if (isSelected) palette.primary else palette.onSurface.copy(alpha = 0.5f),
                            fontSize = 9.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}
