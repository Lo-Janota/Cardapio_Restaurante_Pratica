import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Classe responsável por gerenciar operações de autenticação e interação com o Firestore
class AutenticacaoController {

    // Instâncias para autenticação e banco de dados Firestore
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Função para gerar o hash da senha
    fun hashPassword(password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val hashBytes = md.digest(password.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    // Função para login do usuário com email e senha
    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        try {
            // Autentica o usuário com Firebase Authentication
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Recupera o ID do usuário autenticado
                        val userId = firebaseAuth.currentUser?.uid
                        if (userId != null) {
                            // Verifica se o email existe no Firestore
                            firestore.collection("usuarios").document(userId)
                                .get()
                                .addOnSuccessListener { document ->
                                    if (document.exists()) {
                                        // Login bem-sucedido e email encontrado
                                        onResult(true, null)
                                    } else {
                                        // Email não encontrado no banco de dados
                                        onResult(false, "Email não encontrado no banco de dados")
                                    }
                                }
                                .addOnFailureListener { e ->
                                    // Loga o erro e retorna mensagem de falha
                                    Log.e("AutenticacaoController", "Erro ao verificar email no Firestore", e)
                                    onResult(false, "Erro ao verificar email no banco de dados")
                                }
                        }
                    } else {
                        // Login falhou, retorna mensagem de erro
                        val errorMessage = task.exception?.message
                        onResult(false, errorMessage)
                    }
                }
        } catch (e: Exception) {
            // Captura exceções e loga o erro
            Log.e("AutenticacaoController", "Erro no login", e)
            onResult(false, "Erro ao tentar fazer login")
        }
    }

    fun criarUsuario(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        try {
            // Cria o usuário no Firebase Authentication
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Recupera o ID do usuário recém-criado
                        val userId = firebaseAuth.currentUser?.uid
                        // Formata a data atual para registro
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                        val currentDate = dateFormat.format(Date())

                        // Criptografa a senha com hash
                        val hashedPassword = hashPassword(password)

                        // Cria os dados do usuário para salvar no Firestore
                        val userData = hashMapOf(
                            "email" to email,
                            "dataCriacao" to currentDate,  // Data de criação do usuário
                            "senha" to hashedPassword  // Senha com hash
                        )

                        // Salva os dados no Firestore
                        userId?.let {
                            firestore.collection("usuarios").document(it)
                                .set(userData)
                                .addOnSuccessListener {
                                    // Dados salvos com sucesso
                                    Log.d("AutenticacaoController", "Usuário salvo no Firestore com sucesso")
                                    onResult(true, null)
                                }
                                .addOnFailureListener { e ->
                                    // Falha ao salvar os dados
                                    Log.e("AutenticacaoController", "Erro ao salvar usuário no Firestore", e)
                                    onResult(false, "Erro ao salvar dados do usuário")
                                }
                        } ?: onResult(false, "Erro ao obter ID do usuário")
                    } else {
                        // Falha ao criar usuário no Firebase Authentication
                        val errorMessage = task.exception?.message
                        onResult(false, errorMessage)
                    }
                }
        } catch (e: Exception) {
            // Captura exceções e loga o erro
            Log.e("AutenticacaoController", "Erro ao criar usuário", e)
            onResult(false, "Erro ao criar usuário")
        }
    }


    // Função para enviar e-mail de redefinição de senha
    fun esqueceuSenha(email: String, onResult: (Boolean, String?) -> Unit) {
        try {
            // Envia o e-mail de redefinição de senha
            firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // E-mail enviado com sucesso
                        onResult(true, null)
                    } else {
                        // Falha ao enviar e-mail
                        val errorMessage = task.exception?.message
                        onResult(false, errorMessage)
                    }
                }
        } catch (e: Exception) {
            // Captura exceções e loga o erro
            Log.e("AutenticacaoController", "Erro ao enviar e-mail de redefinição", e)
            onResult(false, "Erro ao enviar e-mail de redefinição")
        }
    }

    // Função para verificar o usuário atualmente autenticado
    fun usuarioAutenticado(): String? {
        return try {
            // Retorna o email do usuário autenticado
            firebaseAuth.currentUser?.email
        } catch (e: Exception) {
            // Captura exceções e loga o erro
            Log.e("AutenticacaoController", "Erro ao obter usuário autenticado", e)
            null
        }
    }

    // Função para deslogar o usuário
    fun logout() {
        try {
            // Realiza o logout
            firebaseAuth.signOut()
        } catch (e: Exception) {
            // Captura exceções e loga o erro
            Log.e("AutenticacaoController", "Erro ao fazer logout", e)
        }
    }
}