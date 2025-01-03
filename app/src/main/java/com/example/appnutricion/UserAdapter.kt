package com.example.appnutricion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.appnutricion.models.User

class UserAdapter(
    private var userList: MutableList<User>, // Lista mutable de usuarios que se mostrará en el RecyclerView
    private val onUserSelected: (String) -> Unit // Función de callback que maneja la selección de un usuario, utilizando el 'username' como identificador único
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private var originalList: List<User> = ArrayList(userList) // Lista original de usuarios que se usará para el filtrado

    // Método que infla el layout del item del RecyclerView y crea un ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        // Inflar el layout 'item_user' para cada item en el RecyclerView
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(itemView) // Retornar el ViewHolder con el item inflado
    }

    // Método que asocia los datos de cada usuario a las vistas en el ViewHolder
    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position] // Obtener el usuario en la posición actual
        holder.bind(user) // Llamar al método bind para asociar los datos del usuario al ViewHolder
        // Establecer un click listener en el item del RecyclerView, que llama a 'onUserSelected' con el 'username' del usuario
        holder.itemView.setOnClickListener { user.username?.let { onUserSelected(it) } }
    }

    // Método que devuelve el número de elementos en la lista de usuarios
    override fun getItemCount(): Int = userList.size

    // Método para filtrar la lista de usuarios basándose en una consulta de texto
    fun filterList(query: String) {
        userList = if (query.isEmpty()) {
            // Si la consulta está vacía, se restaura la lista original
            originalList.toMutableList()
        } else {
            // Si hay una consulta, se filtran los usuarios que coincidan con la consulta en cualquiera de sus campos
            originalList.filter {
                it.name?.contains(query, ignoreCase = true) == true ||
                        it.lastName?.contains(query, ignoreCase = true) == true ||
                        it.email?.contains(query, ignoreCase = true) == true ||
                        it.username?.contains(query, ignoreCase = true) == true
            }.toMutableList() // Retornar una lista mutable con los resultados filtrados
        }

        // Crear una instancia de UserDiffCallback para calcular las diferencias entre la lista original y la lista filtrada
        val diffCallback = UserDiffCallback(originalList, userList)
        val diffResult = DiffUtil.calculateDiff(diffCallback) // Calcular las diferencias
        diffResult.dispatchUpdatesTo(this) // Aplicar las actualizaciones al RecyclerView de manera eficiente
    }

    // ViewHolder que gestiona las vistas de un item de usuario
    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvName) // TextView para mostrar el nombre del usuario
        private val tvLastName: TextView = itemView.findViewById(R.id.tvLastName) // TextView para mostrar el apellido del usuario
        private val tvEmail: TextView = itemView.findViewById(R.id.tvEmail) // TextView para mostrar el correo electrónico del usuario

        // Método que vincula los datos del usuario a las vistas
        fun bind(user: User) {
            // Asigna los valores de los campos del usuario a los TextViews correspondientes
            tvName.text = user.name ?: "Nombre desconocido" // Si el nombre es nulo, mostrar "Nombre desconocido"
            tvLastName.text = user.lastName ?: "Apellido desconocido" // Si el apellido es nulo, mostrar "Apellido desconocido"
            tvEmail.text = user.email ?: "Correo desconocido" // Si el correo es nulo, mostrar "Correo desconocido"
        }
    }

    // Callback de DiffUtil que calcula las diferencias entre dos listas de usuarios
    class UserDiffCallback(
        private val oldList: List<User>, // Lista original de usuarios
        private val newList: List<User>  // Lista filtrada de usuarios
    ) : DiffUtil.Callback() {

        // Devuelve el tamaño de la lista original
        override fun getOldListSize(): Int = oldList.size

        // Devuelve el tamaño de la lista filtrada
        override fun getNewListSize(): Int = newList.size

        // Compara si dos elementos (usuarios) son los mismos, lo cual se basa en el 'username'
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].username == newList[newItemPosition].username
        }

        // Compara si los contenidos de dos elementos (usuarios) son los mismos
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}
