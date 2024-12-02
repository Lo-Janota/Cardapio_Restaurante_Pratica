package com.example.cardapio_pratica

data class CarrinhoItem(
    val nome: String,
    val preco: Double,
    var quantidade: Int = 1
)