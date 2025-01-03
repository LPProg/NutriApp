package com.example.appnutricion

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appnutricion.adapters.ReviewsAdapter
import com.example.appnutricion.models.Review
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ReviewsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth  // Instancia de FirebaseAuth para gestionar la autenticación del usuario
    private lateinit var reviewAdapter: ReviewsAdapter  // Adaptador para mostrar las reseñas en el RecyclerView
    private val reviews = mutableListOf<Review>()  // Lista mutable que almacena las reseñas
    private val database = FirebaseDatabase.getInstance().reference  // Referencia a la base de datos de Firebase

    private lateinit var etComment: EditText  // Campo de texto para que el usuario ingrese su comentario
    private lateinit var ratingBar: RatingBar  // Barra de calificación donde el usuario selecciona su puntaje
    private lateinit var btnSubmitReview: Button  // Botón para enviar la reseña
    private lateinit var recyclerViewReviews: RecyclerView  // RecyclerView para mostrar la lista de reseñas

    private var trainerId: String = ""  // ID del entrenador al que se están enviando las reseñas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviews)

        // Inicialización de vistas
        auth = FirebaseAuth.getInstance()  // Inicializar FirebaseAuth
        etComment = findViewById(R.id.etComment)  // Obtener EditText para el comentario
        ratingBar = findViewById(R.id.ratingBar)  // Obtener RatingBar para la calificación
        btnSubmitReview = findViewById(R.id.btnSubmitReview)  // Obtener el botón para enviar la reseña
        recyclerViewReviews = findViewById(R.id.recyclerViewReviews)  // Obtener el RecyclerView para mostrar reseñas

        // Obtener el ID del entrenador desde el intent
        trainerId = intent.getStringExtra("trainerId") ?: run {
            Toast.makeText(this, "Error: no se especificó el entrenador", Toast.LENGTH_LONG).show()
            finish()  // Finalizar la actividad si no se ha especificado el ID del entrenador
            return
        }

        // Configuración del RecyclerView
        reviewAdapter = ReviewsAdapter(reviews)  // Inicializar el adaptador de reseñas
        recyclerViewReviews.layoutManager = LinearLayoutManager(this)  // Establecer el LayoutManager para el RecyclerView
        recyclerViewReviews.adapter = reviewAdapter  // Asignar el adaptador al RecyclerView

        // Configurar el botón para enviar la reseña
        btnSubmitReview.setOnClickListener { submitReview() }

        // Cargar reseñas del entrenador
        loadReviews(trainerId)
    }

    private fun submitReview() {
        val comment = etComment.text.toString().trim()  // Obtener el comentario del usuario
        val rating = ratingBar.rating  // Obtener la calificación seleccionada por el usuario
        val userName = auth.currentUser?.displayName ?: "Usuario anónimo"  // Obtener el nombre del usuario (o "Usuario anónimo" si no está autenticado)
        val trainerName = trainerId  // Usar el trainerId como el nombre del entrenador

        if (userName == "Usuario anónimo") {  // Verificar si el usuario está autenticado
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        if (comment.isNotEmpty() && rating > 0) {  // Verificar que el comentario no esté vacío y que la calificación sea mayor que 0
            val review = Review(
                userName = userName,
                trainerName = trainerName,  // Usar el nombre del entrenador
                rating = rating,
                comment = comment,
                timestamp = System.currentTimeMillis()  // Establecer el timestamp actual
            )

            // Guardar la reseña en la base de datos
            database.child("Reviews").push().setValue(review)
                .addOnSuccessListener {
                    Toast.makeText(this, "Reseña enviada", Toast.LENGTH_SHORT).show()
                    etComment.text.clear()  // Limpiar el campo de comentario
                    ratingBar.rating = 0f  // Restablecer la barra de calificación
                    loadReviews(trainerName)  // Recargar las reseñas con el nombre del entrenador
                }
                .addOnFailureListener { e ->  // Manejar errores al guardar la reseña
                    Toast.makeText(this, "Error al enviar la reseña: ${e.message}", Toast.LENGTH_LONG).show()
                }
        } else {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()  // Avisar si falta completar algún campo
        }
    }

    private fun loadReviews(trainerName: String) {
        // Cargar reseñas de Firebase basadas en el nombre del entrenador
        database.child("Reviews").orderByChild("trainerName").equalTo(trainerName).get()
            .addOnSuccessListener { snapshot ->
                reviews.clear()  // Limpiar la lista de reseñas antes de cargar las nuevas
                snapshot.children.mapNotNullTo(reviews) { it.getValue(Review::class.java) }  // Mapear las reseñas de la base de datos
                reviewAdapter.notifyDataSetChanged()  // Notificar al adaptador para que actualice la vista
            }
            .addOnFailureListener { e ->  // Manejar errores al cargar las reseñas
                Toast.makeText(this, "Error al cargar reseñas: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
