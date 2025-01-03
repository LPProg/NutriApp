package com.example.appnutricion

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appnutricion.adapters.TrainerAdapter
import com.example.appnutricion.models.Trainer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChooseTrainerActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView // RecyclerView para mostrar la lista de entrenadores
    private lateinit var adapter: TrainerAdapter // Adaptador para gestionar la lista de entrenadores
    private lateinit var trainersList: MutableList<Trainer> // Lista mutable de entrenadores
    private lateinit var auth: FirebaseAuth // Instancia para manejar la autenticación con Firebase

    // Método que se ejecuta al crear la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_trainer)

        auth = FirebaseAuth.getInstance() // Inicializamos FirebaseAuth
        recyclerView = findViewById(R.id.recyclerTrainers) // Referencia al RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this) // Establecemos el layout manager para el RecyclerView
        trainersList = mutableListOf() // Inicializamos la lista de entrenadores

        // Inicializamos el adaptador y definimos el comportamiento al seleccionar un entrenador
        adapter = TrainerAdapter(trainersList) { selectedTrainer ->
            val userId = auth.currentUser?.uid ?: return@TrainerAdapter // Obtenemos el ID del usuario actual
            sendRequestToTrainer(selectedTrainer.trainerUid ?: "", userId) // Enviamos la solicitud al entrenador
        }

        recyclerView.adapter = adapter // Asignamos el adaptador al RecyclerView

        fetchTrainers() // Llamamos a la función para cargar los entrenadores desde Firebase
    }

    // Función para cargar los entrenadores desde Firebase
    private fun fetchTrainers() {
        val trainersRef = FirebaseDatabase.getInstance().getReference("Trainers") // Referencia a la base de datos de entrenadores
        trainersRef.orderByChild("role").equalTo("Entrenador").addValueEventListener(object : ValueEventListener {
            // Callback cuando los datos se han cargado
            override fun onDataChange(snapshot: DataSnapshot) {
                trainersList.clear() // Limpiamos la lista de entrenadores antes de cargar los nuevos
                if (snapshot.exists()) {
                    // Si existen datos de entrenadores, los agregamos a la lista
                    for (trainerSnapshot in snapshot.children) {
                        val trainer = trainerSnapshot.getValue(Trainer::class.java)
                        trainer?.trainerUid = trainerSnapshot.key // Asignamos el UID del entrenador
                        trainer?.let { trainersList.add(it) } // Agregamos el entrenador a la lista
                    }
                    adapter.notifyDataSetChanged() // Notificamos al adaptador que los datos han cambiado
                } else {
                    // Si no se encontraron entrenadores, mostramos un mensaje
                    Toast.makeText(this@ChooseTrainerActivity, "No se encontraron entrenadores", Toast.LENGTH_SHORT).show()
                }
            }

            // Callback en caso de error al cargar los datos
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ChooseTrainerActivity, "Error al cargar entrenadores: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Función para enviar una solicitud al entrenador
    fun sendRequestToTrainer(trainerUid: String, userId: String) {
        val usersRef = FirebaseDatabase.getInstance().getReference("Users") // Referencia a la base de datos de usuarios
        // Obtenemos el nombre de usuario del usuario actual
        usersRef.child(userId).child("username").get().addOnSuccessListener { usernameSnapshot ->
            val username = usernameSnapshot.value as String? ?: "Unknown" // Si no se encuentra el nombre, lo establecemos como "Unknown"

            val requestsRef = FirebaseDatabase.getInstance().getReference("Requests/$trainerUid") // Referencia a las solicitudes de este entrenador

            // Verificamos si ya existe una solicitud pendiente o aceptada
            requestsRef.orderByChild("trainerId").equalTo(trainerUid).limitToFirst(1).get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        // Si ya existe una solicitud, la actualizamos
                        val existingRequest = snapshot.children.first() // Obtenemos la primera solicitud existente
                        val requestId = existingRequest.key // Obtenemos el ID de la solicitud
                        val updateMap = mapOf(
                            "status" to "accepted", // Actualizamos el estado de la solicitud a "aceptada"
                            "timestamp" to System.currentTimeMillis() // Establecemos la hora actual como timestamp
                        )

                        if (requestId != null) {
                            // Actualizamos la solicitud en la base de datos
                            requestsRef.child(requestId).updateChildren(updateMap)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this@ChooseTrainerActivity,
                                        "Solicitud actualizada correctamente",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                .addOnFailureListener { exception ->
                                    Toast.makeText(
                                        this@ChooseTrainerActivity,
                                        "Error al actualizar solicitud: ${exception.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    } else {
                        // Si no existe una solicitud, creamos una nueva
                        val requestId = requestsRef.push().key ?: return@addOnSuccessListener // Generamos un ID único para la nueva solicitud
                        val newRequest = mapOf(
                            "username" to username,
                            "trainerId" to trainerUid, // Asignamos el ID del entrenador
                            "status" to "pending", // Establecemos el estado como "pendiente"
                            "timestamp" to System.currentTimeMillis(), // Establecemos el timestamp
                            "requestId" to requestId // Asignamos el ID de la solicitud
                        )

                        // Guardamos la nueva solicitud en la base de datos
                        requestsRef.child(requestId).setValue(newRequest)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this@ChooseTrainerActivity,
                                    "Solicitud creada correctamente",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(
                                    this@ChooseTrainerActivity,
                                    "Error al crear solicitud: ${exception.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                }.addOnFailureListener { exception ->
                    Toast.makeText(
                        this@ChooseTrainerActivity,
                        "Error al verificar solicitud existente: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }.addOnFailureListener { exception ->
            Toast.makeText(this@ChooseTrainerActivity, "Error al obtener nombre de usuario: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }

}
