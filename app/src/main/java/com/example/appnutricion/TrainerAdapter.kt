package com.example.appnutricion.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appnutricion.R
import com.example.appnutricion.models.Trainer
import android.graphics.Color


class TrainerAdapter(
    private val trainers: List<Trainer>,  // Lista de entrenadores a mostrar en el RecyclerView
    private val onTrainerClick: (Trainer) -> Unit  // Función que se llama cuando se hace clic en un entrenador
) : RecyclerView.Adapter<TrainerAdapter.TrainerViewHolder>() {

    // Crea una nueva vista de holder cuando es necesario
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainerViewHolder {
        // Infla el layout de cada item (item_trainer)
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_trainer, parent, false)
        return TrainerViewHolder(view)
    }

    // Asocia los datos del entrenador con la vista del ViewHolder
    override fun onBindViewHolder(holder: TrainerViewHolder, position: Int) {
        val trainer = trainers[position]

        // Asigna el nombre de usuario del entrenador, o muestra "Sin nombre de usuario" si es null
        holder.trainerName.text = trainer.username ?: "Sin nombre de usuario"

        // Asigna el nombre del entrenador como biografía, o muestra "Sin bio disponible" si es null
        holder.trainerBio.text = trainer.name ?: "Sin bio disponible"

        // Cambia el color de fondo según si el entrenador ha sido aceptado
        if (trainer.isAccepted) {
            holder.itemView.setBackgroundColor(Color.GREEN) // Fondo verde si está aceptado
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE) // Fondo blanco si no está aceptado
        }

        // Configura el listener para el clic en el item, pasando el entrenador seleccionado
        holder.itemView.setOnClickListener {
            onTrainerClick(trainer)  // Llama la función pasada como parámetro
        }
    }

    // Devuelve la cantidad total de items (entrenadores) en la lista
    override fun getItemCount(): Int = trainers.size

    // ViewHolder que almacena las vistas de cada item en el RecyclerView
    class TrainerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val trainerName: TextView = itemView.findViewById(R.id.trainerName)  // Nombre del entrenador
        val trainerBio: TextView = itemView.findViewById(R.id.trainerBio)      // Biografía del entrenador
    }
}

