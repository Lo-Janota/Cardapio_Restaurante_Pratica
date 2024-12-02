package com.example.cardapio_pratica

import AutenticacaoController
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cardapio_pratica.databinding.ActivityCardapioBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

class CardapioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCardapioBinding
    private lateinit var adapter: CardapioAdapter
    private val firestore = FirebaseFirestore.getInstance()

    // Instância do controlador de autenticação
    private lateinit var autenticacaoController: AutenticacaoController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configura o layout da Activity com o binding
        binding = ActivityCardapioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa o controlador de autenticação
        autenticacaoController = AutenticacaoController()

        // Configura o RecyclerView para exibir os itens do cardápio
        binding.rvCardapio.layoutManager = LinearLayoutManager(this)
        adapter = CardapioAdapter(emptyList()) { item ->
            adicionarAoCarrinho(item) // Define a ação ao clicar em um item do cardápio
        }
        binding.rvCardapio.adapter = adapter

        // Define a ação do botão de logout
        binding.imgLogout.setOnClickListener {
            autenticacaoController.logout() // Realiza logout do usuário
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Fecha a Activity atual
        }

        // Define a ação do botão de "Carrinho"
        binding.imgCarrinho.setOnClickListener {
            abrirCarrinho() // Navega para a tela de carrinho
        }

        // Carrega os itens do cardápio do Firestore
        loadCardapioItems()
    }

    // Adiciona um item ao carrinho e exibe uma mensagem de confirmação
    private fun adicionarAoCarrinho(item: CardapioItem) {
        // Obtém os itens do carrinho armazenados no SharedPreferences
        val sharedPrefs = getSharedPreferences("CarrinhoPrefs", MODE_PRIVATE)
        val editor = sharedPrefs.edit()

        // Converte o item para JSON ou um formato simples para salvar
        val itemJson = Gson().toJson(item) // Usando Gson para converter o item em String JSON
        val itemKey = "item_${System.currentTimeMillis()}" // Chave única para o item

        // Salva o item no SharedPreferences
        editor.putString(itemKey, itemJson)
        editor.apply()

        // Exibe a mensagem de sucesso
        Toast.makeText(this, "${item.nome} adicionado ao carrinho", Toast.LENGTH_SHORT).show()
    }


    // Abre a tela do carrinho de compras
    private fun abrirCarrinho() {
        val intent = Intent(this, CarrinhoActivity::class.java)
        startActivity(intent)
    }

    // Carrega os itens do cardápio armazenados no Firestore
    private fun loadCardapioItems() {
        val entradasRef = firestore.collection("cardapio").document("ENTRADAS").collection("itens")
        val smashRef = firestore.collection("cardapio").document("SMASH").collection("itens")
        val hotdogRef = firestore.collection("cardapio").document("HOTDOG").collection("itens")
        val bebidasRef = firestore.collection("cardapio").document("BEBIDAS").collection("itens")

        val allCardapioItems = mutableListOf<CardapioItem>()

        entradasRef.get()
            .addOnSuccessListener { entradasDocuments ->
                val entradasItems = entradasDocuments.map { document ->
                    document.toObject(CardapioItem::class.java)
                }
                allCardapioItems.addAll(entradasItems)

                smashRef.get()
                    .addOnSuccessListener { smashDocuments ->
                        val smashItems = smashDocuments.map { document ->
                            document.toObject(CardapioItem::class.java)
                        }
                        allCardapioItems.addAll(smashItems)

                        hotdogRef.get()
                            .addOnSuccessListener { hotdogDocuments ->
                                val hotdogItems = hotdogDocuments.map { document ->
                                    document.toObject(CardapioItem::class.java)
                                }
                                allCardapioItems.addAll(hotdogItems)

                                bebidasRef.get()
                                    .addOnSuccessListener { bebidasDocuments ->
                                        val bebidasItems = bebidasDocuments.map { document ->
                                            document.toObject(CardapioItem::class.java)
                                        }
                                        allCardapioItems.addAll(bebidasItems)

                                        // Atualiza o adapter com todos os itens
                                        adapter.updateCardapioItems(allCardapioItems)
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("CardapioActivity", "Erro ao buscar itens de bebidas", e)
                                    }
                            }
                            .addOnFailureListener { e ->
                                Log.e("CardapioActivity", "Erro ao buscar itens de hotdog", e)
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.e("CardapioActivity", "Erro ao buscar itens de smash", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("CardapioActivity", "Erro ao buscar itens de entradas", e)
            }
    }

}