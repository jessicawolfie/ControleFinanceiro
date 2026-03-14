package com.example.controlefinanceiro.data.dao

import androidx.room.*
import com.example.controlefinanceiro.data.entity.Conta
import kotlinx.coroutines.flow.Flow

@Dao
interface ContaDao {
    @Query("SELECT * FROM contas ORDER BY nome ASC")
    fun getAll(): Flow<List<Conta>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(conta: Conta)

    @Delete
    suspend fun delete(conta: Conta)
}
