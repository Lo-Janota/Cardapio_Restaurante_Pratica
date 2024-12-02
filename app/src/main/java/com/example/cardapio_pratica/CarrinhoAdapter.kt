package com.example.cardapio_pratica

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cardapio_pratica.databinding.ItemCarrinhoBinding

class CarrinhoAdapter(
    private val listaItens: MutableList<CardapioItem>,
    private val onItemRemoved: (CardapioItem, Int) -> Unit // Ação para remover item
) : RecyclerView.Adapter<CarrinhoAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemCarrinhoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CardapioItem) {
            Glide.with(binding.imgItem.context)
                .load(item.imagem) // Carrega a imagem a partir da URL
                .into(binding.imgItem) // Exibe a imagem no ImageView

            binding.txtNome.text = item.nome // Exibe o nome
            binding.txtDescricao.text = item.descricao // Exibe a descrição
            binding.txtValor.text = "R$ ${item.preco}" // Exibe o valor

            // Alteração: Ao clicar no botão de remover, chama o callback para remover o item
            binding.btnRemover.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemRemoved(item, position)  // Notifica a activity que o item foi removido
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCarrinhoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listaItens[position])
    }

    override fun getItemCount(): Int = listaItens.size
}