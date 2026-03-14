package com.example.controlefinanceiro.ui.relatorios

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.controlefinanceiro.ui.dashboard.formatarMoeda
import com.example.controlefinanceiro.ui.theme.*

@Composable
fun RelatoriosScreen(
    navController: NavController,
    viewModel: RelatoriosViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CinzaFundo)
            .padding(16.dp)
    ) {
        Text(
            text = "Relatórios",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Preto
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Verde)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Card de resumo geral
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Branco)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Resumo",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Preto
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Coluna de receitas
                                Column {
                                    Text(
                                        text = "Total Receitas",
                                        fontSize = 12.sp,
                                        color = CinzaTexto
                                    )
                                    Text(
                                        text = formatarMoeda(uiState.totalReceitas),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Verde
                                    )
                                }
                                // Coluna de despesas
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Total Despesas",
                                        fontSize = 12.sp,
                                        color = CinzaTexto
                                    )
                                    Text(
                                        text = formatarMoeda(uiState.totalDespesas),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Vermelho
                                    )
                                }
                            }
                        }
                    }
                }

                // Título da seção por categoria
                item {
                    Text(
                        text = "Por Categoria",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Preto
                    )
                }

                if (uiState.relatorios.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Nenhuma despesa registrada ainda.",
                                color = CinzaTexto
                            )
                        }
                    }
                } else {
                    // um card por categoria
                    items(uiState.relatorios) { item ->
                        CardCategoria(item = item)
                    }
                }
            }
        }
    }
}

@Composable
fun CardCategoria(item: RelatorioItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Branco)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Bolinha colorida — cor baseada no índice da categoria
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(getCorCategoria(item.categoria.id))
                    )
                    Text(
                        text = item.categoria.nome,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp,
                        color = Preto
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "-${formatarMoeda(item.total)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Vermelho
                    )
                    Text(
                        text = "${item.percentual.toInt()}% do total",
                        fontSize = 12.sp,
                        color = CinzaTexto
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Barra de progresso mostrando o percentual visualmente
            LinearProgressIndicator(
                progress = { item.percentual / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = getCorCategoria(item.categoria.id),
                trackColor = CinzaFundo
            )
        }
    }
}

fun getCorCategoria(id: Long): Color {
    val cores = listOf(
        Color(0xFFE53935),
        Color(0xFF1E88E5),
        Color(0xFF8E24AA),
        Color(0xFF43A047),
        Color(0xFFFF8F00),
        Color(0xFF00ACC1),
        Color(0xFFD81B60),
        Color(0xFF6D4C41),
    )
    return cores[(id % cores.size).toInt()]
}
