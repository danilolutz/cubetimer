package com.danilolutz.cubetimer

import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.test.junit4.v2.createComposeRule
import com.danilolutz.cubetimer.ui.theme.CubeTimerTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.atomic.AtomicReference

class SnackbarThemeTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun snackbarTextHasContrastInLightTheme() {
        assertSnackbarTextContrast(darkTheme = false)
    }

    @Test
    fun snackbarTextHasContrastInDarkTheme() {
        assertSnackbarTextContrast(darkTheme = true)
    }

    @Test
    fun primaryContentHasContrastInLightTheme() {
        val colors = AtomicReference<PrimaryContentColors>()
        composeRule.setContent {
            CubeTimerTheme(darkTheme = false, dynamicColor = false) {
                val colorScheme = androidx.compose.material3.MaterialTheme.colorScheme
                SideEffect {
                    colors.set(
                        PrimaryContentColors(
                            background = colorScheme.background,
                            onBackground = colorScheme.onBackground,
                            surface = colorScheme.surface,
                            onSurface = colorScheme.onSurface,
                        ),
                    )
                }
            }
        }
        composeRule.waitForIdle()

        val primaryContentColors = requireNotNull(colors.get())
        assertReadable(
            container = primaryContentColors.background,
            content = primaryContentColors.onBackground,
            theme = "light",
            element = "background",
        )
        assertReadable(
            container = primaryContentColors.surface,
            content = primaryContentColors.onSurface,
            theme = "light",
            element = "surface",
        )
    }

    private fun assertSnackbarTextContrast(darkTheme: Boolean) {
        val colors = AtomicReference<SnackbarColors>()
        composeRule.setContent {
            CubeTimerTheme(darkTheme = darkTheme, dynamicColor = false) {
                val colorScheme = androidx.compose.material3.MaterialTheme.colorScheme
                SideEffect {
                    colors.set(
                        SnackbarColors(
                            container = colorScheme.surfaceVariant,
                            content = colorScheme.onSurfaceVariant,
                        ),
                    )
                }
            }
        }
        composeRule.waitForIdle()

        val snackbarColors = requireNotNull(colors.get())
        assertReadable(
            container = snackbarColors.container,
            content = snackbarColors.content,
            theme = themeName(darkTheme),
            element = "snackbar",
        )
    }
}

private data class SnackbarColors(
    val container: Color,
    val content: Color,
)

private data class PrimaryContentColors(
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
)

private fun assertReadable(container: Color, content: Color, theme: String, element: String) {
    assertTrue(
        "$element text must remain readable in $theme theme.",
        contrastRatio(container, content) >= MINIMUM_TEXT_CONTRAST,
    )
}

private fun contrastRatio(first: Color, second: Color): Float {
    val lightest = maxOf(first.luminance(), second.luminance())
    val darkest = minOf(first.luminance(), second.luminance())
    return (lightest + LUMINANCE_OFFSET) / (darkest + LUMINANCE_OFFSET)
}

private fun themeName(darkTheme: Boolean): String = if (darkTheme) "dark" else "light"

private const val MINIMUM_TEXT_CONTRAST = 4.5f
private const val LUMINANCE_OFFSET = 0.05f
