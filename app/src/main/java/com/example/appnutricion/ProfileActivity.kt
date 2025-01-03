package com.example.appnutricion

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var etName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etUsername: EditText
    private lateinit var etEmail: EditText
    private lateinit var etAge: EditText
    private lateinit var etWeight: EditText
    private lateinit var etHeight: EditText
    private lateinit var etBio: EditText
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Vincular vistas del layout con variables
        etName = findViewById(R.id.etName)
        etLastName = findViewById(R.id.etLastName)
        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        etAge = findViewById(R.id.etAge)
        etWeight = findViewById(R.id.etWeight)
        etHeight = findViewById(R.id.etHeight)
        etBio = findViewById(R.id.etBio)
        btnSave = findViewById(R.id.btnSave)

        // Verificar si el usuario está autenticado
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "No user authenticated", Toast.LENGTH_SHORT).show()
            finish()  // Salir de la actividad si no hay usuario autenticado
            return
        }

        // Obtener el uid del usuario autenticado
        val userId = currentUser.uid

        // Cargar el perfil del usuario
        loadUserProfile(userId)

        // Configurar el botón para guardar cambios en el perfil
        btnSave.setOnClickListener {
            saveUserProfile(userId)
        }
    }

    // Método para cargar el perfil del usuario desde Firebase usando el uid
    // Método para cargar el perfil del usuario desde Firebase usando el uid
    private fun loadUserProfile(userId: String) {
        val database = FirebaseDatabase.getInstance().reference

        // Primero intentar buscar en el nodo "Users"
        database.child("Users").child(userId).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // Si el usuario existe en el nodo "Users", cargamos sus datos
                val userData = snapshot.value as? Map<String, Any?>
                if (userData != null) {
                    val profile = Profile(
                        name = userData["name"] as? String,
                        lastName = userData["lastName"] as? String,
                        username = userData["username"] as? String,
                        email = userData["email"] as? String,
                        age = userData["age"] as? Int,
                        weight = userData["weight"] as? Double,
                        height = userData["height"] as? Double,
                        bio = userData["bio"] as? String,
                        role = userData["role"] as? String // Obtener el 'role' del usuario
                    )
                    fillProfileFields(profile) // Llenar los campos con los datos del perfil
                }
            } else {
                // Si no se encuentra el perfil en "Users", intentar cargarlo desde "Trainers"
                loadTrainerProfile(userId)
            }
        }.addOnFailureListener {
            // Manejar errores en la carga del perfil desde "Users"
            Toast.makeText(this, "Error loading profile from Users", Toast.LENGTH_SHORT).show()
            // Intentar cargar el perfil desde "Trainers" en caso de error
            loadTrainerProfile(userId)
        }
    }

    // Método para cargar el perfil del usuario desde el nodo "Trainers"
    private fun loadTrainerProfile(userId: String) {
        val database = FirebaseDatabase.getInstance().reference

        // Buscar en el nodo "Trainers" usando el uid del usuario
        database.child("Trainers").child(userId).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // Si el usuario existe en el nodo "Trainers", cargamos sus datos
                val userData = snapshot.value as? Map<String, Any?>
                if (userData != null) {
                    val profile = Profile(
                        name = userData["name"] as? String,
                        lastName = userData["lastName"] as? String,
                        username = userData["username"] as? String,
                        email = userData["email"] as? String,
                        age = userData["age"] as? Int,
                        weight = userData["weight"] as? Double,
                        height = userData["height"] as? Double,
                        bio = userData["bio"] as? String,
                        role = userData["role"] as? String // Obtener el 'role' del usuario
                    )
                    fillProfileFields(profile) // Llenar los campos con los datos del perfil
                }
            } else {
                // Si el perfil no se encuentra en "Trainers" ni en "Users"
                Toast.makeText(this, "User profile not found in Trainers", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            // Manejar errores en la carga del perfil desde "Trainers"
            Toast.makeText(this, "Error loading profile from Trainers", Toast.LENGTH_SHORT).show()
        }
    }


    // Método para rellenar los campos del formulario con los datos del perfil
    private fun fillProfileFields(profile: Profile) {
        etName.setText(profile.name ?: "")
        etLastName.setText(profile.lastName ?: "")
        etUsername.setText(profile.username ?: "")
        etEmail.setText(profile.email ?: "")

        // Campos no editables
        etName.isFocusableInTouchMode = false
        etLastName.isFocusableInTouchMode = false
        etUsername.isFocusableInTouchMode = false
        etEmail.isFocusableInTouchMode = false

        // Rellenar los campos editables con datos del perfil
        etAge.setText(profile.age?.toString() ?: "")
        etWeight.setText(profile.weight?.toString() ?: "")
        etHeight.setText(profile.height?.toString() ?: "")
        etBio.setText(profile.bio ?: "")
    }

    // Método para guardar los datos del perfil actualizado en Firebase
    // Método para guardar los datos del perfil actualizado en Firebase
    private fun saveUserProfile(userId: String) {
        // Obtener los valores de los campos de texto
        val name = etName.text.toString().trim()
        val lastName = etLastName.text.toString().trim()
        val username = etUsername.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val age = etAge.text.toString().toIntOrNull()
        val weight = etWeight.text.toString().toDoubleOrNull()
        val height = etHeight.text.toString().toDoubleOrNull()
        val bio = etBio.text.toString().trim()

        // Validar que los campos obligatorios estén completos
        if (name.isEmpty() || lastName.isEmpty() || username.isEmpty() || email.isEmpty() || bio.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val database = FirebaseDatabase.getInstance().reference

        // Primero verificar si el usuario está en "Users"
        database.child("Users").child(userId).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // Si el usuario existe en "Users", actualizar allí
                updateUserProfileInUsers(userId, name, lastName, username, email, age, weight, height, bio, snapshot)
            } else {
                // Si no se encuentra en "Users", verificar en "Trainers"
                database.child("Trainers").child(userId).get().addOnSuccessListener { trainerSnapshot ->
                    if (trainerSnapshot.exists()) {
                        // Si el usuario existe en "Trainers", actualizar allí
                        updateUserProfileInTrainers(userId, name, lastName, username, email, age, weight, height, bio, trainerSnapshot)
                    } else {
                        // Si no se encuentra en "Users" ni en "Trainers"
                        Toast.makeText(this, "User not found in either Users or Trainers", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    // Manejar errores al leer los datos del usuario en "Trainers"
                    Toast.makeText(this, "Error reading trainer data", Toast.LENGTH_SHORT).show()
                }
            }
        }.addOnFailureListener {
            // Manejar errores al leer los datos del usuario en "Users"
            Toast.makeText(this, "Error reading user data", Toast.LENGTH_SHORT).show()
        }
    }

    // Método para actualizar el perfil del usuario en "Users"
    private fun updateUserProfileInUsers(
        userId: String,
        name: String,
        lastName: String,
        username: String,
        email: String,
        age: Int?,
        weight: Double?,
        height: Double?,
        bio: String,
        snapshot: DataSnapshot
    ) {
        val role = snapshot.child("role").getValue(String::class.java)

        // Crear un mapa para actualizar solo los campos relevantes
        val updatedFields = mutableMapOf<String, Any?>(
            "name" to name,
            "lastName" to lastName,
            "username" to username,
            "email" to email,
            "bio" to bio,
            "age" to (age ?: snapshot.child("age").getValue(Int::class.java)),
            "weight" to (weight ?: snapshot.child("weight").getValue(Double::class.java)),
            "height" to (height ?: snapshot.child("height").getValue(Double::class.java))
        )

        // Asegurarse de que el campo 'role' no se borre
        if (role != null) {
            updatedFields["role"] = role // Mantener el 'role' tal como está
        }

        // Realizar la actualización en la base de datos
        FirebaseDatabase.getInstance().reference.child("Users").child(userId).updateChildren(updatedFields)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile updated successfully in Users", Toast.LENGTH_SHORT).show()

                // Cargar nuevamente el perfil actualizado
                loadUserProfile(userId)  // Refresh the profile after update
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error updating profile in Users", Toast.LENGTH_SHORT).show()
            }
    }

    // Método para actualizar el perfil del usuario en "Trainers"
    private fun updateUserProfileInTrainers(
        userId: String,
        name: String,
        lastName: String,
        username: String,
        email: String,
        age: Int?,
        weight: Double?,
        height: Double?,
        bio: String,
        snapshot: DataSnapshot
    ) {
        val role = snapshot.child("role").getValue(String::class.java)

        // Crear un mapa para actualizar solo los campos relevantes
        val updatedFields = mutableMapOf<String, Any?>(
            "name" to name,
            "lastName" to lastName,
            "username" to username,
            "email" to email,
            "bio" to bio,
            "age" to (age ?: snapshot.child("age").getValue(Int::class.java)),
            "weight" to (weight ?: snapshot.child("weight").getValue(Double::class.java)),
            "height" to (height ?: snapshot.child("height").getValue(Double::class.java))
        )

        // Asegurarse de que el campo 'role' no se borre
        if (role != null) {
            updatedFields["role"] = role // Mantener el 'role' tal como está
        }

        // Realizar la actualización en la base de datos
        FirebaseDatabase.getInstance().reference.child("Trainers").child(userId).updateChildren(updatedFields)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile updated successfully in Trainers", Toast.LENGTH_SHORT).show()

                // Cargar nuevamente el perfil actualizado
                loadUserProfile(userId)  // Refresh the profile after update
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error updating profile in Trainers", Toast.LENGTH_SHORT).show()
            }
    }
}
