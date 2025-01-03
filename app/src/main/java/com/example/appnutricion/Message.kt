package com.example.appnutricion.models

data class Message(
    val sender: String = "",  // El remitente del mensaje (usuario que envía el mensaje)
    val receiver: String = "", // El receptor del mensaje (usuario que recibe el mensaje)
    val text: String = "",     // El contenido del mensaje
    val timestamp: Long = 0    // La hora en que el mensaje fue enviado (en milisegundos desde la época Unix)
)
