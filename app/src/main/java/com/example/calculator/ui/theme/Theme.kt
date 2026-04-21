package com.example.calculator.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    background = Black,
    onBackground = White,
    primary = Grey400,
    secondary = Green200,
    tertiary = Blue200,
    primaryContainer = Grey500,
    onPrimaryContainer = White,
    onSecondaryContainer = Grey100
)

private val LightColorScheme = lightColorScheme(
    background = White,
    onBackground = Grey600,
    primary = Grey200,
    secondary = Green100,
    tertiary = Blue100,
    primaryContainer = Grey300,
    onPrimaryContainer = Black,
    onSecondaryContainer = Black
)

@Composable
fun CalculatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Выключаем динамические цвета, чтобы работали наши Grey400, Green200 и т.д.
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}