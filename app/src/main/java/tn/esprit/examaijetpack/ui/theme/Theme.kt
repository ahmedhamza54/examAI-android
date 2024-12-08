package tn.esprit.examaijetpack.ui.theme

import android.os.Build
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFE53935),     // Red for primary
    onPrimary = Color.White,         // White text on primary
    secondary = Color(0xFF007AFF),   // Blue for secondary
    onSecondary = Color.White,       // White text on secondary
    background = Color(0xFFFFFFFF),  // White background
    surface = Color(0xFFFFFFFF),     // White surface for cards
    onBackground = Color(0xFF000000),// Black text on background
    onSurface = Color(0xFF000000),   // Black text on surface
    error = Color(0xFFD32F2F),       // Error color
    onError = Color.White            // White text on error
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFE53935),     // Red for primary in dark mode
    onPrimary = Color.White,         // White text on primary
    secondary = Color(0xFF007AFF),   // Blue for secondary
    onSecondary = Color.Black,       // Black text on secondary
    background = Color(0xFF121212),  // Dark background
    surface = Color(0xFF1E1E1E),     // Dark surface for cards
    onBackground = Color(0xFFFFFFFF),// White text on background
    onSurface = Color(0xFFFFFFFF),   // White text on surface
    error = Color(0xFFCF6679),       // Error color
    onError = Color.Black            // Black text on error
)

@Composable
fun ExamAIjetpackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Log the primary color to verify
    Log.d("ColorDebug", "Primary color: ${colorScheme.primary}")

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}