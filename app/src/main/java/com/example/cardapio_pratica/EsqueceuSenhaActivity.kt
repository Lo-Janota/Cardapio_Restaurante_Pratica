package com.example.cardapio_pratica

// Importa dependências necessárias
import AutenticacaoController
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cardapio_pratica.databinding.ActivityEsqueceuSenhaBinding

// Classe responsável pela tela de recuperação de senha
class EsqueceuSenhaActivity : AppCompatActivity() {
    // Inicializa o binding para manipular os elementos de interface da Activity
    private lateinit var binding: ActivityEsqueceuSenhaBinding
    private lateinit var ctrl: AutenticacaoController // Controlador para ações de autenticação

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Infla o layout da Activity usando View Binding
        binding = ActivityEsqueceuSenhaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Instancia o controlador de autenticação
        ctrl = AutenticacaoController()

        // Configura o clique do botão de enviar
        binding.btnEnviar.setOnClickListener {
            val email = binding.txtEmail.text.toString() // Obtém o email digitado pelo usuário

            // Chama a função de recuperação de senha no controlador
            ctrl.esqueceuSenha(email) { sucesso, erro ->
                if (sucesso) {
                    Toast.makeText(
                        this,
                        "Um e-mail de redefinição de senha foi enviado para $email.",
                        Toast.LENGTH_LONG
                    ).show()

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent) // Redireciona para a MainActivity
                    finish() // Fecha a EsqueceuSenhaActivity
                } else {
                    val errorMessage = erro?.toString() ?: "Falha desconhecida"
                    Toast.makeText(
                        this,
                        "Falha ao enviar e-mail de redefinição de senha: $errorMessage",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        binding.btnVoltar.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent) // Navega para a MainActivity
            finish() // Fecha a EsqueceuSenhaActivity
        }
    }
}
