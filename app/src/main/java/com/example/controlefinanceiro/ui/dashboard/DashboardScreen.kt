package com.example.controlefinanceiro.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.controlefinanceiro.data.entity.Transacao
import com.example.controlefinanceiro.navigation.Routes
import com.example.controlefinanceiro.ui.theme.Branco
import com.example.controlefinanceiro.ui.theme.CinzaFundo
import com.example.controlefinanceiro.ui.theme.CinzaTexto
import com.example.controlefinanceiro.ui.theme.ControleFinanceiroTheme
import com.example.controlefinanceiro.ui.theme.Preto
import com.example.controlefinanceiro.ui.theme.Verde
import com.example.controlefinanceiro.ui.theme.Vermelho
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.items
import com.example.controlefinanceiro.ui.theme.VerdeFundo
import com.example.controlefinanceiro.ui.theme.VermelhoFundo

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel
) {
    // collectAsState converte o StateFlow em um State do Compose.
    // Toda vez que o StateFlow emitir um valor novo, a tela recompõe.
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        // FAB - Floating Action Button - botão de ação flutuante
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Routes.formulario())
                },
                containerColor = Verde
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Nova transação",
                    tint = Branco
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.Companion
                .fillMaxSize()
                .padding(paddingValues)
                .background(CinzaFundo),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Cada item {} é um bloco fixo na lista
            item { HeaderDashboard() }
            item {
                CardSaldo(
                    saldo = uiState.saldoAtual,
                    isLoading = uiState.isLoading
                )
            }
            item {
                Row(
                    modifier = Modifier.Companion.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CardResumo(
                        modifier = Modifier.Companion.weight(1f),
                        titulo = "RECEITAS",
                        valor = uiState.totalReceitas,
                        isReceita = true
                    )
                    CardResumo(
                        modifier = Modifier.Companion.weight(1f),
                        titulo = "DESPESAS",
                        valor = uiState.totalDespesas,
                        isReceita = false
                    )
                }
            }
            item {
                Row(
                    modifier = Modifier.Companion.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Companion.CenterVertically
                ) {
                    Text(
                        text = "Últimas Transações",
                        fontWeight = FontWeight.Companion.Bold,
                        fontSize = 16.sp,
                        color = Preto
                    )
                    Text(
                        text = "Ver todas",
                        color = Verde,
                        fontSize = 14.sp,
                        modifier = Modifier.Companion.clickable {
                            navController.navigate(Routes.TRANSACOES)
                        }
                    )
                }
            }
            if (uiState.isLoading) {
                item {
                    Box(
                        modifier = Modifier.Companion.fillMaxWidth(),
                        contentAlignment = Alignment.Companion.Center
                    ) {
                        CircularProgressIndicator(color = Verde)
                    }
                }
            } else if (uiState.ultimasTransacoes.isEmpty()) {
                item {
                    Text(
                        text = "Nenhuma transação ainda. \nClique em + para adicionar!",
                        color = CinzaTexto,
                        modifier = Modifier.Companion.padding(vertical = 24.dp)
                    )
                }
            } else {
                // items() itera sobre a lista e renderiza um composable por item.
                items(uiState.ultimasTransacoes) { transacao ->
                    ItemTransacao(
                        transacao = transacao,
                        onClick = {
                            navController.navigate(Routes.formulario(transacao.id))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HeaderDashboard() {
    Row(
        modifier = Modifier.Companion.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Companion.CenterVertically
    ) {
        Column {
            Text(
                text = "Resumo",
                fontSize = 22.sp,
                fontWeight = FontWeight.Companion.Bold,
                color = Preto
            )
        }
        Icon(
            imageVector = Icons.Default.ArrowDownward,
            contentDescription = "Notificações",
            tint = CinzaTexto
        )
    }
}

@Composable
fun CardSaldo(saldo: Double, isLoading: Boolean) {
    Card(
        modifier = Modifier.Companion.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Verde)
    ) {
        Column(modifier = Modifier.Companion.padding(20.dp)) {
            Text(
                text = "Saldo Atual",
                fontSize = 14.sp,
                color = Branco.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.Companion.height(8.dp))
            if (isLoading) {
                CircularProgressIndicator(color = Branco, modifier = Modifier.Companion.size(24.dp))
            } else {
                Text(
                    text = formatarMoeda(saldo),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Companion.Bold,
                    color = Branco
                )
            }
        }
    }
}

@Composable
fun CardResumo(
    modifier: Modifier = Modifier.Companion,
    titulo: String,
    valor: Double,
    isReceita: Boolean
) {
    // A cor e o ícone mudam se for uma receita ou despesa.
    val corFundo = if (isReceita) VerdeFundo else VermelhoFundo
    val corValor = if (isReceita) Verde else Vermelho
    val icone = if (isReceita) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward

    Card(
        modifier = modifier,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Branco)
    ) {
        Column(modifier = Modifier.Companion.padding(16.dp)) {
            // Ícone com fundo colorido.
            Box(
                modifier = Modifier.Companion
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(corFundo),
                contentAlignment = Alignment.Companion.Center
            ) {
                Icon(
                    imageVector = icone,
                    contentDescription = titulo,
                    tint = corValor,
                    modifier = Modifier.Companion.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.Companion.height(8.dp))
            Text(text = titulo, fontSize = 11.sp, color = CinzaTexto)
            Spacer(modifier = Modifier.Companion.height(4.dp))
            Text(
                text = formatarMoeda(valor),
                fontSize = 16.sp,
                fontWeight = FontWeight.Companion.Bold,
                color = corValor
            )
        }
    }
}

@Composable
fun ItemTransacao(
    transacao: Transacao,
    onClick: () -> Unit
) {
    val corValor = if (transacao.tipo == "RECEITA") Verde else Vermelho
    val prefixo = if (transacao.tipo == "RECEITA") "+" else "-"

    Card(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .clickable { onClick() },
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Branco)
    ) {
        Row(
            modifier = Modifier.Companion
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Companion.CenterVertically
        ) {
            Column(modifier = Modifier.Companion.weight(1f)) {
                Text(
                    text = transacao.descricao,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Companion.Medium,
                    color = Preto
                )
                Spacer(modifier = Modifier.Companion.height(4.dp))
                Text(
                    text = formatarData(transacao.data),
                    fontSize = 12.sp,
                    color = CinzaTexto
                )
            }
            Text(
                text = "$prefixo${formatarMoeda(transacao.valor)}",
                fontSize = 15.sp,
                fontWeight = FontWeight.Companion.Bold,
                color = corValor
            )
        }
    }
}

// Formata Double para moeda brasileira.
fun formatarMoeda(valor: Double): String {
    return NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(valor)
}

// Formata Data para string
fun formatarData(data: Date): String {
    return SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(data)
}

