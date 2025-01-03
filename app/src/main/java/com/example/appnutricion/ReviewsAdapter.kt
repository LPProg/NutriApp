package com.example.appnutricion.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appnutricion.R
import com.example.appnutricion.models.Review

class ReviewsAdapter(private val reviews: List<Review>) :
    RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder>() {

    // ViewHolder que mantiene las vistas de un ítem de reseña
    class ReviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView = view.findViewById(R.id.tvUserName)  // Vista para mostrar el nombre del usuario
        val ratingBar: RatingBar = view.findViewById(R.id.ratingBarItem)  // RatingBar para mostrar la calificación
        val comment: TextView = view.findViewById(R.id.tvComment)  // Vista para mostrar el comentario de la reseña
    }

    // Se llama cuando se necesita crear una nueva vista de item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        // Infla el layout del item de la reseña
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_review, parent, false)  // Carga el layout item_review
        return ReviewViewHolder(view)  // Retorna el ViewHolder con la vista inflada
    }

    // Se llama para enlazar los datos de una reseña con las vistas del item
    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]  // Obtiene la reseña en la posición actual
        holder.userName.text = review.userName  // Establece el nombre de usuario en el TextView
        holder.ratingBar.rating = review.rating  // Establece la calificación en el RatingBar
        holder.comment.text = review.comment  // Establece el comentario en el TextView

        // Deshabilita la interacción con el RatingBar, ya que es solo de lectura en este caso
        holder.ratingBar.isEnabled = false  // Impide que el usuario modifique la calificación
    }

    // Retorna el número total de ítems en la lista de reseñas
    override fun getItemCount(): Int = reviews.size  // Retorna la cantidad de reseñas en la lista
}
