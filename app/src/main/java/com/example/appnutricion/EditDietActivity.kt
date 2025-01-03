package com.example.appnutricion.Diet

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.appnutricion.R
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth

// Actividad para editar la dieta de un usuario en un día específico
class EditDietActivity : AppCompatActivity() {

    // Variable para almacenar el ID del usuario cuya dieta se está editando
    private lateinit var selectedUserId: String  // ID del usuario para quien se crea la dieta

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_diet)

        // Obtener el ID del usuario desde el Intent (suponiendo que ya lo pasaste al abrir esta actividad)
        selectedUserId = intent.getStringExtra("selectedUserId") ?: ""

        // Si no se pasó un ID de usuario, mostrar un mensaje y finalizar la actividad
        if (selectedUserId.isEmpty()) {
            Toast.makeText(this, "No se seleccionó un usuario", Toast.LENGTH_SHORT).show()
            finish()  // Termina la actividad si no se ha seleccionado un usuario
            return
        }

        // Obtener el nombre del día desde el Intent para mostrarlo en el título
        val selectedDay = intent.getStringExtra("selectedDay") ?: "Día desconocido"
        title = "Editar Dieta: $selectedDay"  // Establecer el título de la actividad

        // Referencias a los campos de la UI para introducir las comidas
        val etBreakfast: EditText = findViewById(R.id.etBreakfast)
        val etLunch: EditText = findViewById(R.id.etLunch)
        val etSnack: EditText = findViewById(R.id.etSnack)
        val etDinner: EditText = findViewById(R.id.etDinner)
        val btnSave: Button = findViewById(R.id.btnSaveDiet)

        // Configurar el botón para guardar la dieta
        btnSave.setOnClickListener {
            // Obtener los valores de las comidas desde los EditText
            val breakfast = etBreakfast.text.toString()
            val lunch = etLunch.text.toString()
            val snack = etSnack.text.toString()
            val dinner = etDinner.text.toString()

            // Verificar si todos los campos están completos
            if (breakfast.isEmpty() || lunch.isEmpty() || snack.isEmpty() || dinner.isEmpty()) {
                // Si falta algún campo, mostrar un mensaje y no guardar
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Crear un mapa con los datos de la dieta
            val dietData = mapOf(
                "breakfast" to breakfast,
                "lunch" to lunch,
                "snack" to snack,
                "dinner" to dinner
            )

            // Guardar la dieta en la base de datos de Firebase bajo el ID del usuario y el día seleccionado
            val database = FirebaseDatabase.getInstance().getReference("Diets")
                .child(selectedUserId)  // Usar el ID del usuario para almacenar la dieta
                .child(selectedDay)  // Usar el día seleccionado como clave en la base de datos

            // Establecer el valor en la base de datos y manejar el resultado de la operación
            database.setValue(dietData).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Si la operación fue exitosa, mostrar un mensaje y cerrar la actividad
                    Toast.makeText(this, "Dieta guardada correctamente", Toast.LENGTH_SHORT).show()
                    finish()  // Volver a la actividad anterior
                } else {
                    // Si hubo un error, mostrar un mensaje con el detalle del error
                    Toast.makeText(this, "Error al guardar la dieta: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
