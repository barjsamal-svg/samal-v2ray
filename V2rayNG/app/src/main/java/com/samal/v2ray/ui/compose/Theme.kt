
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

// Luxurious Off-White & Deep Neon Pink Theme (Luxurious White Edition)
private val LuxuriousBg = Color(0xFFF4F6F9)       // Warm Off-White luxurious background
private val PureWhite = Color(0xFFFFFFFF)
private val CardSurface = Color(0xFFFFFFFF)
private val LuxuriousPink = Color(0xFFFF1493)     // Deep Neon Pink
private val SoftPinkTint = Color(0xFFFFE4EE)
private val TextDark = Color(0xFF1E2229)          // Soft rich dark for excellent readability
private val TextGray = Color(0xFF7A828E)
private val BorderColor = Color(0xFFE2E8F0)

val colorFabActive = Color(0xFFFF1493)
val colorFabInactiveDark = Color(0xFF2A2E3D)
val colorFabInactiveLight = Color(0xFFE2E8F0)

object ThemeManager {
    fun getTheme(): Int = 0
    fun setTheme(theme: Int) {}
    fun setThemeMode(mode: Int) {}
}

val colorConfigType = Color(0xFFFF1493)

val LocalDarkTheme = androidx.compose.runtime.compositionLocalOf { false }
val dividerColorDark = Color(0xFF2A2E3D)
val dividerColorLight = Color(0xFFE2E8F0)

val toastNormalBgDark = Color(0xFF2A2E3D)
val toastNormalBgLight = Color(0xFFFFFFFF)
val toastSuccessBg = Color(0xFF10B981)
val toastErrorBg = Color(0xFFEF4444)

private val LuxuriousLightColor = lightColorScheme(
    primary = LuxuriousPink,
    onPrimary = PureWhite,
    primaryContainer = SoftPinkTint,
    onPrimaryContainer = Color(0xFF3B0018),
    secondary = Color(0xFF6366F1),
    onSecondary = PureWhite,
    secondaryContainer = Color(0xFFE0E7FF),
    onSecondaryContainer = Color(0xFF1E1B4B),
    background = LuxuriousBg,
    onBackground = TextDark,
    surface = CardSurface,
    onSurface = TextDark,
    surfaceVariant = Color(0xFFFAF2F6),
    onSurfaceVariant = TextGray,
    outline = BorderColor,
    surfaceTint = LuxuriousPink
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = LuxuriousLightColor
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
