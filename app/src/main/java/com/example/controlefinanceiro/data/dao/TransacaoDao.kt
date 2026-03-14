package com.example.controlefinanceiro.data.dao

import androidx.room.*
import com.example.controlefinanceiro.data.entity.Transacao
import kotlinx.coroutines.flow.Flow

@Dao
interface TransacaoDao {
    // Mais recente primeiro.
    @Query("SELECT * FROM transacoes ORDER BY data ASC")
    fun getAll(): Flow<List<Transacao>>

    // Usado nos filtros da tela de Transações (Receitas/Despesas).
    @Query("SELECT * FROM transacoes WHERE tipo = :tipo ORDER BY data ASC")
    fun getByType(tipo: String): Flow<List<Transacao>>

    // Soma todos os valores de uma vez só no banco. Se não houver nenhuma transação, retorna null.
    @Query("SELECT SUM(valor) FROM transacoes WHERE tipo = 'RECEITA'")
    fun getTotalReceitas(): Flow<Double?>

    @Query("SELECT SUM(valor) FROM transacoes WHERE tipo = 'DESPESA'")
    fun getTotalDespesas(): Flow<Double?>

    // Traz os 5 registros mais recentes.
    @Query("SELECT * FROM transacoes ORDER BY data DESC LIMIT 5")
    fun getUltimas(): Flow<List<Transacao>>

    // Agrupa as transações por categoria e soma os valores totais de cada grupo.
    // Resultado: uma linha por categoria com o total gasto.
    @Query("""
        SELECT categoriaId, SUM(valor) AS total
        FROM transacoes
        WHERE tipo = 'DESPESA'
        GROUP BY categoriaId
    """)
    fun getTotalPorCategoria(): Flow<List<TotalPorCategoria>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transacao: Transacao)

    @Update
    suspend fun update(transacao: Transacao)

    @Delete
    suspend fun delete(transacao: Transacao)
}

// Essa classe não é uma @Entity — ela não vira tabela
// o Room usa ela só para mapear o resultado da query de relatório
// que retorna categoriaId + total (não retorna uma Transacao completa)
data class TotalPorCategoria(
    val categoriaId: Long,
    val total: Double
)