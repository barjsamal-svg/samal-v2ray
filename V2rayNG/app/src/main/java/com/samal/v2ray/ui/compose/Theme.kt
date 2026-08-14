package com.samal.v2ray.ui.compose

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.samal.v2ray.AppConfig
import com.samal.v2ray.handler.MmkvManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private val LightColor = lightColorScheme(
    primary = Color(0xFFFF1493), // Deep Pink
    onPrimary = Color(0xFFFFFFFF), // White
    primaryContainer = Color(0xFFFFC0CB), // Pink Light
    onPrimaryContainer = Color(0xFF38000B), // Dark Burgundy
    secondary = Color(0xFFFF69B4), // Hot Pink
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFD1DC),
    onSecondaryContainer = Color(0xFF310012),
    tertiary = Color(0xFFdb7093),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFE4E1),
    onTertiaryContainer = Color(0xFF2C151B),
    error = Color(0xFFBA1A1A),
    errorContainer = Color(0xFFFFDAD6),
    onError = Color(0xFFFFFFFF),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFFFBFD), // Soft White / Pinkish White
    onBackground = Color(0xFF201A1D),
    surface = Color(0xFFFFFFFF), // Pure White
    onSurface = Color(0xFF201A1D),
    surfaceVariant = Color(0xFFF2DDE3),
    onSurfaceVariant = Color(0xFF514347),
    outline = Color(0xFF837377),
    outlineVariant = Color(0xFFD5C2C7),
    inverseSurface = Color(0xFF352F32),
    inverseOnSurface = Color(0xFFFAEDF1),
    inversePrimary = Color(0xFFFFB0CD),
    scrim = Color(0xFF000000),
    surfaceTint = Color(0xFFFF1493),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFFF7F9),
    surfaceContainer = Color(0xFFFCE8EE),
    surfaceContainerHigh = Color(0xFFF7D9E2),
    surfaceContainerHighest = Color(0xFFF1C9D6),
)

private val DarkColor = darkColorScheme(
    primary = Color(0xFFFF69B4), // Hot Pink
    onPrimary = Color(0xFF38001D),
    primaryContainer = Color(0xFFB0005C),
    onPrimaryContainer = Color(0xFFFFD9E4),
    secondary = Color(0xFFFF85C0),
    onSecondary = Color(0xFF3F0022),
    secondaryContainer = Color(0xFF5C0034),
    onSecondaryContainer = Color(0xFFFFD9E4),
    tertiary = Color(0xFFE8B2C7),
    onTertiary = Color(0xFF452131),
    tertiaryContainer = Color(0xFF5E3747),
    onTertiaryContainer = Color(0xFFFFD9E4),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    onError = Color(0xFF690005),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF000000), // OLED Pure Black
    onBackground = Color(0xFFF8E7ED),
    surface = Color(0xFF000000), // OLED Pure Black
    onSurface = Color(0xFFF8E7ED),
    surfaceVariant = Color(0xFF514347),
    onSurfaceVariant = Color(0xFFD5C2C7),
    outline = Color(0xFF9E8C91),
    outlineVariant = Color(0xFF514347),
    inverseSurface = Color(0xFFF8E7ED),
    inverseOnSurface = Color(0xFF1B1115),
    inversePrimary = Color(0xFFFF1493),
    scrim = Color(0xFF000000),
    surfaceTint = Color(0xFFFF69B4),
    surfaceContainerLowest = Color(0xFF000000), // OLED Black
    surfaceContainerLow = Color(0xFF12080D),
    surfaceContainer = Color(0xFF1C0D15),
    surfaceContainerHigh = Color(0xFF27131D),
    surfaceContainerSecond = Color(0xFF331926),
    surfaceContainerHighest = Color(0xFF3F1F2F),
)

// Semantic Colors
val colorPing = Color(0xFFFF1493) // Pink
val colorPingRed = Color(0xFFFF007F)
val colorConfigType = Color(0xFFFF69B4)
val colorFabActive = Color(0xFFFF1493)
val colorFabInactiveLight = Color(0xFF9C9C9C)
val colorFabInactiveDark = Color(0xFF505050)
val dividerColorLight = Color(0xFFFFE4E1)
val dividerColorDark = Color(0xFF331926)

// Toast Colors 70%
val toastNormalBgLight = Color(0xB3353A3E)
val toastNormalBgDark = Color(0xB31C0D15)
val toastSuccessBg = Color(0xB3FF1493)
val toastErrorBg = Color(0xB3D50000)
val toastInfoBg = Color(0xB3FF69B4)
val toastIconCircleBg = Color(0x33FFFFFF)
val toastTextColor = Color.White

object ThemeManager {
    private val _themeMode = MutableStateFlow(
        MmkvManager.decodeSettingsString(AppConfig.PREF_UI_MODE_NIGHT, "2") ?: "2" // Default to dark OLED Pink
    )
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    fun setThemeMode(mode: String) {
        MmkvManager.encodeSettings(AppConfig.PREF_UI_MODE_NIGHT, mode)
        _themeMode.value = mode
    }

    fun refresh() {
        _themeMode.value =
            MmkvManager.decodeSettingsString(AppConfig.PREF_UI_MODE_NIGHT, "2") ?: "2"
    }
}

@Composable
fun resolveDarkTheme(): Boolean {
    val mode by ThemeManager.themeMode.collectAsState()
    return when (mode) {
        "1" -> false
        "2" -> true
        else -> true // Default to OLED Dark Pink
    }
}

val LocalDarkTheme = compositionLocalOf { false }

@Composable
fun AppTheme(
    darkTheme: Boolean = resolveDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColor else LightColor
    val snackbarController = rememberAppSnackbarController()

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context as? Activity ?: return@SideEffect
            val window = activity.window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalDarkTheme provides darkTheme,
        LocalAppSnackbar provides snackbarController
    ) {
        MaterialTheme(
            colorScheme = colorScheme
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AppSnackbarBridge(controller = snackbarController)
                content()
                AppSnackbarHost(hostState = snackbarController.hostState)
            }
        }
    }
}
