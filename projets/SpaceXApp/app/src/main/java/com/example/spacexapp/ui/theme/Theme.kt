package com.example.spacexapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = DeepSkyBlue,
    onPrimary = Color.White,
    secondary = AccentCyan,
    background = SpaceNavy,
    surface = SpaceCard,
    onBackground = WhiteElegant,
    onSurface = WhiteElegant,
    outline = SlateGray
)

@Composable
fun SpaceXAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // On force souvent le Dark Mode pour les apps d'espace car c'est plus immersif
    val colorScheme = if (darkTheme) DarkColorScheme else DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}