package com.example.appnutricion.Diet

import android.content.Intent
import android.os.Bundle
import android.widget.GridView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appnutricion.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class DietUserActivity : AppCompatActivity() {

    private lateinit var gridView: GridView  // Vista GridView para mostrar los días de la semana
    private lateinit var daysOfWeek: Array<String>  // Array que contiene los días de la semana
    private lateinit var dietSummaries: MutableMap<String, String>  // Mapa para almacenar los resúmenes de cada día de la dieta

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diet_user)

        // Inicializa el GridView y el array de días de la semana
        gridView = findViewById(R.id.gridViewDietDays)
        daysOfWeek = arrayOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
        dietSummaries = mutableMapOf()  // Inicializa el mapa de resúmenes de dieta

        // Cargar los resúmenes de la dieta para cada día de la semana
        loadDietSummaries()

        // Configura el adaptador para el GridView que mostrará los días de la semana y sus resúmenes
        val adapter = DaysOfWeekAdapter(this, daysOfWeek, dietSummaries)
        gridView.adapter = adapter

        // Configura el listener para cuando se hace clic en un día de la semana
        gridView.setOnItemClickListener { _, _, position, _ ->
            val day = daysOfWeek[position]  // Obtener el día seleccionado
            showDietForDay(day)  // Mostrar los detalles de la dieta para el día seleccionado
        }
    }

    // Función para cargar los resúmenes de las dietas de los días de la semana
    private fun loadDietSummaries() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return  // Obtener el ID del usuario autenticado
        val database = FirebaseDatabase.getInstance().getReference("Diets").child(userId)  // Referencia a la base de datos de Firebase

        // Recorrer los días de la semana y cargar los resúmenes de cada día
        for (day in daysOfWeek) {
            database.child(day).get().addOnSuccessListener { snapshot ->
                val dietDay = snapshot.getValue(DietDay::class.java)  // Obtener la dieta para el día desde Firebase

                if (dietDay != null) {
                    // Crear un resumen con las comidas de ese día
                    val summary = "Desayuno: ${dietDay.breakfast}\n" +
                            "Almuerzo: ${dietDay.lunch}\n" +
                            "Merienda: ${dietDay.snack}\n" +
                            "Cena: ${dietDay.dinner}"

                    // Almacenar el resumen en el mapa dietSummaries
                    dietSummaries[day] = summary

                    // Notificar al adaptador para que actualice el GridView con los nuevos datos
                    (gridView.adapter as DaysOfWeekAdapter).notifyDataSetChanged()
                }
            }
        }
    }

    // Función para mostrar los detalles de la dieta para el día seleccionado
    private fun showDietForDay(day: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return  // Obtener el ID del usuario autenticado
        val database = FirebaseDatabase.getInstance().getReference("Diets").child(userId)  // Referencia a la base de datos

        // Obtener la dieta completa para el día seleccionado
        database.child(day).get().addOnSuccessListener { snapshot ->
            val dietDay = snapshot.getValue(DietDay::class.java)  // Obtener los datos de la dieta para el día

            if (dietDay != null) {
                // Si la dieta está disponible, enviar los detalles a la siguiente actividad
                val intent = Intent(this, DayDietDetailsActivity::class.java).apply {
                    putExtra("day", day)  // Pasar el día seleccionado
                    putExtra("diet", dietDay)  // Pasar el objeto DietDay con los detalles de la dieta
                }
                startActivity(intent)  // Iniciar la actividad DayDietDetailsActivity
            } else {
                // Si no hay dieta para el día, mostrar un mensaje de error
                Toast.makeText(this, "No hay dieta disponible para este día.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
