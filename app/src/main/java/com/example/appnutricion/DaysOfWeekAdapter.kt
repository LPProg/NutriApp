package com.example.appnutricion.Diet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.appnutricion.R

class DaysOfWeekAdapter(
    private val context: Context, // Contexto de la aplicación
    private val days: Array<String>, // Lista de días de la semana (por ejemplo: "Lunes", "Martes", etc.)
    private val dietSummaries: Map<String, String> // Mapa que asocia cada día con un resumen de dieta
) : BaseAdapter() {

    // Devuelve la cantidad de elementos en el adaptador (el número de días)
    override fun getCount(): Int {
        return days.size // Retorna el número de días en la lista
    }

    // Devuelve el ítem en la posición especificada (un día de la semana)
    override fun getItem(position: Int): Any {
        return days[position] // Retorna el día de la semana en la posición especificada
    }

    // Devuelve el ID de la fila en la posición especificada (en este caso, simplemente devuelve la posición como un Long)
    override fun getItemId(position: Int): Long {
        return position.toLong() // Devuelve el ID de la fila, que es el índice convertido a Long
    }

    // Este método es responsable de crear la vista de cada ítem en la lista
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // Inflamos el layout para cada ítem (representando un día de la semana)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.item_day, null) // Inflamos el layout 'item_day'

        // Referenciamos los elementos dentro de la vista inflada
        val tvDay: TextView = view.findViewById(R.id.tvDay) // TextView para el nombre del día
        val tvDietSummary: TextView = view.findViewById(R.id.tvDietSummary) // TextView para el resumen de la dieta

        // Obtenemos el día de la semana en la posición actual
        val day = days[position]

        // Asignamos el nombre del día al TextView
        tvDay.text = day

        // Asignamos el resumen de la dieta para el día, o un mensaje predeterminado si no hay resumen disponible
        tvDietSummary.text = dietSummaries[day] ?: "Sin resumen disponible"

        return view // Devolvemos la vista con los datos actualizados
    }
}

