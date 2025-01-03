package com.example.appnutricion

data class Profile(
    val name: String? = null,  // Nombre del usuario (opcional)
    val lastName: String? = null,  // Apellido del usuario (opcional)
    val username: String? = null,  // Nombre de usuario único (opcional)
    val email: String? = null,  // Dirección de correo electrónico del usuario (opcional)
    val age: Int? = 0,  // Edad del usuario (opcional)
    val weight: Double? = 0.0,  // Peso del usuario (en kilogramos, opcional)
    val height: Double? = 0.0,  // Altura del usuario (en metros, opcional)
    val bio: String? = null,  // Biografía o descripción del usuario (opcional)
    val role: String? //Añadimos el rol del usuario o entrenador
)


