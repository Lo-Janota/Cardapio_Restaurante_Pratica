package com.example.cardapio_pratica

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cardapio_pratica.databinding.ActivityCarrinhoBinding
import com.google.gson.Gson

class CarrinhoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCarrinhoBinding
    private lateinit var adapter: CarrinhoAdapter
    private val itensCarrinho = mutableListOf<CardapioItem>()
    private var totalPedido = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configura o layout da Activity com o binding
        binding = ActivityCarrinhoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura o RecyclerView para exibir os itens do carrinho
        binding.rvCarrinho.layoutManager = LinearLayoutManager(this)

        // Cria o adapter e passa a função para remoção de item
        adapter = CarrinhoAdapter(itensCarrinho) { item, position ->
            removerItemDoCarrinho(item, position) // Chama a função de remoção
        }
        binding.rvCarrinho.adapter = adapter

        // Configura o botão de voltar
        binding.imgVoltar.setOnClickListener {
            finish() // Retorna para a Activity anterior (CardapioActivity)
        }

        // Carrega os itens do carrinho do SharedPreferences
        carregarItensDoCarrinho()

        // Exibe o total do pedido
        atualizarTotal()

        // Configura o botão de finalizar pedido
        binding.btnFinalizarPedido.setOnClickListener {
            finalizarPedido()
        }
    }

    // Carrega os itens do carrinho do SharedPreferences
    private fun carregarItensDoCarrinho() {
        val sharedPrefs: SharedPreferences = getSharedPreferences("CarrinhoPrefs", MODE_PRIVATE)
        val allEntries = sharedPrefs.all

        itensCarrinho.clear() // Limpa a lista local antes de carregar
        totalPedido = 0.0 // Reseta o total

        for ((_, value) in allEntries) {
            val itemJson = value as String
            val item = Gson().fromJson(itemJson, CardapioItem::class.java)
            itensCarrinho.add(item)
            totalPedido += item.preco // Atualiza o total
        }

        adapter.notifyDataSetChanged()
        binding.txtTotal.text = "Total: R$ $totalPedido"
    }

    // Atualiza o total do pedido
    private fun atualizarTotal() {
        totalPedido = itensCarrinho.sumOf { it.preco } // Soma os preços dos itens no carrinho
        binding.txtTotal.text = "Total: R$ $totalPedido"
    }

    // Função que remove o item do carrinho
    private fun removerItemDoCarrinho(item: CardapioItem, position: Int) {
        val sharedPrefs: SharedPreferences = getSharedPreferences("CarrinhoPrefs", MODE_PRIVATE)
        val editor = sharedPrefs.edit()

        // Subtrai o valor do item removido do total
        totalPedido -= item.preco

        // Remove o item do SharedPreferences (usando o nome como chave)
        editor.remove(item.nome)
        editor.apply()

        // Remove o item da lista local
        itensCarrinho.removeAt(position)
        adapter.notifyItemRemoved(position)

        // Atualiza o total
        atualizarTotal()

        // Salva os itens restantes no SharedPreferences
        salvarItensNoCarrinho()

        Toast.makeText(this, "${item.nome} removido do carrinho", Toast.LENGTH_SHORT).show()
    }

    // Função que salva os itens do carrinho no SharedPreferences
    private fun salvarItensNoCarrinho() {
        val sharedPrefs: SharedPreferences = getSharedPreferences("CarrinhoPrefs", MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.clear() // Remove todos os itens antigos

        // Reinsere os itens restantes no SharedPreferences
        for (item in itensCarrinho) {
            val itemJson = Gson().toJson(item)
            editor.putString(item.nome, itemJson) // Usa o nome como chave
        }

        editor.apply()
    }

    // Função para finalizar o pedido
    private fun finalizarPedido() {
        Toast.makeText(this, "Pedido finalizado! Total: R$ $totalPedido", Toast.LENGTH_LONG).show()

        val sharedPrefs: SharedPreferences = getSharedPreferences("CarrinhoPrefs", MODE_PRIVATE)
        sharedPrefs.edit().clear().apply() // Limpa o SharedPreferences

        itensCarrinho.clear() // Limpa a lista local
        adapter.notifyDataSetChanged()

        totalPedido = 0.0 // Reseta o total
        binding.txtTotal.text = "Total: R$ $totalPedido"

        finish() // Fecha a activity atual
    }
}