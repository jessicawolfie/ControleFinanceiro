package com.example.controlefinanceiro.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.controlefinanceiro.data.dao.CategoriaDao
import com.example.controlefinanceiro.data.dao.ContaDao
import com.example.controlefinanceiro.data.dao.TransacaoDao
import com.example.controlefinanceiro.data.entity.Categoria
import com.example.controlefinanceiro.data.entity.Conta
import com.example.controlefinanceiro.data.entity.Transacao
import com.example.controlefinanceiro.util.Converters

@Database(
    entities = [
        Conta::class,
        Categoria::class,
        Transacao::class],
    version = 2,
    exportSchema = false
)

@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    // funções abstratas — o Room implementa automaticamente
    // cada uma retorna o DAO correspondente
    abstract fun contaDao(): ContaDao
    abstract fun categoriaDao(): CategoriaDao
    abstract fun transacaoDao(): TransacaoDao

    companion object{
        // @Volatile - garante que o valor de instance é sempre o mais atualizado p todas as threads
        @Volatile
        private var INSTANCE: AppDatabase? = null


        // Singleton — garante que só existe uma instância do banco.
        // Se já existir, retorna a mesma. Se não, cria uma nova.
        // Isso evita abrir múltiplas conexões com o banco
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "controle_financeiro_db"
                )

                .addMigrations(MIGRATION_1_2)
                .build()
            INSTANCE = instance
            instance
            }

        }
        // Migration - script que atualiza o banco sem perder os dados.
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                "ALTER TABLE transacoes ADD COLUMN observacao TEXT DEFAULT NULL"
                )
            }
        }


    }
}