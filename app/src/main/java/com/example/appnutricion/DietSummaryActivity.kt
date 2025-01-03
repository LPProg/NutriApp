package com.example.appnutricion.Diet

import android.content.Intent
import android.os.Bundle
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appnutricion.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DietSummaryActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference  // Referencia a la base de datos de Firebase
    private lateinit var tableLayout: TableLayout  // Vista donde se mostrarán las dietas en forma de tabla

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diet_summary)

        // Inicializar la vista TableLayout donde se mostrarán las dietas
        tableLayout = findViewById(R.id.tableLayout)

        // Obtener el UID del usuario autenticado
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            // Si no hay usuario autenticado, mostrar un mensaje de error y cerrar la actividad
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Obtener la referencia en la base de datos de las dietas del usuario autenticado
        database = FirebaseDatabase.getInstance().getReference("Diets/$userId")

        // Llamar a la función para obtener la dieta semanal del usuario
        fetchWeeklyDiet()
    }

    // Función para recuperar la dieta semanal desde Firebase
    private fun fetchWeeklyDiet() {
        // Agregar un escuchador para obtener los datos de la dieta del usuario
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Verificar si existen datos en el snapshot
                if (snapshot.exists()) {
                    // Recorrer cada día de la semana y recuperar la información de las comidas
                    for (daySnapshot in snapshot.children) {
                        val day = daySnapshot.key ?: ""  // Obtener el nombre del día
                        val meals = daySnapshot.getValue(DietDay::class.java)  // Obtener las comidas para ese día

                        // Agregar una fila a la tabla con los datos del día y las comidas
                        addRowToTable(day, meals)
                    }
                } else {
                    // Si no hay dietas para este usuario, mostrar un mensaje de error
                    Toast.makeText(this@DietSummaryActivity, "No se encontraron dietas", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar errores si no se pudo acceder a la base de datos
                Toast.makeText(this@DietSummaryActivity, "Error al cargar las dietas", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Función para agregar una fila a la tabla con la información de un día
    private fun addRowToTable(day: String, meals: DietDay?) {
        val row = TableRow(this)  // Crear una nueva fila para la tabla
        val dayText = TextView(this)  // Vista para mostrar el nombre del día
        dayText.text = day  // Asignar el nombre del día

        val summaryText = TextView(this)  // Vista para mostrar el resumen de las comidas
        summaryText.text = buildSummaryText(meals)  // Crear el resumen a partir de las comidas

        // Agregar las vistas de día y resumen a la fila
        row.addView(dayText)
        row.addView(summaryText)

        // Configurar un clic en la fila para mostrar los detalles de la dieta
        row.setOnClickListener {
            val intent = Intent(this, DietDetailsActivity::class.java)
            intent.putExtra("selectedDay", day)  // Pasar el día seleccionado a la siguiente actividad
            startActivity(intent)  // Iniciar la actividad DietDetailsActivity
        }

        // Agregar la fila a la tabla
        tableLayout.addView(row)
    }

    // Función para construir el resumen de las comidas del día (desayuno, almuerzo, etc.)
    private fun buildSummaryText(meals: DietDay?): String {
        // Crear una lista con los nombres de las comidas disponibles
        return listOfNotNull(
            if (!meals?.breakfast.isNullOrBlank()) "Desayuno" else null,
            if (!meals?.lunch.isNullOrBlank()) "Almuerzo" else null,
            if (!meals?.snack.isNullOrBlank()) "Merienda" else null,
            if (!meals?.dinner.isNullOrBlank()) "Cena" else null
        ).joinToString(", ")  // Unir las comidas disponibles con una coma
    }
}
