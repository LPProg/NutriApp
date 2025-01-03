package com.example.appnutricion.models

data class Request(
    var trainerUid: String = "",      // UID único del entrenador
    var requestId: String = "",       // ID único de la solicitud
    var username: String = "",        // Nombre de usuario del solicitante
    var trainerId: String = "",       // Nombre de usuario (username) del entrenador
    var status: String = "pending",   // Estado de la solicitud: pending, accepted, rejected
    var timestamp: Long = System.currentTimeMillis() // Marca de tiempo en milisegundos
)

