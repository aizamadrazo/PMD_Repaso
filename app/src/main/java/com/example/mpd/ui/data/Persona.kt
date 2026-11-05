package com.example.mpd.ui.data

open class Persona(var nombre: String, var edad: Int) {
    open fun descripcion(): String = "$nombre ($edad años)"
}