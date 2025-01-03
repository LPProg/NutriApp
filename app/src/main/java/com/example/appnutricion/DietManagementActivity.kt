package com.example.appnutricion.Diet

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appnutricion.R
import com.google.firebase.database.*

class DietManagementActivity : AppCompatActivity() {

    // Declaración de las vistas de la UI
    private lateinit var spinnerDayOfWeek: Spinner
    private lateinit var spinnerUser: Spinner
    private lateinit var etBreakfast: EditText
    private lateinit var etLunch: EditText
    private lateinit var etSnack: EditText
    private lateinit var etDinner: EditText
    private lateinit var btnSaveDay: Button
    private lateinit var database: DatabaseReference
    private lateinit var usersList: MutableList<String> // Lista para almacenar los usuarios

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diet_management)

        // Inicializamos las vistas con sus respectivos elementos de la UI
        spinnerDayOfWeek = findViewById(R.id.spinnerDayOfWeek)
        spinnerUser = findViewById(R.id.spinnerUser)  // Spinner para seleccionar usuario
        etBreakfast = findViewById(R.id.etBreakfast)
        etLunch = findViewById(R.id.etLunch)
        etSnack = findViewById(R.id.etSnack)
        etDinner = findViewById(R.id.etDinner)
        btnSaveDay = findViewById(R.id.btnSaveDay)

        // Inicializamos la lista de usuarios (vacía al principio)
        usersList = mutableListOf()

        // Inicializamos Firebase Database
        database = FirebaseDatabase.getInstance().reference

        // Cargamos el spinner de días de la semana
        val daysOfWeek = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
        val dayAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            daysOfWeek
        )
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDayOfWeek.adapter = dayAdapter

        // Cargamos el spinner de usuarios desde Firebase
        loadUsersFromFirebase()

        // Configuramos el evento para el botón que guarda la dieta
        btnSaveDay.setOnClickListener {
            saveDietDay()  // Llamamos al método que guarda la dieta al hacer clic en el botón
        }
    }

    // Función para cargar la lista de usuarios desde Firebase
    private fun loadUsersFromFirebase() {
        // Consultamos la base de datos de Firebase para obtener los usuarios
        database.child("Users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                usersList.clear()  // Limpiamos la lista de usuarios

                // Verificamos si existen datos de usuarios en Firebase
                if (dataSnapshot.exists()) {
                    for (userSnapshot in dataSnapshot.children) {
                        // Obtenemos el nombre de usuario de cada usuario
                        val username = userSnapshot.child("username").getValue(String::class.java)
                        username?.let { usersList.add(it) }  // Añadimos el nombre de usuario a la lista
                    }

                    // Si hay usuarios, los mostramos en el spinner
                    if (usersList.isNotEmpty()) {
                        val userAdapter = ArrayAdapter(
                            this@DietManagementActivity,
                            android.R.layout.simple_spinner_item,
                            usersList
                        )
                        userAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinnerUser.adapter = userAdapter
                    } else {
                        // Si no hay usuarios, mostramos un mensaje
                        Toast.makeText(this@DietManagementActivity, "No hay usuarios disponibles", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Si no se encontraron usuarios en la base de datos, mostramos un mensaje
                    Toast.makeText(this@DietManagementActivity, "No se encontraron datos de usuarios", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // En caso de error al cargar los usuarios, mostramos un mensaje y lo registramos en los logs
                Log.e("FirebaseError", "Error al cargar los usuarios: ${databaseError.message}")
                Toast.makeText(this@DietManagementActivity, "Error al cargar los usuarios", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Función para guardar la dieta seleccionada
    private fun saveDietDay() {
        // Obtenemos los valores seleccionados o ingresados por el administrador
        val selectedDay = spinnerDayOfWeek.selectedItem.toString()
        val selectedUsername = spinnerUser.selectedItem.toString()
        val breakfast = etBreakfast.text.toString()
        val lunch = etLunch.text.toString()
        val snack = etSnack.text.toString()
        val dinner = etDinner.text.toString()

        // Validamos que los campos de dieta no estén vacíos
        if (breakfast.isBlank() || lunch.isBlank() || snack.isBlank() || dinner.isBlank()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Buscamos el ID del usuario seleccionado para asociar la dieta
        getUserIdByUsername(selectedUsername) { userId ->
            if (userId != null) {
                // Si encontramos el ID del usuario, guardamos la dieta
                saveDietForUser(userId, selectedDay, breakfast, lunch, snack, dinner)
            } else {
                // Si no encontramos el ID del usuario, mostramos un mensaje de error
                Toast.makeText(this, "No se pudo obtener el ID del usuario", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Función para obtener el ID del usuario a partir de su nombre de usuario
    private fun getUserIdByUsername(username: String, callback: (String?) -> Unit) {
        // Realizamos una consulta en Firebase para buscar el ID del usuario basado en su nombre de usuario
        database.child("Users").orderByChild("username").equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Si encontramos el usuario, devolvemos su ID
                    if (snapshot.exists()) {
                        for (childSnapshot in snapshot.children) {
                            callback(childSnapshot.key)  // El ID del usuario es la clave del nodo
                            return
                        }
                    } else {
                        // Si no se encuentra el usuario, devolvemos null
                        callback(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // En caso de error al obtener el ID, devolvemos null
                    callback(null)
                }
            })
    }

    // Función para guardar la dieta de un usuario
    private fun saveDietForUser(userId: String, day: String, breakfast: String, lunch: String, snack: String, dinner: String) {
        // Creamos un mapa con los detalles de la dieta
        val diet = mapOf(
            "breakfast" to breakfast,
            "lunch" to lunch,
            "snack" to snack,
            "dinner" to dinner
        )

        // Guardamos la dieta en la base de datos de Firebase bajo el usuario y el día seleccionado
        database.child("Diets").child(userId).child(day).setValue(diet)
            .addOnSuccessListener {
                // Si se guarda correctamente, mostramos un mensaje y limpiamos los campos
                Toast.makeText(this, "Dieta guardada exitosamente", Toast.LENGTH_SHORT).show()
                clearFields()
            }
            .addOnFailureListener {
                // Si ocurre un error al guardar, mostramos un mensaje de error
                Toast.makeText(this, "Error al guardar la dieta", Toast.LENGTH_SHORT).show()
                Log.e("FirebaseError", it.message.toString())
            }
    }

    // Función para limpiar los campos de texto después de guardar la dieta
    private fun clearFields() {
        etBreakfast.text.clear()
        etLunch.text.clear()
        etSnack.text.clear()
        etDinner.text.clear()
    }
}
