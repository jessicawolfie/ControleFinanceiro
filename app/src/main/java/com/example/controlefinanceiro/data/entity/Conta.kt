package com.example.controlefinanceiro.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contas")
data class Conta(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nome: String
)