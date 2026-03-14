package com.example.controlefinanceiro.data.dao

import androidx.room.*
import com.example.controlefinanceiro.data.entity.Categoria
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao {
    @Query("SELECT * FROM categorias ORDER BY nome ASC")
    fun getAll(): Flow<List<Categoria>>

    @Query("SELECT * FROM CATEGORIAS WHERE tipo = :tipo ORDER BY nome ASC")
    fun getByType(tipo: String): Flow<List<Categoria>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(categoria: Categoria)

    @Delete
    suspend fun delete(categoria: Categoria)
}
