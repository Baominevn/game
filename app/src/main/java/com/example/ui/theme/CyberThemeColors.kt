package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import com.example.model.CyberThemeMode

data class CyberPalette(
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val onPrimary: Color,
    val onBackground: Color,
    val onSurface: Color,
    val borderGlow: Color,
    val accentCrit: Color,
    val shieldColor: Color,
    val hpColor: Color,
    val energyColor: Color
) {
    val accent: Color get() = secondary
    val danger: Color get() = accentCrit
}

fun getCyberPalette(mode: CyberThemeMode): CyberPalette {
    return when (mode) {
        CyberThemeMode.NEON_CYAN -> CyberPalette(
            primary = Color(0xFF00E5FF),
            secondary = Color(0xFFFF007F),
            tertiary = Color(0xFF7C4DFF),
            background = Color(0xFF070B14),
            surface = Color(0xFF0F172A),
            surfaceVariant = Color(0xFF1E293B),
            onPrimary = Color(0xFF000000),
            onBackground = Color(0xFFF1F5F9),
            onSurface = Color(0xFFE2E8F0),
            borderGlow = Color(0xFF00E5FF),
            accentCrit = Color(0xFFFF1744),
            shieldColor = Color(0xFF00B0FF),
            hpColor = Color(0xFF00E676),
            energyColor = Color(0xFFFFD600)
        )
        CyberThemeMode.MATRIX_EMERALD -> CyberPalette(
            primary = Color(0xFF00FF66),
            secondary = Color(0xFF10B981),
            tertiary = Color(0xFF00E5FF),
            background = Color(0xFF040D07),
            surface = Color(0xFF0A1F13),
            surfaceVariant = Color(0xFF123321),
            onPrimary = Color(0xFF000000),
            onBackground = Color(0xFFE6F7ED),
            onSurface = Color(0xFFC7EBD5),
            borderGlow = Color(0xFF00FF66),
            accentCrit = Color(0xFFFF3D00),
            shieldColor = Color(0xFF00E5FF),
            hpColor = Color(0xFF00FF66),
            energyColor = Color(0xFFFFEA00)
        )
        CyberThemeMode.CRIMSON_OVERDRIVE -> CyberPalette(
            primary = Color(0xFFFF1744),
            secondary = Color(0xFFFF9100),
            tertiary = Color(0xFFFF5252),
            background = Color(0xFF0D0407),
            surface = Color(0xFF1F0B12),
            surfaceVariant = Color(0xFF33141E),
            onPrimary = Color(0xFFFFFFFF),
            onBackground = Color(0xFFFCE8ED),
            onSurface = Color(0xFFF7D1DB),
            borderGlow = Color(0xFFFF1744),
            accentCrit = Color(0xFFFFEA00),
            shieldColor = Color(0xFF7C4DFF),
            hpColor = Color(0xFFFF1744),
            energyColor = Color(0xFFFF9100)
        )
        CyberThemeMode.OBSIDIAN_GOLD -> CyberPalette(
            primary = Color(0xFFFFD700),
            secondary = Color(0xFF00E5FF),
            tertiary = Color(0xFFFFAB00),
            background = Color(0xFF0A0A0A),
            surface = Color(0xFF1A1813),
            surfaceVariant = Color(0xFF2C281F),
            onPrimary = Color(0xFF000000),
            onBackground = Color(0xFFFFF8E1),
            onSurface = Color(0xFFF3E5AB),
            borderGlow = Color(0xFFFFD700),
            accentCrit = Color(0xFFFF3D00),
            shieldColor = Color(0xFF00E5FF),
            hpColor = Color(0xFF00E676),
            energyColor = Color(0xFFFFD700)
        )
    }
}

fun CyberPalette.toM3ColorScheme(): ColorScheme {
    return darkColorScheme(
        primary = this.primary,
        secondary = this.secondary,
        tertiary = this.tertiary,
        background = this.background,
        surface = this.surface,
        surfaceVariant = this.surfaceVariant,
        onPrimary = this.onPrimary,
        onBackground = this.onBackground,
        onSurface = this.onSurface
    )
}
