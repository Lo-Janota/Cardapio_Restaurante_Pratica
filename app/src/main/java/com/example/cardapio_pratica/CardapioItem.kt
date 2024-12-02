package com.example.cardapio_pratica

data class CardapioItem(
    val nome: String = "",  // Nome do item ou da categoria
    val descricao: String = "",  // Descrição do item (se vazio, trata-se de uma categoria)
    val preco: Double = 0.0,  // Preço do item
    val imagem: String = ""   // URL da imagem do item
)