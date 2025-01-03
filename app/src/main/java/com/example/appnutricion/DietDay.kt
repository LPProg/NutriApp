package com.example.appnutricion.Diet

import android.os.Parcel
import android.os.Parcelable

// Definición de la clase DietDay, que representa un día de dieta con las comidas correspondientes.
data class DietDay(
    val breakfast: String? = null, // Desayuno, puede ser nulo
    val lunch: String? = null, // Almuerzo, puede ser nulo
    val snack: String? = null, // Merienda, puede ser nulo
    val dinner: String? = null // Cena, puede ser nulo
) : Parcelable {

    // Constructor que se usa para crear un objeto DietDay desde un Parcel
    constructor(parcel: Parcel) : this(
        breakfast = parcel.readString(), // Leemos el desayuno desde el Parcel
        lunch = parcel.readString(), // Leemos el almuerzo desde el Parcel
        snack = parcel.readString(), // Leemos la merienda desde el Parcel
        dinner = parcel.readString() // Leemos la cena desde el Parcel
    )

    // Método para escribir los datos del objeto DietDay en un Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(breakfast) // Escribimos el desayuno en el Parcel
        parcel.writeString(lunch) // Escribimos el almuerzo en el Parcel
        parcel.writeString(snack) // Escribimos la merienda en el Parcel
        parcel.writeString(dinner) // Escribimos la cena en el Parcel
    }

    // Método necesario para la interfaz Parcelable, devuelve un valor entero que describe el contenido (en este caso, 0)
    override fun describeContents(): Int {
        return 0
    }

    // Companion object que permite la creación de objetos Parcelable
    companion object CREATOR : Parcelable.Creator<DietDay> {
        // Crea un objeto DietDay a partir de un Parcel
        override fun createFromParcel(parcel: Parcel): DietDay {
            return DietDay(parcel) // Utilizamos el constructor con Parcel
        }

        // Devuelve un array de tamaño 'size' con elementos de tipo DietDay
        override fun newArray(size: Int): Array<DietDay?> {
            return arrayOfNulls(size) // Retorna un array de tamaño 'size' con valores nulos
        }
    }
}

