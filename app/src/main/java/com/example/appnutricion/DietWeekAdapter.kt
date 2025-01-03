package com.example.appnutricion.Diet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appnutricion.R

// Adaptador para mostrar una semana de dieta (DietWeek) en un RecyclerView
class DietWeekAdapter(private val dietWeek: DietWeek) :
    RecyclerView.Adapter<DietWeekAdapter.DietWeekViewHolder>() {

    // Lista de días de la semana para mostrar en el RecyclerView
    private val daysOfWeek = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")

    // Este método crea una nueva vista para cada ítem en el RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DietWeekViewHolder {
        // Infla el layout para un ítem de la dieta (un día de la semana)
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_day_diet, parent, false)
        // Devuelve un ViewHolder que contiene la referencia a los elementos UI
        return DietWeekViewHolder(itemView)
    }

    // Este método asocia los datos con las vistas del ViewHolder para cada ítem
    override fun onBindViewHolder(holder: DietWeekViewHolder, position: Int) {
        // Obtiene el nombre del día de la semana según la posición
        val dayName = daysOfWeek[position]

        // Obtiene la dieta correspondiente al día de la semana (monday, tuesday, etc.)
        val dietDay = when (position) {
            0 -> dietWeek.monday
            1 -> dietWeek.tuesday
            2 -> dietWeek.wednesday
            3 -> dietWeek.thursday
            4 -> dietWeek.friday
            5 -> dietWeek.saturday
            6 -> dietWeek.sunday
            else -> dietWeek.monday // En caso de un índice no válido, se asigna el lunes
        }

        // Asocia el nombre del día a la vista correspondiente
        holder.dayOfWeekTextView.text = dayName

        // Asocia las comidas del día a la vista correspondiente.
        // Si la comida es nula o vacía, se muestra "No disponible"
        holder.mealsTextView.text = """
            Breakfast: ${dietDay.breakfast ?: "No disponible"}
            Lunch: ${dietDay.lunch ?: "No disponible"}
            Snack: ${dietDay.snack ?: "No disponible"}
            Dinner: ${dietDay.dinner ?: "No disponible"}
        """.trimIndent() // trimIndent() elimina la sangría extra en la cadena de texto
    }

    // Este método devuelve el número total de ítems en el RecyclerView (7 días en una semana)
    override fun getItemCount() = 7 // Seven days in a week

    // ViewHolder que mantiene las vistas de un día de la semana
    class DietWeekViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Referencias a las vistas de texto en el layout
        val dayOfWeekTextView: TextView = itemView.findViewById(R.id.tvDayName) // Nombre del día
        val mealsTextView: TextView = itemView.findViewById(R.id.tvMeals) // Resumen de las comidas del día
    }
}

