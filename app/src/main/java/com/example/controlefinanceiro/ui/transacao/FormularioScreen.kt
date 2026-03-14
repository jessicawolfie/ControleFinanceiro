package com.example.controlefinanceiro.ui.transacao

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.controlefinanceiro.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioScreen(
    navController: NavController,
    viewModel: FormularioViewModel,
    transacaoId: Long
) {
    val uiState by viewModel.uiState.collectAsState()
    val categorias by viewModel.categorias.collectAsState()
    val contas by viewModel.contas.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(transacaoId) {
        if (transacaoId == -1L) {
            viewModel.limpar() // limpa o formulário para nova transação
        } else {
            viewModel.carregarTransacao(transacaoId) // carrega para edição
        }
    }

    // Quando salvar, volta para a tela anterior.
    LaunchedEffect(uiState.salvou) {
        if (uiState.salvou) {
            navController.popBackStack()
        }
    }

    // Mostrar erro se existir
    LaunchedEffect(uiState.erro) {
        uiState.erro?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.isEdicao) "Editar Transação" else "Nova Transação",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    if (uiState.isEdicao) {
                        IconButton(onClick = { viewModel.deletar() }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Deletar",
                                tint = Vermelho
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Branco)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CinzaFundo)
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Toggle Receita / Despesa
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Branco)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Botão Receita
                    BotaoTipo(
                        modifier = Modifier.weight(1f),
                        texto = "↑ Receita",
                        selecionado = uiState.tipo == "RECEITA",
                        corAtiva = Verde,
                        onClick = { viewModel.onTipoChange("RECEITA") }
                    )

                    // Botão Despesa
                    BotaoTipo(
                        modifier = Modifier.weight(1f),
                        texto = "↓ Despesa",
                        selecionado = uiState.tipo == "DESPESA",
                        corAtiva = Vermelho,
                        onClick = { viewModel.onTipoChange("DESPESA") }
                    )
                }
            }
            
            // Campos do formulário
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Branco)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Campo nome
                    CampoTexto(
                        label = "Nome da Transação",
                        placeholder = "Ex: Mercado, Salário...",
                        value = uiState.descricao,
                        onValueChange = { viewModel.onDescricaoChange(it) }
                    )

                    // Campo valor
                    CampoTexto(
                        label = "Valor",
                        placeholder = "0,00",
                        value = uiState.valor,
                        onValueChange = { viewModel.onValorChange(it) },
                        keyboardType = KeyboardType.Decimal
                    )

                    // Dropdown categoria
                    DropdownCampo(
                        label = "Categoria",
                        placeholder = "Selecione uma categoria",
                        opcoes = categorias,
                        opcaoSelecionada = uiState.categoriaSelecionada,
                        textoOpcao = { it.nome },
                        onOpcaoSelecionada = { viewModel.onCategoriaChange(it) }
                    )

                    // Dropdown conta
                    DropdownCampo(
                        label = "Conta",
                        placeholder = "Selecione uma conta",
                        opcoes = contas,
                        opcaoSelecionada = uiState.contaSelecionada,
                        textoOpcao = { it.nome },
                        onOpcaoSelecionada = { viewModel.onContaChange(it) }
                    )

                    // Campo data
                    CampoData(
                        data = uiState.data,
                        onDataSelecionada = { viewModel.onDataChange(it) },
                        context = context
                    )

                    // Campo observação
                    CampoTexto(
                        label = "Observação (opcional)",
                        placeholder = "Adicione detalhes...",
                        value = uiState.observacao,
                        onValueChange = { viewModel.onObservacaoChange(it) },
                        linhas = 3
                    )
                }
            }

            // Botão salvar
            Button(
                onClick = { viewModel.salvar() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (uiState.tipo == "RECEITA") Verde else Vermelho)
            ) {
                Text(
                    text = if (uiState.isEdicao) "Salvar Alterações" else "Cadastrar Transação",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

// Componentes reutilizáveis
@Composable
fun BotaoTipo(
    modifier: Modifier = Modifier,
    texto: String,
    selecionado: Boolean,
    corAtiva: Color,
    onClick: () -> Unit
) {
    val corFundo = if (selecionado) corAtiva else CinzaFundo
    val corTexto = if (selecionado) Branco else CinzaTexto

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(corFundo)
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = texto,
            color = corTexto,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
fun CampoTexto(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    linhas: Int = 1
) {
    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            color = CinzaTexto,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.LightGray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            minLines = linhas,
            maxLines = linhas,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Verde,
                unfocusedBorderColor = Color.LightGray
            )
        )
    }
}

// T é um tipo genérico — funciona para Categoria e Conta
@Composable
fun <T> DropdownCampo(
    label: String,
    placeholder: String,
    opcoes: List<T>,
    opcaoSelecionada: T?,
    textoOpcao: (T) -> String,
    onOpcaoSelecionada: (T) -> Unit
) {
    // controla se o menu está aberto ou fechado
    var expandido by remember { mutableStateOf(false) }

    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            color = CinzaTexto,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = if (expandido) Verde else CinzaTexto.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable { expandido = !expandido }
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = opcaoSelecionada?.let { textoOpcao(it) } ?: placeholder,
                    color = if (opcaoSelecionada != null) Preto else CinzaTexto,
                    fontSize = 14.sp
                )
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = CinzaTexto
                )
            }
            // menu suspenso com as opções
            DropdownMenu(
                expanded = expandido,
                onDismissRequest = { expandido = false }
            ) {
                opcoes.forEach { opcao ->
                    DropdownMenuItem(
                        text = { Text(textoOpcao(opcao)) },
                        onClick = {
                            onOpcaoSelecionada(opcao)
                            expandido = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CampoData(
    data: Date,
    onDataSelecionada: (Date) -> Unit,
    context: android.content.Context
) {
    val formato = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
    val calendario = Calendar.getInstance().apply { time = data }

    Column {
        Text(
            text = "Data",
            fontSize = 12.sp,
            color = CinzaTexto,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = CinzaTexto.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp)
                )
                // abre o DatePickerDialog nativo do Android ao clicar
                .clickable {
                    DatePickerDialog(
                        context,
                        { _, ano, mes, dia ->
                            val cal = Calendar.getInstance()
                            cal.set(ano, mes, dia)
                            onDataSelecionada(cal.time)
                        },
                        calendario.get(Calendar.YEAR),
                        calendario.get(Calendar.MONTH),
                        calendario.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formato.format(data),
                    color = Preto,
                    fontSize = 14.sp
                )
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = "Selecionar data",
                    tint = CinzaTexto
                )
            }
        }
    }
}
