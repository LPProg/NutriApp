package com.example.appnutricion.models

data class User(
    val name: String? = null,           // El nombre del usuario (opcional, puede ser null)
    val lastName: String? = null,       // El apellido del usuario (opcional, puede ser null)
    val email: String? = null,          // El correo electrónico del usuario (opcional, puede ser null)
    var username: String? = null,       // El nombre de usuario (opcional, puede ser null; se puede modificar)
    val role: String? = null,           // El rol del usuario (opcional, puede ser null)
)



