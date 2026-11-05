package com.example.mpd

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import com.example.mpd.ui.data.Profesor
import kotlinx.coroutines.launch
// Cursos que se pueden elegir en el desplegable
/*
private val cursosProfesor = listOf("Informática", "Márketing", "Comercio", "Administración")

@Composable

fun PantallaCrudProfesor() {
    val context = LocalContext.current
    // Lista donde se guardan los profesores
    val profesores = remember { mutableStateListOf<Profesor>() }
    // Estado de los inputs
    var nombre by remember { mutableStateOf("") }
    var curso by remember { mutableStateOf(cursosProfesor[0]) }
    var edadTxt by remember { mutableStateOf("") }
    var fechaTxt by remember { mutableStateOf("") }
    var numeroTxt by remember { mutableStateOf("") }
    var jefe by remember { mutableStateOf(false) }
    var editandoId by remember { mutableStateOf<Int?>(null) } // null = modo añadir
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    //funcion apra lipiar los datso despues de guardar
    fun limpiar() {
        nombre = ""
        curso = cursosProfesor[0]
        edadTxt = ""
        fechaTxt = ""
        numeroTxt = ""
        jefe = false
        editandoId = null
    }

    fun guardar() {
        // 1) Campos vacíos
        if (nombre.isBlank() || edadTxt.isBlank() || fechaTxt.isBlank() || numeroTxt.isBlank()) {
            Toast.makeText(context, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }
        // 2) La edad es un número (try/catch)
        val edad: Int = try {
            edadTxt.trim().toInt()
        } catch (e: NumberFormatException) {
            Toast.makeText(context, "La edad debe ser un número entero", Toast.LENGTH_SHORT).show()
            return
        }
        // 3) El número de profesor es un número (try/catch)
        val numero: Int = try {
            numeroTxt.trim().toInt()
        } catch (e: NumberFormatException) {
            Toast.makeText(context, "El número de profesor debe ser un número entero", Toast.LENGTH_SHORT).show()
            return
        }

        // 4) Rangos
        if (edad < 18 || edad > 65) {
            Toast.makeText(context, "La edad debe estar entre 18 y 99", Toast.LENGTH_SHORT).show()
            return
        }
        if (numero < 1 || numero > 20) {
            Toast.makeText(context, "El número de profesor debe estar entre 1 y 20", Toast.LENGTH_SHORT).show()
            return
        }
        val idUsar = editandoId ?: ((profesores.maxOfOrNull { it.id } ?: 0) + 1)

        // 5) Crear el profesor y guardarlo
        val nuevo = Profesor(
            id = idUsar,
            nombre = nombre.trim(),
            cursos = listOf(curso),
            edad = edad,
            fechaNacimiento = fechaTxt,
            numeroProfesor = numero,
            jefeDepartamento = jefe
        )
        if (editandoId == null) {
            profesores.add(nuevo)
            Toast.makeText(context, "Profesor añadido", Toast.LENGTH_SHORT).show()
        } else {
            val pos = profesores.indexOfFirst { it.id == editandoId }
            if (pos != -1) profesores[pos] = nuevo   // reemplazar => Compose se actualiza
            Toast.makeText(context, "Profesor modificado", Toast.LENGTH_SHORT).show()
        }
        limpiar()
    }
    fun editar(p: Profesor) {
        nombre = p.nombre
        curso = p.cursos.firstOrNull() ?: cursosProfesor[0]
        edadTxt = p.edad.toString()
        fechaTxt = p.fechaNacimiento
        numeroTxt = p.numeroProfesor.toString()
        jefe = p.jefeDepartamento
        editandoId = p.id
        scope.launch { listState.animateScrollToItem(0) } // sube al formulario
    }

    fun borrar(p: Profesor) {
        profesores.remove(p)
        if (editandoId == p.id) limpiar() // si lo estabas editando, vacía el formulario
        Toast.makeText(context, "Profesor eliminado", Toast.LENGTH_SHORT).show()
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                if (editandoId == null) "Nuevo profesor" else "Editando profesor",
                style = MaterialTheme.typography.titleLarge
            )
        }
        // Nombre
        item {
            OutlinedTextField(
                value = nombre, onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Curso en el que da clase (combobox)
        item { SelectorDesplegable("Curso", cursosProfesor, curso) { curso = it } }

        // Edad
        item {
            OutlinedTextField(
                value = edadTxt, onValueChange = { edadTxt = it },
                label = { Text("Edad") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Fecha de nacimiento
        item {
            OutlinedTextField(
                value = fechaTxt, onValueChange = { fechaTxt = it },
                label = { Text("Fecha de nacimiento (dd/MM/yyyy)") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Número de profesor (1-20)
        item {
            OutlinedTextField(
                value = numeroTxt, onValueChange = { numeroTxt = it },
                label = { Text("Número de profesor (1-20)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Jefe de departamento (checkbox)
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = jefe, onCheckedChange = { jefe = it })
                Text("Jefe de departamento")
            }
        }
        item { HorizontalDivider() }

        // Botón (sin lógica todavía)
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { guardar() }) {
                    Text(if (editandoId == null) "Añadir" else "Guardar")
                }
                if (editandoId != null) {
                    OutlinedButton(onClick = { limpiar() }) { Text("Cancelar") }
                }
            }
        }
        items(profesores, key = { it.id }) { p ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text(p.nombre, style = MaterialTheme.typography.titleMedium)
                    Text("Cursos: ${p.cursos.joinToString(", ")}")
                    Text("Edad: ${p.edad} | Nacimiento: ${p.fechaNacimiento}")
                    Text("Nº de profesor: ${p.numeroProfesor}")
                    Text(if (p.jefeDepartamento) "Jefe de departamento" else "Profesor")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { editar(p) }) { Text("Editar") }
                        OutlinedButton(onClick = { borrar(p) }) { Text("Borrar") }
                    }
                }

            }
        }
    }
}*/

