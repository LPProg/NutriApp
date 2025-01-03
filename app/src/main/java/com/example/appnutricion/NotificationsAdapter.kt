package com.example.appnutricion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotificationsAdapter(private val notifications: List<Notification>) :
    RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {

    // ViewHolder para contener las vistas de cada notificación
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val typeTextView: TextView = itemView.findViewById(R.id.notificationType)  // TextView para mostrar el tipo de notificación
        val statusTextView: TextView = itemView.findViewById(R.id.notificationStatus)  // TextView para mostrar el estado de la notificación
        val timestampTextView: TextView = itemView.findViewById(R.id.notificationTimestamp)  // TextView para mostrar la fecha y hora de la notificación
    }

    // Método para crear un nuevo ViewHolder y asignar el layout correspondiente
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Inflar el layout 'item_notification' para cada elemento de la lista
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return ViewHolder(view)  // Devolver un nuevo ViewHolder con la vista inflada
    }

    // Método para vincular los datos de la notificación al ViewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = notifications[position]  // Obtener la notificación en la posición actual

        // Establecer los valores de la notificación en las vistas correspondientes
        holder.typeTextView.text = notification.type  // Asignar el tipo de la notificación
        holder.statusTextView.text = notification.status  // Asignar el estado de la notificación

        // Formatear la fecha y hora de la notificación y asignarla al TextView
        holder.timestampTextView.text = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            .format(Date(notification.timestamp ?: 0))  // Formato de la fecha: dd/MM/yyyy HH:mm:ss
    }

    // Método para obtener el número total de elementos en la lista de notificaciones
    override fun getItemCount(): Int {
        return notifications.size  // Devolver el tamaño de la lista de notificaciones
    }
}
