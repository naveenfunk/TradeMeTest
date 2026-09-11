package nz.co.trademetest.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Tasman500,
    onPrimary = White,
    secondary = Feijoa500,
    onSecondary = White,
    background = White,
    onBackground = BluffOyster800,
    surface = White,
    onSurface = BluffOyster800,
    onSurfaceVariant = BluffOyster600,
    outlineVariant = BluffOyster600,
)

private val DarkColors = darkColorScheme(
    primary = Tasman500,
    onPrimary = White,
    secondary = Feijoa500,
    onSecondary = White,
    background = NeutralDark,
    onBackground = White,
    surface = NeutralDark,
    onSurface = White,
    onSurfaceVariant = BluffOyster600,
    outlineVariant = BluffOyster600,
)

@Composable
fun TradeMeTestTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content,
    )
}
