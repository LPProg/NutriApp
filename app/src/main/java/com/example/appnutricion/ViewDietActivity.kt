package com.example.appnutricion.Diet

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appnutricion.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.example.appnutricion.models.User


class ViewDietActivity : AppCompatActivity() {

    private lateinit var spinnerUsers: Spinner // Spinner para seleccionar el usuario
    private lateinit var recyclerView: RecyclerView // RecyclerView para mostrar la dieta de la semana
    private lateinit var adapter: DietWeekAdapter // Adaptador para el RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_diet)

        // Inicialización de la vista del Spinner y RecyclerView
        spinnerUsers = findViewById(R.id.spinnerUsers)
        recyclerView = findViewById(R.id.recyclerViewDiet)
        recyclerView.layoutManager = LinearLayoutManager(this) // Configura el RecyclerView con un LinearLayoutManager

        // Cargar los usuarios disponibles en el Spinner
        loadUsers()

        // Listener para el Spinner cuando se selecciona un usuario
        spinnerUsers.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Obtener el nombre de usuario seleccionado
                val selectedUsername = parent?.getItemAtPosition(position) as String
                // Cargar la dieta correspondiente al usuario seleccionado
                loadDietForUser(selectedUsername)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    // Método para cargar la lista de usuarios desde la base de datos
    private fun loadUsers() {
        val database = FirebaseDatabase.getInstance().reference
        // Obtener la lista de usuarios de la base de datos
        database.child("Usuario").get().addOnSuccessListener { snapshot ->
            // Crear una lista de nombres de usuario a partir de las claves de los usuarios
            val usersList = snapshot.children.mapNotNull { it.key }
            // Configurar el adaptador del Spinner con los usuarios obtenidos
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, usersList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerUsers.adapter = adapter // Asignar el adaptador al Spinner
        }
    }

    // Método para cargar la dieta de un usuario seleccionado
    private fun loadDietForUser(username: String) {
        val database = FirebaseDatabase.getInstance().reference
        // Obtener la dieta de la semana del usuario desde la base de datos
        database.child("Usuario").child(username).child("Diet").get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                // Si existe la dieta, se deserializa a un objeto DietWeek
                val dietWeek = snapshot.getValue(DietWeek::class.java) ?: DietWeek()
                // Crear el adaptador para el RecyclerView con los datos de la dieta
                adapter = DietWeekAdapter(dietWeek)
                recyclerView.adapter = adapter // Asignar el adaptador al RecyclerView
            } else {
                // Si no se encuentra la dieta, mostrar un mensaje de error
                Toast.makeText(this, "No se encontró la dieta del usuario", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            // Si ocurre un error al cargar la dieta, mostrar un mensaje de error
            Toast.makeText(this, "Error al cargar la dieta", Toast.LENGTH_SHORT).show()
        }
    }
}
