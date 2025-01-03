package com.example.appnutricion.models

data class Review(
    val userName: String = "",  // Nombre del usuario que realiza la reseña
    val trainerName: String = "",  // Nombre del entrenador que recibe la reseña
    val rating: Float = 0f,  // Calificación dada por el usuario
    val comment: String = "",  // Comentario adicional de la reseña
    val timestamp: Long = System.currentTimeMillis()  // Marca de tiempo de cuando se creó la reseña
)
