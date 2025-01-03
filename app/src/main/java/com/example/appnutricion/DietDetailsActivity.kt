package com.example.appnutricion.Diet

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appnutricion.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
// Actividad que muestra los detalles de la dieta de un usuario para un día específico de la semana.
class DietDetailsActivity : AppCompatActivity() {

    private lateinit var spinnerDayOfWeek: Spinner // Spinner para seleccionar el día de la semana
    private lateinit var tvBreakfast: TextView // TextView para mostrar el desayuno
    private lateinit var tvLunch: TextView // TextView para mostrar el almuerzo
    private lateinit var tvSnack: TextView // TextView para mostrar la merienda
    private lateinit var tvDinner: TextView // TextView para mostrar la cena
    private lateinit var userId: String // ID del usuario logueado

    // Método de inicialización de la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diet_details)

        // Inicializamos las vistas
        spinnerDayOfWeek = findViewById(R.id.spinnerDayOfWeek)
        tvBreakfast = findViewById(R.id.tvBreakfast)
        tvLunch = findViewById(R.id.tvLunch)
        tvSnack = findViewById(R.id.tvSnack)
        tvDinner = findViewById(R.id.tvDinner)

        // Obtenemos el ID del usuario actual desde Firebase Auth
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        // Cargamos el Spinner con los días de la semana
        val daysOfWeek = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, daysOfWeek)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDayOfWeek.adapter = adapter

        // Establecemos el listener para el Spinner para que cargue los detalles de la dieta al seleccionar un día
        spinnerDayOfWeek.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            // Se llama cuando un día es seleccionado
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedDay = parent?.getItemAtPosition(position).toString() // Obtenemos el día seleccionado
                loadDietDetails(selectedDay) // Cargamos los detalles de la dieta para el día seleccionado
            }

            // Método vacío, se usa cuando no se selecciona un día
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Aquí puedes manejar el caso cuando no hay ítem seleccionado
            }
        })
    }

    // Función para cargar los detalles de la dieta desde Firebase
    private fun loadDietDetails(selectedDay: String) {
        // Referencia a la base de datos de Firebase para obtener la dieta del día seleccionado
        val database = FirebaseDatabase.getInstance().getReference("Diets").child(userId).child(selectedDay)

        // Agregamos un ValueEventListener para obtener los datos de la dieta
        database.addValueEventListener(object : ValueEventListener {
            // Método que se ejecuta cuando los datos son obtenidos correctamente
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Si los datos existen, los deserializamos en un objeto DietDay
                    val diet = snapshot.getValue(DietDay::class.java)

                    // Asignamos los valores de las comidas al TextView correspondiente
                    tvBreakfast.text = diet?.breakfast ?: "No disponible" // Si no hay desayuno, mostramos "No disponible"
                    tvLunch.text = diet?.lunch ?: "No disponible" // Lo mismo para el almuerzo
                    tvSnack.text = diet?.snack ?: "No disponible" // Lo mismo para la merienda
                    tvDinner.text = diet?.dinner ?: "No disponible" // Lo mismo para la cena
                } else {
                    // Si no se encontraron datos para este día, mostramos un mensaje de error
                    Toast.makeText(this@DietDetailsActivity, "No se encontraron datos para este día", Toast.LENGTH_SHORT).show()
                }
            }

            // Método que se ejecuta si hay un error al acceder a la base de datos
            override fun onCancelled(error: DatabaseError) {
                // Mostramos un mensaje de error
                Toast.makeText(this@DietDetailsActivity, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
