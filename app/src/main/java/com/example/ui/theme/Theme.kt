package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val KimiDarkColorScheme = darkColorScheme(
  primary = ElectricBlue,
  onPrimary = Color.White,
  secondary = IceBlue,
  onSecondary = Color.White,
  tertiary = KimiRed,
  onTertiary = Color.White,
  background = DarkNavyBackground,
  onBackground = TextLight,
  surface = DarkNavySurface,
  onSurface = TextLight,
  surfaceVariant = SecondaryNavySurface,
  onSurfaceVariant = TextLight
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force dark blue as requested
  dynamicColor: Boolean = false, // Emphasize brand colors
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = KimiDarkColorScheme,
    typography = Typography,
    content = content
  )
}
