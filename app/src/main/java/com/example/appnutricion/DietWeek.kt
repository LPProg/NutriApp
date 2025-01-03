package com.example.appnutricion.Diet

// Esta clase representa una semana de dieta, donde cada día de la semana tiene una instancia de DietDay
data class DietWeek(
    val monday: DietDay = DietDay(),
    val tuesday: DietDay = DietDay(),
    val wednesday: DietDay = DietDay(),
    val thursday: DietDay = DietDay(),
    val friday: DietDay = DietDay(),
    val saturday: DietDay = DietDay(),
    val sunday: DietDay = DietDay()
)
