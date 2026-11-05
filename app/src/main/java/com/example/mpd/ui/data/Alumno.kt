package com.example.mpd.ui.data


//alumno herda nombre y eda de persiona lo demas son coas especificas de alumno
class Alumno(
    nombre: String,
    edad: Int,
    var curso: String,
    var repetidor: Boolean,
    var turno: String
) : Persona(nombre, edad) {
    //udncion de descricion vara el chabox si es repetidor ture pone rerepetidor sino nada
    //la descirpcion es curso turno y repetidor
    override fun descripcion(): String {
        val rep = if (repetidor) " [Repetidor]" else ""
        return "${super.descripcion()} - $curso - $turno$rep"
    }
}