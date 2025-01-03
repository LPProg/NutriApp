package com.example.appnutricion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.appnutricion.Diet.DietManagementActivity
import com.example.appnutricion.Messages.MessagesActivity
import com.example.appnutricion.ViewUsersActivity

class TrainerBaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trainer_base) // Asegúrate de que este layout exista

        // Inicializa los botones que se encuentran en el layout
        val btnDietManagement: Button = findViewById(R.id.btnDietManagement)
        val btnViewUsers: Button = findViewById(R.id.btnViewUsers)
        val btnMessages: Button = findViewById(R.id.btnMessages)
        val btnCalculateIMC: Button = findViewById(R.id.btnCalculateIMC)
        val btnMyProfileActivity: Button = findViewById(R.id.btnMyProfile)
        val btnUserRequests: Button = findViewById(R.id.btnUserRequests)

        // Acción para el botón "Gestión de Dietas"
        btnDietManagement.setOnClickListener {
            // Al hacer clic, se inicia la actividad de gestión de dietas
            val intent = Intent(this, DietManagementActivity::class.java)
            startActivity(intent)
        }

        // Acción para el botón "Ver Usuarios"
        btnViewUsers.setOnClickListener {
            // Al hacer clic, se inicia la actividad para ver usuarios
            val intent = Intent(this, ViewUsersActivity::class.java)
            startActivity(intent)
        }

        // Acción para el botón "Mensajes"
        btnMessages.setOnClickListener {
            // Al hacer clic, se inicia la actividad de mensajes
            val intent = Intent(this, MessagesActivity::class.java)
            startActivity(intent)
        }

        // Acción para el botón "Calcular IMC"
        btnCalculateIMC.setOnClickListener {
            // Al hacer clic, se inicia la actividad de cálculo de IMC
            val intent = Intent(this, CalculateIMCActivity::class.java)
            startActivity(intent)
        }

        // Acción para el botón "Mi Perfil"
        btnMyProfileActivity.setOnClickListener {
            // Al hacer clic, se inicia la actividad de perfil del entrenador
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // Acción para el botón "Solicitudes de Entrenador"
        btnUserRequests.setOnClickListener {
            // Al hacer clic, se inicia la actividad para gestionar solicitudes de entrenador
            val intent = Intent(this, TrainerRequestsActivity::class.java)
            startActivity(intent)
        }
    }
}

