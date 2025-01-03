package com.example.appnutricion

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import androidx.core.content.ContextCompat

class UpdateWeightActivity : AppCompatActivity() {

    private lateinit var lineChart: LineChart // Gráfico de línea para mostrar el historial de peso
    private lateinit var etWeight: EditText // Campo de texto para ingresar el peso
    private lateinit var btnUpdate: Button // Botón para actualizar el peso
    private lateinit var btnDeleteLastWeight: Button  // Nuevo botón para borrar el último peso
    private val userId: String by lazy { FirebaseAuth.getInstance().currentUser?.uid ?: "" } // UID del usuario autenticado
    private val database by lazy {
        FirebaseDatabase.getInstance("https://nutriapp-9f068-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference("Users").child(userId).child("WeightHistory") // Referencia a la base de datos donde se guarda el historial de peso
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_weight) // Establece el layout de la actividad

        // Inicialización de vistas
        lineChart = findViewById(R.id.lineChart)
        etWeight = findViewById(R.id.etWeight)
        btnUpdate = findViewById(R.id.btnUpdate)
        btnDeleteLastWeight = findViewById(R.id.btnDeleteLastWeight)  // Inicialización del botón de eliminar peso

        // Acción al presionar el botón de actualizar peso
        btnUpdate.setOnClickListener {
            val weightInput = etWeight.text.toString()
            if (weightInput.isEmpty()) {
                Toast.makeText(this, "Por favor, introduce tu peso", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val weight = weightInput.toFloatOrNull() // Intenta convertir el input a un valor float
            if (weight == null || weight <= 0) {
                Toast.makeText(this, "Introduce un peso válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            saveWeightToDatabase(weight) // Guardar el peso en la base de datos
        }

        // Acción al presionar el botón de eliminar último peso
        btnDeleteLastWeight.setOnClickListener {
            deleteLastWeight() // Eliminar el último peso registrado
        }

        // Cargar el historial de peso desde la base de datos
        loadWeightHistory()
    }

    // Función para guardar el peso en la base de datos
    private fun saveWeightToDatabase(weight: Float) {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) // Fecha actual
        val weightData = mapOf("date" to currentDate, "weight" to weight) // Datos a guardar en la base de datos

        // Guardar el peso en Firebase bajo el historial del usuario
        database.push().setValue(weightData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Peso actualizado correctamente", Toast.LENGTH_SHORT).show()
                etWeight.text.clear() // Limpiar el campo de texto
                loadWeightHistory() // Recargar el gráfico con los nuevos datos
            } else {
                Toast.makeText(this, "Error al actualizar el peso: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Función para cargar el historial de peso desde la base de datos
    private fun loadWeightHistory() {
        database.get().addOnSuccessListener { snapshot ->
            val weightEntries = mutableListOf<Entry>() // Lista para los puntos del gráfico
            val weightLabels = mutableListOf<String>() // Lista para las fechas

            var index = 0
            snapshot.children.forEach { child ->
                val weight = child.child("weight").getValue(Float::class.java)
                val date = child.child("date").getValue(String::class.java)

                // Si los valores de peso y fecha son válidos, agregar al gráfico
                if (weight != null && date != null) {
                    weightEntries.add(Entry(index.toFloat(), weight)) // Agregar punto al gráfico
                    weightLabels.add(date) // Agregar la fecha correspondiente
                    index++
                }
            }

            displayChart(weightEntries, weightLabels) // Mostrar el gráfico actualizado
        }
    }

    // Función para mostrar el gráfico con el historial de peso
    private fun displayChart(entries: List<Entry>, labels: List<String>) {
        val lineDataSet = LineDataSet(entries, "Historial de Peso") // Crear el conjunto de datos para el gráfico
        lineDataSet.color = ContextCompat.getColor(this, R.color.purple_500)
        val color = ContextCompat.getColor(this, R.color.black)
        lineDataSet.lineWidth = 2f
        lineDataSet.circleRadius = 4f
        lineDataSet.setCircleColor(ContextCompat.getColor(this, R.color.purple_500))

        val lineData = LineData(lineDataSet) // Crear los datos de línea para el gráfico
        lineChart.data = lineData

        // Configurar el eje X del gráfico
        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = IndexAxisValueFormatter(labels) // Formatear las etiquetas con las fechas
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true

        // Desactivar el eje derecho y la descripción del gráfico
        lineChart.axisRight.isEnabled = false
        lineChart.description.isEnabled = false
        lineChart.invalidate() // Actualizar el gráfico
    }

    // Función para eliminar el último peso registrado
    private fun deleteLastWeight() {
        // Consultamos el último peso de la base de datos
        database.get().addOnSuccessListener { snapshot ->
            if (snapshot.childrenCount > 0) {
                // Si hay datos, obtenemos el último elemento
                val lastChild = snapshot.children.last()
                // Eliminar el último peso de la base de datos
                lastChild.ref.removeValue().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Último peso eliminado", Toast.LENGTH_SHORT).show()
                        loadWeightHistory() // Recargar el gráfico y los datos
                    } else {
                        Toast.makeText(this, "Error al eliminar el peso: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "No hay pesos registrados para eliminar", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
