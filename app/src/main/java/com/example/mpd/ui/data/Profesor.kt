package com.example.mpd.ui.data

data class Profesor(
    val id: Int,
    val nombre: String,
    val cursos: List<String>,       // cursos en los que da clase
    val edad: Int,
    val fechaNacimiento: String,    // formato "dd/MM/yyyy"
    val numeroProfesor: Int,        // solo del 1 al 20
    val jefeDepartamento: Boolean   // true = es jefe, false = no
)