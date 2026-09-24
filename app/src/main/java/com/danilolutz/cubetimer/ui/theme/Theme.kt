package com.danilolutz.cubetimer.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightThemeBackground = Color(0xFFE8E8EE)
private val LightThemeSurface = Color(0xFFF9F9FF)
private val LightThemeOnBackground = Color(0xFF1B1B1F)
private val LightThemeOnSurface = Color(0xFF1B1B1F)
private val LightThemeSurfaceVariant = Color(0xFFE4E1EC)
private val LightThemeOnSurfaceVariant = Color(0xFF49454F)

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = LightThemeBackground,
    onBackground = LightThemeOnBackground,
    surface = LightThemeSurface,
    onSurface = LightThemeOnSurface,
    surfaceVariant = LightThemeSurfaceVariant,
    onSurfaceVariant = LightThemeOnSurfaceVariant,
)

@Composable
fun CubeTimerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context).copy(
                    background = LightThemeBackground,
                    onBackground = LightThemeOnBackground,
                    surface = LightThemeSurface,
                    onSurface = LightThemeOnSurface,
                    surfaceVariant = LightThemeSurfaceVariant,
                    onSurfaceVariant = LightThemeOnSurfaceVariant,
                )
            }
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
