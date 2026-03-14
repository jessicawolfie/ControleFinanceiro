package com.example.controlefinanceiro.ui.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.controlefinanceiro.navigation.Routes
import com.example.controlefinanceiro.ui.theme.*
import kotlinx.coroutines.delay
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController

@Composable
fun SplashScreen(navController: NavController) {
    // animateFloatAsState anima um valor Float de um estado para outro
    var iniciarAnimacao by remember { mutableStateOf(false) }
    val opacidade by animateFloatAsState(
        targetValue = if (iniciarAnimacao) 1f else 0f,
        // tween = animação linear com duração definida.
        animationSpec = tween(durationMillis = 1000)
    )

    // LaunchedEffect executa código suspenso dentro de um Composable.
    // O bloco roda uma vez quando o composable entra na tela.
    LaunchedEffect(Unit) {
        iniciarAnimacao = true // Dispara animação de fade in
        delay(2500) // Espera 2.5 segundos
        navController.navigate(Routes.DASHBOARD) {
            // Remove o SplashScreen da pilha de navegação
            popUpTo(Routes.SPLASH) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Branco)
            .alpha(opacidade),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ícone — quadrado verde arredondado com emoji de gráfico
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Verde),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📈",
                    fontSize = 48.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Controle Financeiro",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Verde
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Organize sua vida financeira",
                fontSize = 14.sp,
                color = CinzaTexto
            )

            Spacer(modifier = Modifier.height(48.dp))
            Text(
                text = ". . .",
                fontSize = 20.sp,
                color = Verde,
                textAlign = TextAlign.Center
            )
        }

        // Versão e copyright no rodapé.
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Versão 1.0.0",
                fontSize = 12.sp,
                color = CinzaTexto
            )
            Text(
                text = "© 2026 Controle Financeiro",
                fontSize = 12.sp,
                color = CinzaTexto
            )
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    ControleFinanceiroTheme {
        SplashScreen(navController = rememberNavController())
    }
}