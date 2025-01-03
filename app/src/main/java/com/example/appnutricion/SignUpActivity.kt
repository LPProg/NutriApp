package com.example.appnutricion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth  // Instancia de FirebaseAuth para autenticación

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        auth = FirebaseAuth.getInstance()  // Inicializamos FirebaseAuth

        // Inicialización de las vistas de los campos de entrada
        val etName: EditText = findViewById(R.id.etName)  // Campo para el nombre
        val etLastName: EditText = findViewById(R.id.etSurname)  // Campo para el apellido
        val etUsername: EditText = findViewById(R.id.etUsername)  // Campo para el nombre de usuario
        val etEmail: EditText = findViewById(R.id.etEmail)  // Campo para el correo electrónico
        val etPassword: EditText = findViewById(R.id.etPassword)  // Campo para la contraseña
        val rgRole: RadioGroup = findViewById(R.id.rgRole)  // Grupo de botones de radio para seleccionar el rol
        val btnSignUp: Button = findViewById(R.id.btnRegister)  // Botón de registro

        // Acción al hacer clic en el botón de registro
        btnSignUp.setOnClickListener {
            // Obtener los datos de los campos de entrada
            val name = etName.text.toString().trim()
            val lastName = etLastName.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val selectedRoleId = rgRole.checkedRadioButtonId  // Obtener el ID del botón de radio seleccionado

            // Validación de campos vacíos
            if (name.isEmpty() || lastName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || selectedRoleId == -1) {
                // Si falta algún campo, mostramos un mensaje de error
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener  // Salir del método si algún campo está vacío
            }

            // Determinar el rol según el botón de radio seleccionado
            val role = if (selectedRoleId == R.id.rbUser) "Usuario" else "Entrenador"

            // Verificar si el nombre de usuario ya está registrado
            val database = FirebaseDatabase.getInstance().reference
            database.child("Usernames").child(username).get()  // Consultamos si el nombre de usuario ya existe
                .addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        // Si el nombre de usuario ya existe, mostramos un mensaje de error
                        Toast.makeText(this, "Este nombre de usuario ya está registrado", Toast.LENGTH_SHORT).show()
                    } else {
                        // Si el nombre de usuario no existe, continuamos con el registro
                        registerUser(name, lastName, email, password, role, username)
                    }
                }
        }
    }

    // Función para registrar al usuario en Firebase
    private fun registerUser(name: String, lastName: String, email: String, password: String, role: String, username: String) {
        // Crear un usuario con el correo electrónico y la contraseña proporcionados
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Si el registro fue exitoso, obtenemos el ID del usuario
                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener  // Si no hay ID, salimos de la función

                    // Verificar el rol antes de enviarlo
                    Log.d("SignUpActivity", "Rol seleccionado: $role")

                    // Crear un mapa con los datos del usuario, incluyendo el rol
                    val user = mapOf(
                        "name" to name,
                        "lastName" to lastName,
                        "username" to username,
                        "email" to email,
                        "role" to role, // Incluimos el rol en los datos del usuario
                        "age" to "",
                        "bio" to "",
                        "height" to "",
                        "weight" to ""
                    )

                    // Guardar los datos del usuario en Firebase dependiendo del rol
                    val database = FirebaseDatabase.getInstance().reference
                    if (role == "Entrenador") {
                        // Si el rol es "Entrenador", lo guardamos en el nodo "Trainers"
                        Log.d("SignUpActivity", "Guardando en el nodo 'Trainers' con el rol $role")
                        database.child("Trainers").child(userId).setValue(user)
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    // Si se guardó correctamente, mostramos un mensaje y redirigimos al Login
                                    Toast.makeText(this, "Entrenador registrado con éxito", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, LoginActivity::class.java))  // Redirige a la pantalla de login
                                    finish()  // Cierra la actividad actual
                                } else {
                                    // Si hubo un error al guardar los datos, mostramos un mensaje de error
                                    Toast.makeText(this, "Error al guardar datos del entrenador: ${dbTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        // Si el rol es "Usuario", lo guardamos en el nodo "Users"
                        Log.d("SignUpActivity", "Guardando en el nodo 'Users' con el rol $role")
                        database.child("Users").child(userId).setValue(user)
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    // Si se guardó correctamente, mostramos un mensaje y redirigimos al Login
                                    Toast.makeText(this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, LoginActivity::class.java))  // Redirige a la pantalla de login
                                    finish()  // Cierra la actividad actual
                                } else {
                                    // Si hubo un error al guardar los datos, mostramos un mensaje de error
                                    Toast.makeText(this, "Error al guardar datos del usuario: ${dbTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                } else {
                    // Si el registro en Firebase falló, mostramos un mensaje de error
                    Toast.makeText(this, "Error al registrarse: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

}
