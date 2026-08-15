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

// Ultra Modern Neo-Neon Pink & Pure OLED Black Theme
private val LightColor = lightColorScheme(
    primary = Color(0xFFFF007F), // Electric Neon Pink
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFB6C1),
    onPrimaryContainer = Color(0xFF380011),
    secondary = Color(0xFFFF1493), // Deep Pink
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFD1DC),
    onSecondaryContainer = Color(0xFF3B0014),
    tertiary = Color(0xFFC71585),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFE4E1),
    onTertiaryContainer = Color(0xFF2C0B1B),
    error = Color(0xFFD32F2F),
    errorContainer = Color(0xFFFFCDD2),
    onError = Color(0xFFFFFFFF),
    onErrorContainer = Color(0xFFB71C1C),
    background = Color(0xFFFFF0F5), // Lavender Blush (Soft Pinkish White)
    onBackground = Color(0xFF1F1A1C),
    surface = Color(0xFFFFFFFF), // Pure White Card Surface
    onSurface = Color(0xFF1F1A1C),
    surfaceVariant = Color(0xFFF9E4EC),
    onSurfaceVariant = Color(0xFF524348),
    outline = Color(0xFF857378),
    outlineVariant = Color(0xFFD7C1C8),
    inverseSurface = Color(0xFF342F31),
    inverseOnSurface = Color(0xFFFAEDF1),
    inversePrimary = Color(0xFFFF85C0),
    scrim = Color(0xFF000000),
    surfaceTint = Color(0xFFFF007F),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFFF8FA),
    surfaceContainer = Color(0xFFFDE8EE),
    surfaceContainerHigh = Color(0xFFF7D5E0),
    surfaceContainerHighest = Color(0xFFF0B8CB),
)

private val DarkColor = darkColorScheme(
    primary = Color(0xFFFF2A9D), // Vibrant Glowing Pink
    onPrimary = Color(0xFF20000A),
    primaryContainer = Color(0xFF99004C),
    onPrimaryContainer = Color(0xFFFFD9E4),
    secondary = Color(0xFFFF66B2),
    onSecondary = Color(0xFF2B0014),
    secondaryContainer = Color(0xFF660033),
    onSecondaryContainer = Color(0xFFFFD9E4),
    tertiary = Color(0xFFFF80BF),
    onTertiary = Color(0xFF33001A),
    tertiaryContainer = Color(0xFF800040),
    onTertiaryContainer = Color(0xFFFFD9E4),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    onError = Color(0xFF690005),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF000000), // Pure OLED Black 100%
    onBackground = Color(0xFFFFF0F5),
    surface = Color(0xFF000000), // Pure OLED Black Surface
    onSurface = Color(0xFFFFF0F5),
    surfaceVariant = Color(0xFF2E1A23), // Deep Glass Dark Pink Tint
    onSurfaceVariant = Color(0xFFD9C1CA),
    outline = Color(0xFFA18C94),
    outlineVariant = Color(0xFF3D2631),
    inverseSurface = Color(0xFFFFEFF5),
    inverseOnSurface = Color(0xFF1A1115),
    inversePrimary = Color(0xFFFF007F),
    scrim = Color(0xFF000000),
    surfaceTint = Color(0xFFFF2A9D),
    surfaceContainerLowest = Color(0xFF000000), // Absolute OLED Black
    surfaceContainerLow = Color(0xFF0D0307),
    surfaceContainer = Color(0xFF1A0610),
    surfaceContainerHigh = Color(0xFF2B0A1B),
    surfaceContainerHighest = Color(0xFF47102C),
)

// Semantic Colors
val colorPing = Color(0xFFFF007F)
val colorPingRed = Color(0xFFFF0055)
val colorConfigType = Color(0xFFFF2A9D)
val colorFabActive = Color(0xFFFF007F)
val colorFabInactiveLight = Color(0xFFB0B0B0)
val colorFabInactiveDark = Color(0xFF404040)
val dividerColorLight = Color(0xFFFFD1DC)
val dividerColorDark = Color(0xFF3D1426)

// Toast Colors
val toastNormalBgLight = Color(0xCC201A1D)
val toastNormalBgDark = Color(0xCC1A0610)
val toastSuccessBg = Color(0xCCFF007F)
val toastErrorBg = Color(0xCCD32F2F)
val toastInfoBg = Color(0xCCFF2A9D)
val toastIconCircleBg = Color(0x40FFFFFF)
val toastTextColor = Color.White

object ThemeManager {
    private val _themeMode = MutableStateFlow(2) // Default Dark OLED
    val themeMode: StateFlow<Int> = _themeMode.asStateFlow()

    fun setThemeMode(mode: Int) {
        _themeMode.value = mode
        try {
            MmkvManager.encodeSettings("pref_theme", mode)
        } catch (_: Exception) {}
    }

    init {
        try {
            val saved = MmkvManager.decodeSettings("pref_theme", 2)
            if (saved is Int) {
                _themeMode.value = saved
            }
        } catch (_: Exception) {}
    }
}

val LocalDarkTheme = compositionLocalOf { true }

@Composable
fun SamalTheme(
    content: @Composable () -> Unit
) {
    val themeMode by ThemeManager.themeMode.collectAsState()
    val systemDark = isSystemInDarkTheme()
    val darkTheme = when (themeMode) {
        0 -> false // Light
        1 -> true  // Dark
        else -> true // OLED Black by default
    }

    val colorScheme = if (darkTheme) DarkColor else LightColor

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(LocalDarkTheme provides darkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}
