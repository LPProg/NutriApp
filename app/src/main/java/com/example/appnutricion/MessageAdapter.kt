package com.example.appnutricion.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appnutricion.R
import com.example.appnutricion.models.Message

class MessageAdapter(private val messageList: List<Message>) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    // Este método se encarga de crear una nueva vista para cada mensaje en la lista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        // Inflamos el layout item_message.xml que será usado para mostrar un mensaje
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)  // Retornamos un nuevo ViewHolder con la vista inflada
    }

    // Este método se llama para asociar los datos del mensaje con la vista en cada posición
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        // Obtenemos el mensaje de la lista según la posición
        val message = messageList[position]

        // Asignamos los valores de sender y text del mensaje a los TextViews del ViewHolder
        holder.tvSender.text = message.sender
        holder.tvMessage.text = message.text
    }

    // Este método retorna la cantidad total de elementos en la lista de mensajes
    override fun getItemCount(): Int {
        return messageList.size
    }

    // ViewHolder que contiene las vistas del mensaje (sender y text)
    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSender: TextView = itemView.findViewById(R.id.tvSender)  // TextView para mostrar el remitente
        val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)  // TextView para mostrar el contenido del mensaje
    }
}
