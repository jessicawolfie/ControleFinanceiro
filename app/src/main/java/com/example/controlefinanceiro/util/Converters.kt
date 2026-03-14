package com.example.controlefinanceiro.util

import androidx.room.TypeConverter
import java.util.Date

class Converters {
    // Esse método é chamado automaticamente pelo Room sempre que ele for salvar uma date no banco
    @TypeConverter
    fun fromDate(date: Date?): Long? = date?.time

    // Esse método é chamado automaticamente pelo Room sempre que ele for ler uma date do banco
    @TypeConverter
    fun toDate(timestamp: Long?): Date? = timestamp?.let { Date(it)}
}