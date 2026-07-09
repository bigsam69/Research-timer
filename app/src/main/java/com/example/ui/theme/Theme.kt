package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = CocGold,
    secondary = CocElixir,
    tertiary = CocButtonGreen,
    background = CocStoneBg,
    surface = CocWoodCard,
    onPrimary = CocBorder,
    onSecondary = CocTextWhite,
    onBackground = CocTextWhite,
    onSurface = CocTextWhite,
    surfaceVariant = CocStoneCard
  )

private val LightColorScheme = DarkColorScheme // Keep it consistent for game immersive experience

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force dark theme for Clash of Clans game immersion
  dynamicColor: Boolean = false, // Disable dynamic colors to keep game UI
  content: @Composable () -> Unit,
) {
  val colorScheme = DarkColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
