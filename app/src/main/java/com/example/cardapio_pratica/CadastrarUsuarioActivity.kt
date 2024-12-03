package com.example.cardapio_pratica

// Importa a classe de autenticação e outras dependências necessárias
import AutenticacaoController
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cardapio_pratica.databinding.ActivityCadastrarBinding

// Classe da Activity responsável pelo cadastro de novos usuários
class CadastrarUsuarioActivity : AppCompatActivity() {

    // Binding para acessar os elementos de layout da Activity
    private lateinit var binding: ActivityCadastrarBinding

    // Instância do controlador de autenticação
    private lateinit var ctrl: AutenticacaoController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa o binding para vincular os elementos do layout
        binding = ActivityCadastrarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa o controlador de autenticação
        ctrl = AutenticacaoController()

        // Define a ação do botão "Salvar" ao ser clicado
        binding.btnSalvar.setOnClickListener {
            // Obtém os valores dos campos de email e senha do usuário
            val email = binding.txtEmail.text.toString()
            val senha = binding.txtSenha.text.toString()

            try {
                // Chama a função de criação de usuário no controlador
                ctrl.criarUsuario(email, senha) { sucesso, erro ->
                    if (sucesso) {
                        // Exibe mensagem de sucesso e encerra a Activity
                        Toast.makeText(this, "Usuário criado com sucesso", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        // Exibe mensagem de erro com a descrição do problema
                        val errorMessage = erro ?: "Erro desconhecido"
                        Toast.makeText(this, "Erro ao criar usuário: $errorMessage", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                // Trata exceções inesperadas e exibe uma mensagem genérica de erro
                Log.e("CadastrarActivity", "Erro ao criar usuário", e)
                Toast.makeText(this, "Erro ao criar usuário", Toast.LENGTH_LONG).show()
            }
        }
    }
}