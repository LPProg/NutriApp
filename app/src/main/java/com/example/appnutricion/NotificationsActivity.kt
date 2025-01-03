package com.example.appnutricion

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import com.example.appnutricion.R
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class NotificationsActivity : AppCompatActivity() {

    private lateinit var mDatabase: DatabaseReference  // Referencia a la base de datos de Firebase
    private lateinit var mAuth: FirebaseAuth  // Instancia de autenticación de Firebase para obtener el usuario actual
    private lateinit var notificationsListView: ListView  // Vista que muestra la lista de notificaciones
    private lateinit var backButton: Button  // Botón para regresar a la actividad anterior

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        // Inicializar Firebase Auth y Database
        mAuth = FirebaseAuth.getInstance()  // Obtener la instancia de FirebaseAuth
        mDatabase = FirebaseDatabase.getInstance().reference  // Obtener la referencia raíz de FirebaseDatabase

        // Vincular vistas del layout
        notificationsListView = findViewById(R.id.notificationsListView)  // Asignar el ListView de las notificaciones
        backButton = findViewById(R.id.btnBack)  // Asignar el botón de retroceso

        // Cargar las notificaciones
        loadNotifications()

        // Configurar el botón para regresar
        backButton.setOnClickListener {
            finish() // Cierra la actividad actual y vuelve a la anterior
        }
    }

    private fun loadNotifications() {
        val userId = mAuth.currentUser?.uid  // Obtener el ID del usuario actual
        if (userId == null) {  // Verificar si el usuario no está autenticado
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        // Referencia a las notificaciones del usuario en Firebase
        val notificationsRef = mDatabase.child("Notifications").child(userId)

        // Escuchar los cambios en las notificaciones del usuario
        notificationsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val notifications = mutableListOf<String>()  // Lista para almacenar las notificaciones

                // Recorrer cada notificación en Firebase
                for (notificationSnapshot in dataSnapshot.children) {
                    val notification = notificationSnapshot.getValue(Map::class.java)
                    notification?.let {
                        val message = it["message"] as String
                        notifications.add(message)  // Agregar la notificación a la lista
                    }
                }

                // Configurar el adaptador para mostrar las notificaciones en el ListView
                val adapter = ArrayAdapter(
                    this@NotificationsActivity,  // Contexto de la actividad
                    android.R.layout.simple_list_item_1,  // Layout simple para cada elemento de la lista
                    notifications  // Lista de notificaciones a mostrar
                )
                notificationsListView.adapter = adapter  // Asignar el adaptador al ListView
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    this@NotificationsActivity,
                    "Error al cargar las notificaciones",  // Mostrar mensaje de error si ocurre un fallo al leer desde Firebase
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}

