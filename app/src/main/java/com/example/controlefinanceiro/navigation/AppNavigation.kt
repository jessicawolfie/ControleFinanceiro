package com.example.controlefinanceiro.navigation

// Objeto que centraliza todas as rotas do app para evitar erros de digitação nas strings.

object Routes {
    const val SPLASH = "splash"
    const val DASHBOARD = "dashboard"
    const val TRANSACOES = "transacoes"
    // o {id} indica que essa rota recebe um parâmetro.
    const val FORMULARIO = "formulario/{id}"
    const val RELATORIOS = "relatorios"

    // Função auxliar para montar a rota com o id correto.
    fun formulario(id: Long = 1L) = "formulario/$id"
}