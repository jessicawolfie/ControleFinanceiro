package com.example.controlefinanceiro.data.repository

import com.example.controlefinanceiro.data.dao.CategoriaDao
import com.example.controlefinanceiro.data.dao.ContaDao
import com.example.controlefinanceiro.data.dao.TransacaoDao
import com.example.controlefinanceiro.data.dao.TotalPorCategoria
import com.example.controlefinanceiro.data.entity.Categoria
import com.example.controlefinanceiro.data.entity.Conta
import com.example.controlefinanceiro.data.entity.Transacao
import kotlinx.coroutines.flow.Flow

class FinanceiroRepository(
    private val contaDao: ContaDao,
    private val categoriaDao: CategoriaDao,
    private val transacaoDao: TransacaoDao
) {
    val todasContas: Flow<List<Conta>> = contaDao.getAll()

    // Contas
    suspend fun insertConta(conta: Conta) = contaDao.insert(conta)
    suspend fun deleteConta(conta: Conta) = contaDao.delete(conta)

    // Categorias
    val todasCategorias: Flow<List<Categoria>> = categoriaDao.getAll()

    fun categoriasPorTipo(tipo: String): Flow<List<Categoria>> = categoriaDao.getByType(tipo)

    suspend fun insertCategoria(categoria: Categoria) = categoriaDao.insert(categoria)
    suspend fun deleteCategoria(categoria: Categoria) = categoriaDao.delete(categoria)

    // Transações
    val todasTransacoes: Flow<List<Transacao>> = transacaoDao.getAll()
    val ultimasTransacoes: Flow<List<Transacao>> = transacaoDao.getUltimas()
    val totalReceitas: Flow<Double?> = transacaoDao.getTotalReceitas()
    val totalDespesas: Flow<Double?> = transacaoDao.getTotalDespesas()
    val totalPorCategoria: Flow<List<TotalPorCategoria>> = transacaoDao.getTotalPorCategoria()

    fun transacoesPorTipo(tipo: String): Flow<List<Transacao>> = transacaoDao.getByType(tipo)

    suspend fun insertTransacao(transacao: Transacao) = transacaoDao.insert(transacao)

    suspend fun updateTransacao(transacao: Transacao) = transacaoDao.update(transacao)

    suspend fun deleteTransacao(transacao: Transacao) = transacaoDao.delete(transacao)
}