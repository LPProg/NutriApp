package com.example.appnutricion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SearchView
import com.example.appnutricion.Diet.EditDietActivity
import com.example.appnutricion.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ViewUsersActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView // RecyclerView para mostrar la lista de usuarios
    private lateinit var adapter: UserAdapter // Adaptador para RecyclerView
    private lateinit var searchView: SearchView // Barra de búsqueda para filtrar usuarios
    private val usersList = mutableListOf<User>() // Lista original de usuarios obtenida desde Firebase
    private val filteredList = mutableListOf<User>() // Lista de usuarios filtrada según la búsqueda

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_users)

        // Inicialización del RecyclerView y su adaptador
        recyclerView = findViewById(R.id.recyclerViewUsers)
        recyclerView.layoutManager = LinearLayoutManager(this) // Configurar el RecyclerView para usar LinearLayout

        searchView = findViewById(R.id.searchView) // Inicialización de la barra de búsqueda

        // Inicialización del adaptador con la lista filtrada de usuarios
        adapter = UserAdapter(filteredList) { userId ->
            // Cuando un usuario es seleccionado, se pasa su ID a la actividad EditDietActivity
            val intent = Intent(this, EditDietActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }
        recyclerView.adapter = adapter // Asignamos el adaptador al RecyclerView

        // Llamamos a Firebase para obtener los usuarios
        fetchUsersFromFirebase()

        // Configuración del listener de la barra de búsqueda
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // No hacemos nada cuando el texto es enviado, solo devolvemos false
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Filtramos la lista de usuarios cada vez que cambia el texto de la búsqueda
                filterUsers(newText)
                return false
            }
        })
    }

    // Método para obtener los usuarios de Firebase
    private fun fetchUsersFromFirebase() {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance().reference
        // Consultamos la base de datos para obtener la lista de usuarios bajo el nodo "Usuario"
        database.child("Usuario").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Si la consulta es exitosa, mapeamos los datos a objetos User
                val usersData = task.result.children.mapNotNull { snapshot ->
                    snapshot.getValue(User::class.java)
                }
                // Limpiamos las listas y añadimos los usuarios obtenidos
                usersList.clear()
                usersList.addAll(usersData)
                filteredList.clear()
                filteredList.addAll(usersData)
                // Notificamos al adaptador para actualizar la vista
                adapter.notifyDataSetChanged()
            } else {
                // Si hay un error, mostramos un mensaje en el log y en un Toast
                Log.e("ViewUsersActivity", "Error al obtener usuarios: ${task.exception?.message}")
                Toast.makeText(this, "Error al cargar usuarios", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Método para filtrar los usuarios según la búsqueda
    private fun filterUsers(query: String?) {
        filteredList.clear() // Limpiamos la lista filtrada
        if (query.isNullOrEmpty()) {
            // Si la búsqueda está vacía, mostramos todos los usuarios
            filteredList.addAll(usersList)
        } else {
            // Si hay texto en la búsqueda, filtramos por nombre o correo electrónico
            val filterPattern = query.lowercase().trim() // Convertimos a minúsculas para una búsqueda insensible a mayúsculas
            filteredList.addAll(usersList.filter {
                (it.name?.lowercase()?.contains(filterPattern) == true) ||
                        (it.email?.lowercase()?.contains(filterPattern) == true)
            })
        }
        // Notificamos al adaptador para que actualice la vista con los resultados filtrados
        adapter.notifyDataSetChanged()
    }
}
