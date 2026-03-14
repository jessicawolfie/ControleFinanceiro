package com.example.controlefinanceiro.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val EsquemaDeCores = lightColorScheme(
    primary = Verde,
    onPrimary = Branco,
    secondary = VerdeClaro,
    onSecondary = Branco,
    background = CinzaFundo,
    onBackground = Preto,
    surface = Branco,
    onSurface = Preto,
    error = Vermelho,
    onError = Branco
)

@Composable
fun ControleFinanceiroTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = EsquemaDeCores,
        typography = Typography,
        content = content
    )
}