package com.example.controlefinanceiro.ui.relatorios

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.controlefinanceiro.data.entity.Categoria
import com.example.controlefinanceiro.data.repository.FinanceiroRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

// Agrupa o total por categoria com o nome da categoria
data class RelatorioItem(
    val categoria: Categoria,
    val total: Double,
    val percentual: Float
)

data class RelatoriosUiState (
    val totalReceitas: Double = 0.0,
    val totalDespesas: Double = 0.0,
    val relatorios: List<RelatorioItem> = emptyList(),
    val isLoading: Boolean = true
)

class RelatoriosViewModel(
    private val repository: FinanceiroRepository
) : ViewModel() {
    val uiState: StateFlow<RelatoriosUiState> = combine(
        repository.totalReceitas,
        repository.totalDespesas,
        repository.totalPorCategoria,
        repository.todasCategorias
    ) { receitas, despesas, totaisPorCategoria, categorias ->

        val totalReceitas = receitas ?: 0.0
        val totalDespesas = despesas ?: 0.0

        // Para cada total por categoria, busca o nome da categoria
        // e calcula o percentual em relação ao total de despesas
        val itens = totaisPorCategoria.mapNotNull { totalPorCategoria ->
            val categoria = categorias.find { it.id == totalPorCategoria.categoriaId }
            categoria?.let {
                RelatorioItem(
                    categoria = it,
                    total = totalPorCategoria.total,
                    // percentual = (total da categoria / total geral) * 100
                    percentual = if (totalDespesas > 0) {
                        ((totalPorCategoria.total / totalDespesas) * 100).toFloat()
                    } else {
                        0f
                    }
                )
            }
        }.sortedByDescending { it.total }

        RelatoriosUiState(
            totalReceitas = totalReceitas,
            totalDespesas = totalDespesas,
            relatorios = itens,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RelatoriosUiState()
    )

    companion object {
        fun factory(repository: FinanceiroRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return RelatoriosViewModel(repository) as T
                }
            }
    }
}
