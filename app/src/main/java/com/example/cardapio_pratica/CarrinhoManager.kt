package com.example.cardapio_pratica

object CarrinhoManager {
    private val itensNoCarrinho = mutableListOf<CarrinhoItem>()

    fun adicionarAoCarrinho(item: CarrinhoItem) {
        val existente = itensNoCarrinho.find { it.nome == item.nome }
        if (existente != null) {
            // Incrementa a quantidade se o item já estiver no carrinho
            existente.quantidade += 1
        } else {
            // Adiciona o item novo
            itensNoCarrinho.add(item)
        }
    }

    fun obterCarrinho(): List<CarrinhoItem> = itensNoCarrinho

    fun calcularTotal(): Double = itensNoCarrinho.sumOf { it.preco * it.quantidade }

    fun limparCarrinho() {
        itensNoCarrinho.clear()
    }
}