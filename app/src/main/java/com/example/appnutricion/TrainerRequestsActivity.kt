package com.example.appnutricion

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appnutricion.models.Request
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TrainerRequestsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView // RecyclerView para mostrar las solicitudes
    private lateinit var requestsAdapter: RequestsAdapter // Adaptador que maneja la lista de solicitudes
    private lateinit var requestsList: MutableList<Request> // Lista mutable que contiene las solicitudes
    private lateinit var auth: FirebaseAuth // FirebaseAuth para gestionar la autenticación del usuario
    private lateinit var database: DatabaseReference // Referencia a la base de datos de Firebase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trainer_requests) // Establece el layout de la actividad

        auth = FirebaseAuth.getInstance() // Inicializa la autenticación de Firebase
        database = FirebaseDatabase.getInstance().reference // Inicializa la referencia a la base de datos

        // Inicializa el RecyclerView y su LayoutManager
        recyclerView = findViewById(R.id.recyclerTrainerRequests)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inicializa la lista de solicitudes
        requestsList = mutableListOf()

        // Configura el adaptador con la lista de solicitudes y la acción al hacer clic
        requestsAdapter = RequestsAdapter(requestsList) { request, action ->
            handleRequestAction(request, action) // Llama a la función para manejar la acción de la solicitud
        }

        // Asocia el adaptador con el RecyclerView
        recyclerView.adapter = requestsAdapter

        // Carga las solicitudes de los entrenadores
        fetchTrainerRequests()
    }

    // Función para obtener las solicitudes del entrenador desde Firebase
    private fun fetchTrainerRequests() {
        val currentTrainerUid = auth.currentUser?.uid ?: return // Obtiene el UID del entrenador actual
        val requestsRef = database.child("Requests").child(currentTrainerUid) // Referencia a las solicitudes del entrenador

        // Se agrega un ValueEventListener para escuchar los cambios en las solicitudes
        requestsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    // Si no hay solicitudes, muestra un mensaje
                    findViewById<TextView>(R.id.tvNoRequests).visibility = View.VISIBLE
                    return
                }

                // Limpiar la lista de solicitudes antes de agregar las nuevas
                requestsList.clear()

                // Iterar sobre las solicitudes recibidas
                for (requestSnapshot in snapshot.children) {
                    val request = requestSnapshot.getValue(Request::class.java)
                    if (request != null && request.status == "pending") {
                        // Solo agregar solicitudes pendientes
                        request.requestId = requestSnapshot.key ?: "" // Asignar el ID de la solicitud
                        requestsList.add(request) // Agregar la solicitud a la lista
                    }
                }

                // Notificar al adaptador que los datos han cambiado
                requestsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejo de error si la lectura desde Firebase falla
                Toast.makeText(
                    this@TrainerRequestsActivity,
                    "Error al cargar solicitudes: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    // Función para manejar las acciones (aceptar/rechazar) de una solicitud
    private fun handleRequestAction(request: Request, action: String) {
        val currentTrainerUid = auth.currentUser?.uid ?: return // Obtiene el UID del entrenador actual
        val requestRef = database.child("Requests").child(currentTrainerUid).child(request.requestId)

        // Actualiza el estado de la solicitud en la base de datos
        requestRef.child("status").setValue(action).addOnSuccessListener {
            // Crear una notificación para el usuario que envió la solicitud
            createRequestNotification(request.username, action)

            // Eliminar la solicitud de la lista y actualizar la UI
            requestsList.remove(request)
            requestsAdapter.notifyDataSetChanged()

            // Mostrar un mensaje de éxito
            Toast.makeText(
                this,
                "Solicitud ${if (action == "accepted") "aceptada" else "rechazada"}",
                Toast.LENGTH_SHORT
            ).show()
        }.addOnFailureListener { exception ->
            // Manejo de error si la actualización de la solicitud falla
            Toast.makeText(
                this,
                "Error al procesar solicitud: ${exception.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Función para crear una notificación para el usuario que hizo la solicitud
    private fun createRequestNotification(userId: String, status: String) {
        val notificationsRef = database.child("Notifications").child(userId).push()
        val notificationData = mapOf(
            "type" to "trainer_request", // Tipo de notificación
            "status" to status,          // Estado de la solicitud (aceptada/rechazada)
            "timestamp" to System.currentTimeMillis() // Timestamp de la notificación
        )

        // Guardar la notificación en la base de datos
        notificationsRef.setValue(notificationData).addOnSuccessListener {
            Log.d("TrainerRequestsActivity", "Notificación creada exitosamente.")
        }.addOnFailureListener {
            Log.e("TrainerRequestsActivity", "Error al crear la notificación.")
        }
    }
}
