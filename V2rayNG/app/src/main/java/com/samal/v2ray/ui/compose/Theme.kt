package com.samal.v2ray.ui.compose

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.samal.v2ray.handler.MmkvManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Pure White & Neon Pink Theme (White Edition)
private val PureWhite = Color(0xFFFFFFFF)
private val LightSurface = Color(0xFFF8F9FA)
private val CardSurface = Color(0xFFFFFFFF)
private val NeonPink = Color(0xFFFF007F)
private val DeepPink = Color(0xFFFF1493)
private val SoftPinkBg = Color(0xFFFFEFF5)
private val TextDark = Color(0xFF1A1A1A)
private val TextGray = Color(0xFF666666)

private val LightColor = lightColorScheme(
    primary = NeonPink,
    onPrimary = Color.WHITE,
    primaryContainer = Color(0xFFFFD6E8),
    onPrimaryContainer = Color(0xFF3B0018),
    secondary = DeepPink,
    onSecondary = Color.WHITE,
    secondaryContainer = Color(0xFFFFD1DC),
    onSecondaryContainer = Color(0xFF3D0017),
    background = PureWhite,
    onBackground = TextDark,
    surface = CardSurface,
    onSurface = TextDark,
    surfaceVariant = SoftPinkBg,
    onSurfaceVariant = TextGray,
    outline = Color(0xFFE0E0E0),
    surfaceTint = NeonPink
)

// Also keep light theme as default for dark setting if forced white
private val DarkColor = lightColorScheme(
    primary = NeonPink,
    onPrimary = Color.WHITE,
    primaryContainer = Color(0xFFFFD6E8),
    onPrimaryContainer = Color(0xFF3B0018),
    secondary = DeepPink,
    onSecondary = Color.WHITE,
    secondaryContainer = Color(0xFFFFD1DC),
    onSecondaryContainer = Color(0xFF3D0017),
    background = PureWhite,
    onBackground = TextDark,
    surface = CardSurface,
    onSurface = TextDark,
    surfaceVariant = SoftPinkBg,
    onSurfaceVariant = TextGray,
    outline = Color(0xFFE0E0E0),
    surfaceTint = NeonPink
)

// Exported Semantic Colors for other components
val colorPing = Color(0xFF00C853)
val colorPingRed = Color(0xFFFF3D00)
val colorConfigType = Color(0xFFFF007F)
val colorFabActive = Color(0xFFFF007F)
val colorFabInactiveLight = Color(0xFFE0E0E0)
val colorFabInactiveDark = Color(0xFFBDBDBD)
val dividerColorLight = Color(0xFFFFD1DC)
val dividerColorDark = Color(0xFFFFD1DC)

// Toast/SnackBar Colors
val toastNormalBgLight = Color(0xCC1A1A1A)
val toastNormalBgDark = Color(0xCC1A1A1A)
val toastSuccessBg = Color(0xCCFF007F)
val toastErrorBg = Color(0xCCD32F2F)
val toastInfoBg = Color(0xCCFF1493)
val toastIconCircleBg = Color(0x20FFFFFF)
val toastTextColor = Color.White

val LocalDarkTheme = compositionLocalOf { false }

object ThemeManager {
    private val _themeMode = MutableStateFlow(0) // Force Light/White mode by default
    val themeMode: StateFlow<Int> = _themeMode.asStateFlow()

    fun setThemeMode(mode: Int) {
        _themeMode.value = 0 // Always white
    }

    fun refresh() {}

    init {
        _themeMode.value = 0
    }
}

@Composable
fun SamalTheme(content: @Composable () -> Unit) {
    val colorScheme = LightColor
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = true
                isAppearanceLightNavigationBars = true
            }
        }
    }

    CompositionLocalProvider(LocalDarkTheme provides false) {
        MaterialTheme(colorScheme = colorScheme, content = content)
    }
}

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    SamalTheme(content)
}
