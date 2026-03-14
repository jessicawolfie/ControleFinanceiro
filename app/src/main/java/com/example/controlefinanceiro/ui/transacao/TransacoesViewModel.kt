package com.example.controlefinanceiro.ui.transacao

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.controlefinanceiro.data.entity.Transacao
import com.example.controlefinanceiro.data.repository.FinanceiroRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

// Filtros disponíveis na tela
enum class FiltroTransacao {
    TODAS, RECEITAS, DESPESAS
}

data class TransacoesUiState(
    val transacoes: List<Transacao> = emptyList(),
    val filtroAtivo: FiltroTransacao = FiltroTransacao.TODAS,
    val isLoading: Boolean = true
)

class TransacoesViewModel(
    private val repository: FinanceiroRepository
) : ViewModel() {

    // _filtroAtivo é um Flow separado que controla qual filtro está ativo
    private val _filtroAtivo = MutableStateFlow(FiltroTransacao.TODAS)

    // combine junta o filtro ativo com as listas do banco
    val uiState: StateFlow<TransacoesUiState> = combine(
        _filtroAtivo,
        repository.todasTransacoes,
        repository.transacoesPorTipo("RECEITA"),
        repository.transacoesPorTipo("DESPESA")
    ) { filtro, todas, receitas, despesas ->
        // Seleciona a lista correta com base no filtro
        val listaFiltrada = when (filtro) {
            FiltroTransacao.TODAS -> todas
            FiltroTransacao.RECEITAS -> receitas
            FiltroTransacao.DESPESAS -> despesas
        }
        TransacoesUiState(
            transacoes = listaFiltrada,
            filtroAtivo = filtro,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TransacoesUiState()
    )

    // Chamado quando o usuário clica em um dos chips de filtro
    fun onFiltroChange(filtro: FiltroTransacao) {
        _filtroAtivo.update { filtro }
    }

    companion object {
        fun factory(repository: FinanceiroRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TransacoesViewModel(repository) as T
                }
            }
    }
}
