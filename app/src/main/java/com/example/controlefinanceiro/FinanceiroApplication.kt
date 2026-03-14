package com.example.controlefinanceiro

import android.app.Application
import com.example.controlefinanceiro.data.db.AppDatabase
import com.example.controlefinanceiro.data.entity.Categoria
import com.example.controlefinanceiro.data.entity.Conta
import com.example.controlefinanceiro.data.repository.FinanceiroRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class FinanceiroApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }

    val repository by lazy {
        FinanceiroRepository(
            database.contaDao(),
            database.categoriaDao(),
            database.transacaoDao()
        )
    }

    override fun onCreate() {
        super.onCreate()
        // Roda em background — nunca bloqueia a UI thread
        CoroutineScope(Dispatchers.IO).launch {
            popularDadosIniciais()
        }
    }

    private suspend fun popularDadosIniciais() {
        val categoriasDespesa = listOf(
            Categoria(nome = "Alimentação", tipo = "DESPESA"),
            Categoria(nome = "Transporte", tipo = "DESPESA"),
            Categoria(nome = "Entretenimento", tipo = "DESPESA"),
            Categoria(nome = "Saúde", tipo = "DESPESA"),
            Categoria(nome = "Moradia", tipo = "DESPESA"),
            Categoria(nome = "Educação", tipo = "DESPESA"),
            Categoria(nome = "Roupas", tipo = "DESPESA"),
            Categoria(nome = "Outros", tipo = "DESPESA")
        )

        val categoriasReceita = listOf(
            Categoria(nome = "Salário", tipo = "RECEITA"),
            Categoria(nome = "Freelance", tipo = "RECEITA"),
            Categoria(nome = "Investimentos", tipo = "RECEITA"),
            Categoria(nome = "Extra", tipo = "RECEITA")
        )

        val contas = listOf(
            Conta(nome = "Carteira"),
            Conta(nome = "Nubank"),
            Conta(nome = "Bradesco"),
            Conta(nome = "Inter")
        )

        // first() pega o primeiro valor emitido pelo Flow e para de escutar
        // Utilizado para verificar se os dados já existem no banco.
        val categoriasExistentes = database.categoriaDao().getAll().first()
        if (categoriasExistentes.isEmpty()) {
            categoriasDespesa.forEach { repository.insertCategoria(it) }
            categoriasReceita.forEach { repository.insertCategoria(it) }
        }

        val contasExistentes = database.contaDao().getAll().first()
        if (contasExistentes.isEmpty()) {
            contas.forEach { repository.insertConta(it) }
        }
    }
}