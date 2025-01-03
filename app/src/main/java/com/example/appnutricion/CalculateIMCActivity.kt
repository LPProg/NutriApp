package com.example.appnutricion

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CalculateIMCActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth // Instancia para manejar la autenticación con Firebase
    private lateinit var spinnerUsers: Spinner // Spinner para seleccionar un usuario
    private lateinit var etPeso: EditText // Campo para ingresar el peso
    private lateinit var etAltura: EditText // Campo para ingresar la altura
    private lateinit var btnCalcularIMC: Button // Botón para calcular el IMC
    private var usersList: List<String> = listOf() // Lista para almacenar los nombres de usuarios

    // Método que se ejecuta al crear la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculate_imc)

        auth = FirebaseAuth.getInstance() // Inicializamos FirebaseAuth

        // Referencias a los campos y botones de la interfaz
        spinnerUsers = findViewById(R.id.spinnerUsers)
        etPeso = findViewById(R.id.etPeso)
        etAltura = findViewById(R.id.etAltura)
        btnCalcularIMC = findViewById(R.id.btnCalcularIMC)

        // Llamamos a la función para cargar la lista de usuarios desde Firebase
        cargarUsuarios()

        // Configuramos el listener para el botón de calcular IMC
        btnCalcularIMC.setOnClickListener {
            val peso = etPeso.text.toString().trim() // Obtenemos el valor del peso
            val altura = etAltura.text.toString().trim() // Obtenemos el valor de la altura

            // Verificamos si los campos de peso o altura están vacíos
            if (peso.isEmpty() || altura.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Convertimos la altura a metros, verificando que sea un número válido
            val alturaEnMetros = if (altura.toDoubleOrNull() != null) {
                altura.toDouble() // Si la altura es válida, la usamos tal cual
            } else {
                Toast.makeText(this, "Altura inválida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Convertimos el peso a un número de tipo Double
            val pesoEnKg = peso.toDoubleOrNull()

            // Si el peso es inválido, mostramos un mensaje y retornamos
            if (pesoEnKg == null) {
                Toast.makeText(this, "Peso incorrecto", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Realizamos el cálculo del IMC usando la fórmula: IMC = peso / (altura^2)
            val imc = pesoEnKg / (alturaEnMetros * alturaEnMetros)
            val username = spinnerUsers.selectedItem.toString() // Obtenemos el username seleccionado del spinner

            // Llamamos a la función para enviar el IMC calculado al usuario
            enviarIMCAlUsuario(username, imc)
        }
    }

    // Función para cargar los usuarios desde Firebase y llenar el spinner
    private fun cargarUsuarios() {
        val database = FirebaseDatabase.getInstance("https://nutriapp-9f068-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("Users")

        // Obtenemos los usuarios desde Firebase
        database.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val usuarios = mutableListOf<String>()
                task.result.children.forEach {
                    val userName = it.child("username").value as String?
                    if (userName != null) {
                        usuarios.add(userName) // Agregamos el nombre de usuario a la lista
                    }
                }
                usersList = usuarios // Actualizamos la lista de usuarios
                Log.d("UsersList", "Usuarios cargados: $usuarios") // Verificar qué usuarios se están cargando
                setupSpinner() // Configuramos el spinner con la lista de usuarios
            } else {
                Toast.makeText(this, "Error al cargar usuarios", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Función para configurar el spinner con la lista de usuarios
    private fun setupSpinner() {
        if (usersList.isEmpty()) {
            Toast.makeText(this, "No hay usuarios disponibles", Toast.LENGTH_SHORT).show()
            return
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, usersList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUsers.adapter = adapter // Asignamos el adaptador al spinner
    }

    // Función para enviar el IMC calculado al usuario seleccionado
    private fun enviarIMCAlUsuario(username: String, imc: Double) {
        val database = FirebaseDatabase.getInstance("https://nutriapp-9f068-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("Users")

        // Buscamos el usuario en Firebase por su nombre de usuario
        database.orderByChild("username").equalTo(username).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userSnapshot = task.result.children.firstOrNull()
                if (userSnapshot != null) {
                    val userId = userSnapshot.key
                    Log.d("UserID", "Usuario encontrado: $userId") // Verifica el userId
                    if (userId != null) {
                        // Guardamos el IMC calculado en la base de datos de Notificaciones
                        val notificationsRef = FirebaseDatabase.getInstance()
                            .getReference("Notifications")
                            .child(userId)
                            .push() // Agregamos una nueva notificación

                        val notificationData = mapOf(
                            "type" to "IMC",
                            "message" to "Tu IMC es: ${"%.2f".format(imc)}", // Formateamos el IMC a dos decimales
                            "timestamp" to System.currentTimeMillis() // Fecha y hora del cálculo
                        )

                        // Enviamos la notificación al usuario
                        notificationsRef.setValue(notificationData).addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(this, "El IMC se ha enviado al usuario", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "Error al enviar el IMC", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "No se encontró el usuario", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Error al encontrar el usuario", Toast.LENGTH_SHORT).show()
            }
        }
    }


}

