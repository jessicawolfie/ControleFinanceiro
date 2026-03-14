package com.example.controlefinanceiro.ui.transacao

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.controlefinanceiro.data.entity.Transacao
import com.example.controlefinanceiro.navigation.Routes
import com.example.controlefinanceiro.ui.dashboard.formatarData
import com.example.controlefinanceiro.ui.dashboard.formatarMoeda
import com.example.controlefinanceiro.ui.theme.*

@Composable
fun TransacoesScreen(
    navController: NavController,
    viewModel: TransacoesViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Routes.formulario())
                },
                containerColor = Verde
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nova transação", tint = Branco)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(CinzaFundo)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Transações",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Preto
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Chips de filtro — Todas / Receitas / Despesas
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ChipFiltro(
                    texto = "Todas",
                    selecionado = uiState.filtroAtivo == FiltroTransacao.TODAS,
                    onClick = { viewModel.onFiltroChange(FiltroTransacao.TODAS) }
                )
                ChipFiltro(
                    texto = "Receitas",
                    selecionado = uiState.filtroAtivo == FiltroTransacao.RECEITAS,
                    onClick = { viewModel.onFiltroChange(FiltroTransacao.RECEITAS) }
                )
                ChipFiltro(
                    texto = "Despesas",
                    selecionado = uiState.filtroAtivo == FiltroTransacao.DESPESAS,
                    onClick = { viewModel.onFiltroChange(FiltroTransacao.DESPESAS) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Verde)
                }
            } else if (uiState.transacoes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhuma transação encontrada.",
                        color = CinzaTexto
                    )
                }
            } else {
                // LazyColumn renderiza só os itens visíveis
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.transacoes) { transacao ->
                        ItemTransacaoLista(
                            transacao = transacao,
                            onClick = {
                                navController.navigate(
                                    Routes.formulario(transacao.id)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChipFiltro(
    texto: String,
    selecionado: Boolean,
    onClick: () -> Unit
) {
    // Chip muda de cor quando selecionado
    val corFundo = if (selecionado) Verde else Branco
    val corTexto = if (selecionado) Branco else CinzaTexto

    Surface(
        shape = RoundedCornerShape(50.dp),
        color = corFundo,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = texto,
            color = corTexto,
            fontSize = 14.sp,
            fontWeight = if (selecionado) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun ItemTransacaoLista(
    transacao: Transacao,
    onClick: () -> Unit
) {
    val corValor = if (transacao.tipo == "RECEITA") Verde else Vermelho
    val prefixo = if (transacao.tipo == "RECEITA") "+" else "-"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Branco)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transacao.descricao,
                    fontWeight = FontWeight.Medium,
                    fontSize = 15.sp,
                    color = Preto
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatarData(transacao.data),
                    fontSize = 12.sp,
                    color = CinzaTexto
                )
            }
            Text(
                text = "$prefixo${formatarMoeda(transacao.valor)}",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = corValor
            )
        }
    }
}
