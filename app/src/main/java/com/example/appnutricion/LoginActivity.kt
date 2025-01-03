package com.example.appnutricion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appnutricion.User.UserBaseActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

// Actividad que maneja el inicio de sesión del usuario en la aplicación
class LoginActivity : AppCompatActivity() {

    // Inicialización de FirebaseAuth para la autenticación y DatabaseReference para la base de datos
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inicializar Firebase
        FirebaseApp.initializeApp(this)  // Inicialización de la app Firebase
        auth = FirebaseAuth.getInstance()  // Obtener la instancia de FirebaseAuth para la autenticación
        database = FirebaseDatabase.getInstance("https://nutriapp-9f068-default-rtdb.europe-west1.firebasedatabase.app/").reference // Obtener la referencia de la base de datos

        // Referencias a los campos de la UI
        val etEmail: EditText = findViewById(R.id.etEmail)
        val etPassword: EditText = findViewById(R.id.etPassword)
        val btnLogin: Button = findViewById(R.id.btnLogin)
        val btnRegister: Button = findViewById(R.id.btnRegister)

        // Listener para el botón de inicio de sesión
        btnLogin.setOnClickListener {
            // Obtener los valores de los campos de texto
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Verificar si los campos están vacíos
            if (email.isEmpty() || password.isEmpty()) {
                // Si los campos están vacíos, mostrar un mensaje y detener la ejecución
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Intentar autenticar al usuario con el correo y contraseña proporcionados
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Si la autenticación es exitosa, obtener el ID del usuario
                        val userId = auth.currentUser?.uid ?: ""
                        // Llamar a la función para verificar el rol del usuario (si es Usuario o Entrenador)
                        checkUserRole(userId, email)
                    } else {
                        // Si la autenticación falla, mostrar el mensaje de error
                        Toast.makeText(this, "Error de inicio de sesión: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // Listener para el botón de registro
        btnRegister.setOnClickListener {
            // Redirigir al usuario a la pantalla de registro
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    // Función para verificar el rol del usuario en la base de datos
    // Función para verificar el rol del usuario en la base de datos
    private fun checkUserRole(userId: String, email: String) {
        // Consultar la base de datos de "Usuarios"
        val usersRef = database.child("Users")
        usersRef.get().addOnCompleteListener { userTask ->
            if (userTask.isSuccessful) {
                val userSnapshot = userTask.result
                var role: String? = null

                // Buscar en los usuarios registrados si el email coincide
                userSnapshot.children.forEach { user ->
                    val emailValue = user.child("email").value as? String
                    Log.d("LoginActivity", "Buscando en Usuarios, email: $emailValue")  // Agregar Log
                    if (emailValue == email) {
                        role = user.child("role").value as? String
                        Log.d("LoginActivity", "Rol encontrado en Usuarios: $role")  // Agregar Log
                        return@forEach
                    }
                }

                // Si el rol fue encontrado en los usuarios, proceder a manejarlo
                if (role != null) {
                    handleRole(role)
                } else {
                    // Si no se encuentra el rol en los usuarios, buscar en los entrenadores
                    val trainersRef = database.child("Trainers")
                    trainersRef.get().addOnCompleteListener { trainerTask ->
                        if (trainerTask.isSuccessful) {
                            val trainerSnapshot = trainerTask.result
                            var trainerFound = false  // Flag para indicar si encontramos al entrenador
                            trainerSnapshot.children.forEach { trainer ->
                                val emailValue = trainer.child("email").value as? String
                                Log.d("LoginActivity", "Buscando en Entrenadores, email: $emailValue")  // Agregar Log

                                if (emailValue == email) {
                                    role = trainer.child("role").value as? String
                                    Log.d("LoginActivity", "Rol encontrado en Entrenadores: $role")  // Agregar Log
                                    trainerFound = true
                                    return@forEach
                                }
                            }

                            // Si se encuentra el rol en los entrenadores, manejarlo
                            if (trainerFound && role != null) {
                                handleRole(role)
                            } else {
                                // Si no se encuentra en ninguno de los dos, mostrar mensaje de usuario no encontrado
                                Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            // Si hubo un error al consultar los entrenadores, mostrar un mensaje de error
                            Toast.makeText(this, "Error al buscar en Entrenadores: ${trainerTask.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                // Si hubo un error al consultar los usuarios, mostrar un mensaje de error
                Toast.makeText(this, "Error al buscar en Usuarios: ${userTask.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    // Función que maneja el rol del usuario después de haberlo encontrado
    private fun handleRole(role: String?) {
        // Según el rol, redirige a la actividad correspondiente
        when (role) {
            "Usuario" -> {
                // Si el rol es "Usuario", redirigir a la actividad del usuario
                startActivity(Intent(this, UserBaseActivity::class.java))
                finish()  // Terminar la actividad actual
            }
            "Entrenador" -> {
                // Si el rol es "Entrenador", redirigir a la actividad del entrenador
                startActivity(Intent(this, TrainerBaseActivity::class.java))
                finish()  // Terminar la actividad actual
            }
            else -> {
                // Si el rol no es reconocido, mostrar un mensaje de error
                Toast.makeText(this, "Rol desconocido", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
