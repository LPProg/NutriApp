package com.example.appnutricion.models

data class Trainer(
    var trainerUid: String? = null,  // ID único del entrenador, se asigna cuando se guarda en la base de datos
    val name: String? = null,        // Nombre del entrenador
    val lastName: String? = null,    // Apellido del entrenador
    val email: String? = null,       // Correo electrónico del entrenador
    val username: String? = null,    // Nombre de usuario del entrenador
    val role: String? = null,        // Rol del entrenador (normalmente "Entrenador")
    var isAccepted: Boolean = false  // Indicador si el entrenador ha sido aceptado para trabajar en la plataforma
)
