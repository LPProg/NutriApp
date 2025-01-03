package com.example.appnutricion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appnutricion.models.Request

class RequestsAdapter(
    private val requestsList: List<Request>, // Lista de solicitudes a mostrar en el RecyclerView
    private val actionListener: (Request, String) -> Unit // Función que maneja las acciones de aceptar/rechazar
) : RecyclerView.Adapter<RequestsAdapter.RequestViewHolder>() {

    // Crea una nueva vista para cada ítem en el RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        // Infla el layout del ítem
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_request, parent, false)
        return RequestViewHolder(view)  // Retorna el ViewHolder asociado
    }

    // Vincula los datos del ítem a la vista correspondiente
    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val request = requestsList[position]  // Obtiene la solicitud en la posición correspondiente
        holder.bind(request, actionListener)  // Enlaza los datos de la solicitud con el ViewHolder
    }

    // Devuelve el número total de ítems en la lista
    override fun getItemCount(): Int = requestsList.size

    // ViewHolder para cada ítem en el RecyclerView
    inner class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Definir las vistas para cada solicitud
        private val tvUsername: TextView = itemView.findViewById(R.id.tvRequestUserName)  // Vista para el nombre de usuario
        private val tvStatus: TextView = itemView.findViewById(R.id.tvRequestStatus)      // Vista para el estado de la solicitud
        private val btnAccept: Button = itemView.findViewById(R.id.btnAcceptRequest)       // Botón para aceptar la solicitud
        private val btnReject: Button = itemView.findViewById(R.id.btnRejectRequest)       // Botón para rechazar la solicitud

        // Función para vincular los datos de la solicitud con las vistas correspondientes
        fun bind(request: Request, actionListener: (Request, String) -> Unit) {
            // Asignar los valores de la solicitud a las vistas
            tvUsername.text = request.username
            tvStatus.text = request.status

            // Configurar el comportamiento de los botones
            // Botón de aceptar
            btnAccept.setOnClickListener {
                actionListener(request, "accepted")  // Llama al listener con la acción de aceptación
            }
            // Botón de rechazar
            btnReject.setOnClickListener {
                actionListener(request, "rejected")  // Llama al listener con la acción de rechazo
            }
        }
    }
}
