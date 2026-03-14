package com.example.controlefinanceiro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.controlefinanceiro.navigation.Routes
import com.example.controlefinanceiro.ui.dashboard.DashboardScreen
import com.example.controlefinanceiro.ui.dashboard.DashboardViewModel
import com.example.controlefinanceiro.ui.relatorios.RelatoriosScreen
import com.example.controlefinanceiro.ui.relatorios.RelatoriosViewModel
import com.example.controlefinanceiro.ui.splash.SplashScreen
import com.example.controlefinanceiro.ui.theme.ControleFinanceiroTheme
import com.example.controlefinanceiro.ui.transacao.FormularioScreen
import com.example.controlefinanceiro.ui.transacao.FormularioViewModel
import com.example.controlefinanceiro.ui.transacao.TransacoesScreen
import com.example.controlefinanceiro.ui.transacao.TransacoesViewModel

// Representa cada item da barra de navegação
data class ItemNavegacao(
    val rota: String,
    val titulo: String,
    val icone: ImageVector
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ControleFinanceiroTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Define os 3 itens da bottom navigation
    val itensNavegacao = listOf(
        ItemNavegacao(Routes.DASHBOARD, "Resumo", Icons.Default.Home),
        ItemNavegacao(Routes.TRANSACOES, "Transações", Icons.Default.List),
        ItemNavegacao(Routes.RELATORIOS, "Relatórios", Icons.Default.BarChart)
    )

    // Observa a rota atual para saber qual item destacar na barra
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val rotaAtual = navBackStackEntry?.destination?.route

    // Rotas onde a bottom nav deve aparecer
    val rotasComNav = listOf(Routes.DASHBOARD, Routes.TRANSACOES, Routes.RELATORIOS)

    Scaffold(
        bottomBar = {
            // Só mostra a barra nas telas principais
            // no Splash e no Formulário ela fica escondida
            if (rotaAtual in rotasComNav) {
                NavigationBar {
                    itensNavegacao.forEach { item ->
                        NavigationBarItem(
                            selected = rotaAtual == item.rota,
                            onClick = {
                                navController.navigate(item.rota) {
                                    // popUpTo evita acumular telas na pilha
                                    // ao navegar pelo bottom nav
                                    popUpTo(Routes.DASHBOARD) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icone,
                                    contentDescription = item.titulo
                                )
                            },
                            label = { Text(item.titulo) }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Routes.SPLASH,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Routes.SPLASH) {
                SplashScreen(navController = navController)
            }

            composable(Routes.DASHBOARD) {
                val context = LocalContext.current
                val app = context.applicationContext as FinanceiroApplication
                val viewModel: DashboardViewModel = viewModel(
                    factory = DashboardViewModel.factory(app.repository)
                )
                DashboardScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }

            composable(Routes.TRANSACOES) {
                val context = LocalContext.current
                val app = context.applicationContext as FinanceiroApplication
                val viewModel: TransacoesViewModel = viewModel(
                    factory = TransacoesViewModel.factory(app.repository)
                )
                TransacoesScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }

            composable(
                route = Routes.FORMULARIO,
                arguments = listOf(
                    navArgument("id") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("id") ?: -1L
                val context = LocalContext.current
                val app = context.applicationContext as FinanceiroApplication
                val viewModel: FormularioViewModel = viewModel(
                    factory = FormularioViewModel.factory(app.repository)
                )
                FormularioScreen(
                    navController = navController,
                    transacaoId = id,
                    viewModel = viewModel
                )
            }

            composable(Routes.RELATORIOS) {
                val context = LocalContext.current
                val app = context.applicationContext as FinanceiroApplication
                val viewModel: RelatoriosViewModel = viewModel(
                    factory = RelatoriosViewModel.factory(app.repository)
                )
                RelatoriosScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
    }
}