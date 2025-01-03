package com.example.appnutricion

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appnutricion.adapters.TrainerAdapter
import com.example.appnutricion.models.Trainer
import com.google.firebase.database.*

class TrainerListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView // RecyclerView para mostrar la lista de entrenadores
    private lateinit var trainerAdapter: TrainerAdapter // Adaptador que maneja la lista de entrenadores
    private lateinit var trainerList: MutableList<Trainer> // Lista mutable que contiene los entrenadores

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trainer_list) // Se establece el layout de la actividad

        // Inicializa el RecyclerView y configura su LayoutManager
        recyclerView = findViewById(R.id.recyclerViewTrainers)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inicializa la lista de entrenadores y el adaptador
        trainerList = mutableListOf()
        trainerAdapter = TrainerAdapter(trainerList) { trainer ->
            // Acción al seleccionar un entrenador
            Toast.makeText(this, "Seleccionaste a ${trainer.name}", Toast.LENGTH_SHORT).show()

            // Navegar a la actividad de perfil del entrenador, pasando el username
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("trainerUsername", trainer.username) // Pasar el username del entrenador
            startActivity(intent) // Iniciar la actividad de perfil
        }

        // Asocia el adaptador con el RecyclerView
        recyclerView.adapter = trainerAdapter

        // Llamada a Firebase para obtener la lista de entrenadores
        fetchTrainersFromFirebase()
    }

    // Función para cargar la lista de entrenadores desde Firebase
    private fun fetchTrainersFromFirebase() {
        val database = FirebaseDatabase.getInstance().reference // Obtiene una referencia a la base de datos
        database.child("Trainers").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trainerList.clear() // Limpiar la lista de entrenadores antes de agregar los nuevos
                for (trainerSnapshot in snapshot.children) {
                    // Obtener cada entrenador del snapshot
                    val trainer = trainerSnapshot.getValue(Trainer::class.java)
                    if (trainer != null) {
                        trainerList.add(trainer) // Agregar el entrenador a la lista
                    }
                }
                trainerAdapter.notifyDataSetChanged() // Notificar al adaptador que los datos han cambiado
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejo de error si la lectura desde Firebase falla
                Toast.makeText(this@TrainerListActivity, "Error al cargar los entrenadores", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
