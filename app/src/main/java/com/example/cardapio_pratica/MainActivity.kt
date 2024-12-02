package com.example.cardapio_pratica

// Importa a classe de autenticação e as dependências necessárias
import AutenticacaoController
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cardapio_pratica.databinding.ActivityMainBinding

// Classe principal responsável pela tela de login do aplicativo
class MainActivity : AppCompatActivity() {
    // Binding para acessar os elementos da interface do layout
    private lateinit var binding: ActivityMainBinding
    private lateinit var ctrl: AutenticacaoController // Controlador para gerenciar autenticação

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Infla o layout da Activity usando View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Instancia o controlador de autenticação
        ctrl = AutenticacaoController()

        // Configura a ação do botão "Entrar"
        binding.btnEntrar.setOnClickListener {
            val email = binding.txtEmail.text.toString() // Obtém o e-mail digitado pelo usuário
            val senha = binding.txtSenha.text.toString() // Obtém a senha digitada pelo usuário

            try {
                // Realiza o login usando o controlador de autenticação
                ctrl.login(email, senha) { sucesso, erro ->
                    if (sucesso) {
                        // Se o login for bem-sucedido, navega para a tela do cardápio
                        val intent = Intent(this, CardapioActivity::class.java)
                        startActivity(intent)
                        finish() // Finaliza a tela de login para não permitir retorno
                    } else {
                        // Caso contrário, exibe uma mensagem de erro
                        val errorMessage = erro ?: "Erro desconhecido"
                        Toast.makeText(this, "Erro no login: $errorMessage", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                // Captura exceções inesperadas e registra no log
                Log.e("MainActivity", "Erro no login", e)
                Toast.makeText(this, "Erro ao tentar fazer login", Toast.LENGTH_LONG).show()
            }
        }

        // Configura a ação do texto "Cadastrar" para abrir a tela de cadastro
        binding.txtCadastrar.setOnClickListener {
            val intent = Intent(this, CadastrarActivity::class.java)
            startActivity(intent)
        }

        // Configura a ação do texto "Esqueceu Senha" para abrir a tela de recuperação de senha
        binding.txtEsqueceuSenha.setOnClickListener {
            val intent = Intent(this, EsqueceuSenhaActivity::class.java)
            startActivity(intent)
        }
    }
}