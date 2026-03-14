package com.example.controlefinanceiro.ui.transacao

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.controlefinanceiro.data.entity.Categoria
import com.example.controlefinanceiro.data.entity.Conta
import com.example.controlefinanceiro.data.entity.Transacao
import com.example.controlefinanceiro.data.repository.FinanceiroRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

data class FormularioUiState(
    val id: Long = -1L,
    val descricao: String = "",
    val valor: String = "",
    val tipo: String = "DESPESA",
    val categoriaSelecionada: Categoria? = null,
    val contaSelecionada: Conta? = null,
    val data: Date = Date(),
    val observacao: String = "",
    val isEdicao: Boolean = false,
    val salvou: Boolean = false,
    val erro: String? = null
)

class FormularioViewModel(
    private val repository: FinanceiroRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(FormularioUiState())
    val uiState: StateFlow<FormularioUiState> = _uiState

    // Lista de categorias filtrada pelo tipo selecionado.
    // Atualiza automaticamente quando o tipo muda.
    val categorias: StateFlow<List<Categoria>> =
        repository.categoriasPorTipo(uiState.value.tipo)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val contas: StateFlow<List<Conta>> =
        repository.todasContas
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    // Carrega os dados de uma transação existente para edição.
    fun carregarTransacao(id: Long) {
        if (id == -1L) return // É uma nova transação, não precisa carregar
        viewModelScope.launch {
            // Busca a transação no banco pelo ID.
            repository.todasTransacoes.collect { lista ->
                val transacao = lista.find { it.id == id }
                transacao?.let { t ->
                    val categoria = categorias.value.find { it.id == t.categoriaId }
                    val conta = contas.value.find { it.id == t.contaId }
                    // Update modifica só os campos que você especificar
                    // Os outros campos ficam com o valor anterior
                    _uiState.update { state ->
                        state.copy(
                            id = t.id,
                            descricao = t.descricao,
                            valor = t.valor.toString(),
                            tipo = t.tipo,
                            categoriaSelecionada = categoria,
                            contaSelecionada = conta,
                            data = t.data,
                            observacao = t.observacao ?: "",
                            isEdicao = true
                        )
                    }
                }
            }
        }
    }

    fun onDescricaoChange(value: String) = _uiState.update { it.copy(descricao = value) }
    fun onValorChange(value: String) = _uiState.update { it.copy(valor = value)}
    fun onTipoChange(value: String) = _uiState.update { it.copy(tipo = value) }
    fun onCategoriaChange(value: Categoria?) = _uiState.update { it.copy(categoriaSelecionada = value) }
    fun onContaChange(value: Conta?) = _uiState.update { it.copy(contaSelecionada = value) }
    fun onDataChange(value: Date) = _uiState.update { it.copy(data = value) }
    fun onObservacaoChange(value: String) = _uiState.update { it.copy(observacao = value) }

    fun salvar() {
        val state = _uiState.value
        if (state.descricao.isBlank()) {
            _uiState.update { it.copy(erro = "Informe o nome da transação")}
            return
        }
        val valorDouble = state.valor.replace(",", ".").toDoubleOrNull()
        if (valorDouble == null || valorDouble <= 0) {
            _uiState.update { it.copy(erro = "Informe um valor válido.") }
            return
        }
        if (state.categoriaSelecionada == null) {
            _uiState.update { it.copy(erro = "Selecione uma categoria.") }
            return
        }
        if (state.contaSelecionada == null) {
            _uiState.update { it.copy(erro = "Selecione uma conta.") }
            return
        }
        viewModelScope.launch {
            val transacao = Transacao(
                id = if (state.isEdicao) state.id else 0L,
                descricao = state.descricao,
                valor = valorDouble,
                tipo = state.tipo,
                data = state.data,
                categoriaId = state.categoriaSelecionada.id,
                contaId = state.contaSelecionada.id,
                observacao = state.observacao.ifBlank { null }
            )
            if (state.isEdicao) {
                repository.updateTransacao(transacao)
            } else {
                repository.insertTransacao(transacao)
            }
            // Sinaliza para a tela que salvou com sucesso.
            _uiState.update { it.copy(salvou = true) }
        }
    }

    fun deletar() {
        val state = _uiState.value
        if (!state.isEdicao) return
        viewModelScope.launch {
            val transacao = Transacao(
                id = state.id,
                descricao = state.descricao,
                valor = state.valor.toDoubleOrNull() ?: 0.0,
                tipo = state.tipo,
                data = state.data,
                categoriaId = state.categoriaSelecionada?.id ?: 0L,
                contaId = state.contaSelecionada?.id ?: 0L
            )
            repository.deleteTransacao(transacao)
            _uiState.update { it.copy(salvou = true) }
        }
    }

    // Limpa todos os campos para uma nova transação
    fun limpar() {
        _uiState.value = FormularioUiState()
    }

    companion object {
        fun factory(repository: FinanceiroRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return FormularioViewModel(repository) as T
                }
            }
    }
}
