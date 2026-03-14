package com.example.controlefinanceiro.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.controlefinanceiro.data.entity.Transacao
import com.example.controlefinanceiro.data.repository.FinanceiroRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn


data class DashboardUiState(
    val totalReceitas: Double = 0.0,
    val totalDespesas: Double = 0.0,
    val saldoAtual: Double = 0.0,
    val ultimasTransacoes: List<Transacao> = emptyList(),
    val isLoading: Boolean = true
)

class DashboardViewModel(
    private val repository: FinanceiroRepository
) : ViewModel() {

    // combine junta múltiplos Flows em um só
    // sempre que qualquer um dos 3 Flows emitir um valor novo,
    // o bloco é executado e a UI recebe o estado atualizado
    val uiState: StateFlow<DashboardUiState> = combine(
        repository.totalReceitas,
        repository.totalDespesas,
        repository.ultimasTransacoes
    ){ receitas, despesas, ultimas ->

        val totalRceitas = receitas ?: 0.0
        val totalDespesas = despesas ?: 0.0

        DashboardUiState(
            totalReceitas = totalRceitas,
            totalDespesas = totalDespesas,
            // Saldo = receitas - despesas
            saldoAtual = totalRceitas - totalDespesas,
            ultimasTransacoes = ultimas,
            isLoading = false
        )
    }.stateIn(
        // stateIn converte o Flow em StateFlow
        // StateFlow sempre tem um valor atual — a UI nunca fica sem estado
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState() // Estado inicial enquanto carrega
    )

    // ViewModelProvider.Factory é necessário porque o ViewModel
    // recebe parâmetros no construtor (o repository)
    // sem isso, o Android não saberia como criar o ViewModel
    companion object {
    fun factory(repository: FinanceiroRepository): ViewModelProvider.Factory =
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return DashboardViewModel(repository) as T
            }
        }
    }
}
