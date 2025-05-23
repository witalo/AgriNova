package com.example.agrinova.util
import java.text.SimpleDateFormat
import java.util.*

object Constants {
    const val GRAPHQL_URL = "http://190.119.143.147/graphql" /*"TU_URL_BASE_GRAPHQL"*/
//    const val GRAPHQL_URL = "http://192.168.1.5:9000/graphql" /*"TU_URL_BASE_GRAPHQL"*/
//    const val GRAPHQL_URL = "http://192.168.1.245:9000/graphql" /*"TU_URL_BASE_GRAPHQL"*/
    const val DATABASE_NAME = "db_agrinova"/*"app_database"*/
    // Método para obtener la fecha actual
    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Formato de fecha
        return dateFormat.format(Date()) // Fecha actual
    }
    // Obtener la fecha en cualquier momento con esta propiedad
    val DATE_NOW: String
        get() = getCurrentDate()
    object GraphQL {
        const val TIMEOUT_SECONDS = 60L
    }
}