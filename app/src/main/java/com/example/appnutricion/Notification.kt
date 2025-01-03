package com.example.appnutricion

data class Notification(
    val status: String? = null,  // El estado de la notificación
    val timestamp: Long? = null,  // Marca de tiempo que indica cuándo se envió o recibió la notificación
    val type: String? = null  // El tipo de notificación
)

