package com.example.appnutricion.Diet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appnutricion.R

class DietManagementAdapter(
    private val daysOfWeek: List<String>,  // Lista de los días de la semana para mostrar en el RecyclerView
    private val onDayClicked: (String) -> Unit  // Función de callback para manejar el clic en un día
) : RecyclerView.Adapter<DietManagementAdapter.ViewHolder>() {

    // ViewHolder: Clase que mantiene las vistas para cada ítem del RecyclerView
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayName: TextView = itemView.findViewById(R.id.tvDayName)  // Vista para mostrar el nombre del día
        val editButton: Button = itemView.findViewById(R.id.btnEditDiet)  // Botón para editar la dieta del día
    }

    // Método llamado cuando se crea una nueva vista para un ítem en el RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflamos el layout del ítem (la vista para un día)
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_day_diet, parent, false)
        return ViewHolder(view)  // Retornamos el ViewHolder que contiene las vistas infladas
    }

    // Método llamado para asociar los datos del ítem con las vistas del ViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Obtenemos el nombre del día correspondiente a la posición actual
        val dayName = daysOfWeek[position]

        // Asignamos el nombre del día a la vista correspondiente
        holder.dayName.text = dayName

        // Configuramos el evento de clic para el botón de editar dieta
        holder.editButton.setOnClickListener {
            // Llamamos al callback con el nombre del día cuando se haga clic en el botón
            onDayClicked(dayName)
        }
    }

    // Retorna el número total de ítems en el RecyclerView (en este caso, los días de la semana)
    override fun getItemCount(): Int = daysOfWeek.size
}
