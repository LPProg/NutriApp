package com.example.appnutricion.User

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.appnutricion.ChooseTrainerActivity
import com.example.appnutricion.Diet.DietUserActivity
import com.example.appnutricion.Messages.MessagesActivity
import com.example.appnutricion.R
import com.example.appnutricion.ReviewsActivity
import com.example.appnutricion.UpdateWeightActivity
import com.example.appnutricion.NotificationsActivity // Importar la nueva actividad
import com.example.appnutricion.ProfileActivity

class UserBaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_base)

        // Inicialización de los botones que permiten acceder a diferentes funcionalidades
        val btnViewDiets: Button = findViewById(R.id.btnViewDiets) // Botón para ver las dietas
        val btnUpdateWeight: Button = findViewById(R.id.btnUpdateWeight) // Botón para actualizar el peso
        val btnMessaging: Button = findViewById(R.id.btnMessaging) // Botón para acceder a los mensajes
        val btnReviews: Button = findViewById(R.id.btnReviews) // Botón para ver las valoraciones
        val btnMyProfile: Button = findViewById(R.id.btnMyProfile) // Botón para acceder al perfil
        val btnChooseTrainer = findViewById<Button>(R.id.btnChooseTrainer) // Botón para elegir un entrenador
        val btnNotifications = findViewById<Button>(R.id.btnNotifications) // Nuevo botón para acceder a las notificaciones

        // Listener para el botón de ver dietas
        btnViewDiets.setOnClickListener {
            // Navega a la actividad DietUserActivity cuando el botón es presionado
            startActivity(Intent(this, DietUserActivity::class.java))
        }

        // Listener para el botón de actualizar peso
        btnUpdateWeight.setOnClickListener {
            // Navega a la actividad UpdateWeightActivity cuando el botón es presionado
            startActivity(Intent(this, UpdateWeightActivity::class.java))
        }

        // Listener para el botón de mensajes
        btnMessaging.setOnClickListener {
            // Navega a la actividad MessagesActivity cuando el botón es presionado
            startActivity(Intent(this, MessagesActivity::class.java))
        }

        // Listener para el botón de valoraciones
        btnReviews.setOnClickListener {
            // Navega a la actividad ReviewsActivity cuando el botón es presionado
            startActivity(Intent(this, ReviewsActivity::class.java))
        }

        // Listener para el botón de perfil
        btnMyProfile.setOnClickListener {
            // Navega a la actividad ProfileActivity cuando el botón es presionado
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // Listener para el botón de elegir entrenador
        btnChooseTrainer.setOnClickListener {
            // Navega a la actividad ChooseTrainerActivity cuando el botón es presionado
            val intent = Intent(this, ChooseTrainerActivity::class.java)
            startActivity(intent)
        }

        // Listener para el botón de notificaciones
        btnNotifications.setOnClickListener {
            // Navega a la actividad NotificationsActivity cuando el botón es presionado
            startActivity(Intent(this, NotificationsActivity::class.java))
        }
    }
}
