package com.example.controlefinanceiro.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "transacoes",
    foreignKeys = [
        ForeignKey(
            entity = Conta::class,
            parentColumns = ["id"],
            childColumns = ["contaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Categoria::class,
            parentColumns = ["id"],
            childColumns = ["categoriaId"],
        )
    ],
    indices = [
        Index(value = ["contaId"]),
        Index(value = ["categoriaId"])
    ]
)
data class Transacao(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val descricao: String,
    val valor: Double,
    val tipo: String, // "receita" ou "despesa"
    val data: Date, // data da transação
    val categoriaId: Long,
    val contaId: Long,
    val observacao: String? = null // opcional
)