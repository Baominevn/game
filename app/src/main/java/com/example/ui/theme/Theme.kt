package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.example.model.CyberThemeMode

val LocalCyberPalette = staticCompositionLocalOf {
    getCyberPalette(CyberThemeMode.NEON_CYAN)
}

@Composable
fun CyberStrikeTheme(
    mode: CyberThemeMode = CyberThemeMode.NEON_CYAN,
    content: @Composable () -> Unit
) {
    val palette = getCyberPalette(mode)
    val colorScheme = palette.toM3ColorScheme()

    CompositionLocalProvider(LocalCyberPalette provides palette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

