package com.example.appnutricion.Messages

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appnutricion.R
import com.example.appnutricion.adapters.MessageAdapter
import com.example.appnutricion.models.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MessagesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView  // RecyclerView para mostrar los mensajes
    private lateinit var messageAdapter: MessageAdapter  // Adaptador que conecta los datos de los mensajes con el RecyclerView
    private lateinit var messageInput: EditText  // Campo de entrada de texto para el mensaje
    private lateinit var sendButton: Button  // Botón para enviar el mensaje
    private lateinit var messageList: MutableList<Message>  // Lista mutable de los mensajes a mostrar

    // Instancia de FirebaseAuth y FirebaseDatabase para autenticación y acceder a los datos
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance("https://nutriapp-9f068-default-rtdb.europe-west1.firebasedatabase.app").getReference("Messages")

    private lateinit var userUsername: String  // El nombre de usuario del usuario
    private lateinit var trainerUsername: String  // El nombre de usuario del entrenador

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages)

        // Inicializar las vistas
        recyclerView = findViewById(R.id.recyclerViewMessages)
        messageInput = findViewById(R.id.etMessage)
        sendButton = findViewById(R.id.btnSendMessage)

        // Configurar el RecyclerView con un LayoutManager para visualizar los mensajes
        recyclerView.layoutManager = LinearLayoutManager(this)
        messageList = mutableListOf()  // Inicializar la lista de mensajes
        messageAdapter = MessageAdapter(messageList)  // Crear el adaptador con la lista de mensajes
        recyclerView.adapter = messageAdapter  // Asignar el adaptador al RecyclerView

        // Obtener los nombres de usuario del Intent (suponiendo que se pasaron como extras)
        userUsername = intent.getStringExtra("userUsername") ?: ""
        trainerUsername = intent.getStringExtra("trainerUsername") ?: ""

        // Verificar si los usernames fueron obtenidos correctamente
        if (userUsername.isEmpty() || trainerUsername.isEmpty()) {
            Toast.makeText(this, "No se ha asignado el usuario o el entrenador", Toast.LENGTH_SHORT).show()
            return  // Salir si falta el username
        }

        // Cargar los mensajes desde Firebase
        loadMessages()

        // Configurar el botón de enviar mensaje
        sendButton.setOnClickListener {
            val messageText = messageInput.text.toString().trim()
            if (messageText.isNotEmpty()) {
                sendMessage(messageText)  // Llamar a la función para enviar el mensaje
            }
        }

        // Configurar el envío de mensaje con la tecla Enter en el teclado
        messageInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                val messageText = messageInput.text.toString().trim()
                if (messageText.isNotEmpty()) {
                    sendMessage(messageText)  // Enviar el mensaje
                }
                true  // Indicar que se ha procesado la acción
            } else {
                false
            }
        }
    }

    // Función para cargar los mensajes entre el usuario y el entrenador
    private fun loadMessages() {
        // Escuchar los mensajes entre el usuario y el entrenador en la base de datos
        database.child(userUsername).child(trainerUsername)
            .orderByChild("timestamp")  // Ordenar los mensajes por timestamp
            .addChildEventListener(object : ChildEventListener {
                // Cuando se agrega un nuevo mensaje
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val message = snapshot.getValue(Message::class.java)  // Obtener el mensaje desde el snapshot
                    if (message != null) {
                        messageList.add(message)  // Añadir el mensaje a la lista
                        messageAdapter.notifyItemInserted(messageList.size - 1)  // Notificar al adaptador para actualizar el RecyclerView
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    // Función para enviar un mensaje
    private fun sendMessage(text: String) {
        if (userUsername.isEmpty() || trainerUsername.isEmpty()) {
            Toast.makeText(this, "Faltan los nombres de usuario o entrenador", Toast.LENGTH_SHORT).show()
            return  // Salir si no se han asignado los usernames
        }

        // Crear el objeto Message con los detalles del mensaje
        val message = Message(
            sender = userUsername,  // Usuario que envía el mensaje
            receiver = trainerUsername,  // Suponiendo que el receptor es el entrenador
            text = text,  // Texto del mensaje
            timestamp = System.currentTimeMillis()  // Timestamp para el mensaje
        )

        // Generar una clave única para el mensaje y guardar en Firebase
        val messageKey = database.child(userUsername).child(trainerUsername).push().key
        if (messageKey != null) {
            // Guardar el mensaje en la base de datos
            database.child(userUsername).child(trainerUsername).child(messageKey).setValue(message)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        messageInput.text.clear()  // Limpiar el campo de entrada después de enviar el mensaje
                    } else {
                        Toast.makeText(this, "Error al enviar mensaje", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
