package com.example.appnutricion.Diet

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.appnutricion.R

class DayDietDetailsActivity : AppCompatActivity() {

    // Método que se ejecuta cuando se crea la actividad
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day_diet_details)

        // Recuperamos los datos pasados por el Intent
        // 'day' es una cadena que representa el día que se pasó como extra
        val day = intent.getStringExtra("day")

        // 'diet' es un objeto DietDay que se pasó como extra en el Intent
        val dietDay: DietDay? = intent.getParcelableExtra("diet")

        // Vinculamos las vistas de la actividad
        val tvDay: TextView = findViewById(R.id.tvDay) // Día
        val tvBreakfast: TextView = findViewById(R.id.tvBreakfast) // Desayuno
        val tvLunch: TextView = findViewById(R.id.tvLunch) // Almuerzo
        val tvSnack: TextView = findViewById(R.id.tvSnack) // Merienda
        val tvDinner: TextView = findViewById(R.id.tvDinner) // Cena

        // Asignamos los datos a las vistas
        // Si 'day' es nulo, mostramos "Día no especificado"
        tvDay.text = day ?: "Día no especificado"

        // Si 'dietDay' no es nulo, asignamos los valores de las comidas
        // Si alguna comida es nula, mostramos "No especificado"
        tvBreakfast.text = dietDay?.breakfast ?: "No especificado"
        tvLunch.text = dietDay?.lunch ?: "No especificado"
        tvSnack.text = dietDay?.snack ?: "No especificado"
        tvDinner.text = dietDay?.dinner ?: "No especificado"
    }
}
