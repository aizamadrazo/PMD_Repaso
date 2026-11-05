package com.example.mpd


import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.mpd.ui.data.Alumno
import kotlinx.coroutines.launch

// ---------------- Cursos disponibles ----------------
/*
object Cursos {
    val lista = listOf("1º DAM", "2º DAM", "1º DAW", "2º DAW")
}

// ---------------- Datos de ejemplo (Int, String, Double, Boolean) ----------------
fun datosEjemplo(): List<Alumno> = listOf(
    Alumno(1, "Ane Etxeberria", "1º DAM", 19, 8.5, true),
    Alumno(2, "Jon Garmendia", "2º DAM", 21, 4.2, false),
    Alumno(3, "Maite Urrutia", "1º DAW", 18, 6.7, false),
    Alumno(4, "Iker Zubizarreta", "2º DAW", 22, 9.3, true),
    Alumno(5, "Leire Aranburu", "1º DAM", 20, 3.8, true),
    Alumno(6, "Unai Goikoetxea", "2º DAM", 23, 5.0, false),
    Alumno(7, "Nerea Ibarra", "1º DAW", 19, 7.4, true),
    Alumno(8, "Aitor Mendizabal", "2º DAW", 24, 2.9, false)
)

// ---------------- Validación (vacíos, try/catch, rangos) ----------------
fun crearAlumno(
    context: Context,
    id: Int,
    nombre: String,
    curso: String,
    edadTxt: String,
    notaTxt: String,
    beca: Boolean
): Alumno? {
    if (nombre.isBlank() || edadTxt.isBlank() || notaTxt.isBlank()) {
        Toast.makeText(context, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
        return null
    }
    val edad: Int = try {
        edadTxt.trim().toInt()
    } catch (e: NumberFormatException) {
        Toast.makeText(context, "La edad debe ser un número entero", Toast.LENGTH_SHORT).show()
        return null
    }
    val nota: Double = try {
        notaTxt.replace(",", ".").toDouble()
    } catch (e: NumberFormatException) {
        Toast.makeText(context, "La nota debe ser un número", Toast.LENGTH_SHORT).show()
        return null
    }
    if (edad < 1 || edad > 99) {
        Toast.makeText(context, "La edad debe estar entre 1 y 99", Toast.LENGTH_SHORT).show()
        return null
    }
    if (nota < 0.0 || nota > 10.0) {
        Toast.makeText(context, "La nota debe estar entre 0 y 10", Toast.LENGTH_SHORT).show()
        return null
    }
    return Alumno(id, nombre.trim(), curso, edad, nota, beca)
}

// ---------------- PANTALLA CRUD ----------------
@Composable
fun PantallaCrud() {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // La lista vive aquí y se la pasamos a la pantalla de filtros
    val alumnos = remember { mutableStateListOf<Alumno>().apply { addAll(datosEjemplo()) } }
    var verFiltros by remember { mutableStateOf(false) }

    // Estado del formulario
    var nombre by remember { mutableStateOf("") }
    var curso by remember { mutableStateOf(Cursos.lista[0]) }
    var edadTxt by remember { mutableStateOf("") }
    var notaTxt by remember { mutableStateOf("") }
    var beca by remember { mutableStateOf(false) }
    var editandoId by remember { mutableStateOf<Int?>(null) } // null = modo añadir

    fun limpiar() {
        nombre = ""; curso = Cursos.lista[0]; edadTxt = ""; notaTxt = ""
        beca = false; editandoId = null
    }

    fun guardar() {
        val idUsar = editandoId ?: ((alumnos.maxOfOrNull { it.id } ?: 0) + 1)
        val nuevo = crearAlumno(context, idUsar, nombre, curso, edadTxt, notaTxt, beca) ?: return
        if (editandoId == null) {
            alumnos.add(nuevo)
            Toast.makeText(context, "Alumno añadido", Toast.LENGTH_SHORT).show()
        } else {
            val pos = alumnos.indexOfFirst { it.id == editandoId }
            if (pos != -1) alumnos[pos] = nuevo
            Toast.makeText(context, "Alumno modificado", Toast.LENGTH_SHORT).show()
        }
        limpiar()
    }

    fun editar(a: Alumno) {
        nombre = a.nombre; curso = a.curso; edadTxt = a.edad.toString()
        notaTxt = a.nota.toString(); beca = a.beca; editandoId = a.id
        scope.launch { listState.animateScrollToItem(0) } // sube al formulario
    }

    fun borrar(a: Alumno) {
        alumnos.remove(a)
        if (editandoId == a.id) limpiar()
        Toast.makeText(context, "Alumno eliminado", Toast.LENGTH_SHORT).show()
    }

    if (verFiltros) {
        // Pantalla de filtros; su botón Atrás pone verFiltros = false
        PantallaFiltros(alumnos) { verFiltros = false }
    } else {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // ===== FORMULARIO =====
            item {
                Text(
                    if (editandoId == null) "Nuevo alumno" else "Editando alumno",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            item {
                OutlinedTextField(
                    value = nombre, onValueChange = { nombre = it },
                    label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth()
                )
            }
            item { SelectorDesplegable("Curso", Cursos.lista, curso) { curso = it } }
            item {
                OutlinedTextField(
                    value = edadTxt, onValueChange = { edadTxt = it },
                    label = { Text("Edad") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                OutlinedTextField(
                    value = notaTxt, onValueChange = { notaTxt = it },
                    label = { Text("Nota (0-10)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = beca, onCheckedChange = { beca = it })
                    Text("Tiene beca")
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { guardar() }) {
                        Text(if (editandoId == null) "Añadir" else "Guardar")
                    }
                    if (editandoId != null) {
                        OutlinedButton(onClick = { limpiar() }) { Text("Cancelar") }
                    }
                    Button(onClick = { verFiltros = true }) { Text("Filtrar") }
                }
            }

            item { HorizontalDivider() }
            item { Text("Alumnos (${alumnos.size})", style = MaterialTheme.typography.titleLarge) }

            // ===== LISTA con Editar / Borrar =====
            items(alumnos, key = { it.id }) { a ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text("${a.nombre} - ${a.curso}", style = MaterialTheme.typography.titleMedium)
                        Text("Edad: ${a.edad} | Nota: ${a.nota} (${a.estado()})")
                        Text(if (a.beca) "Con beca" else "Sin beca")
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { editar(a) }) { Text("Editar") }
                            OutlinedButton(onClick = { borrar(a) }) { Text("Borrar") }
                        }
                    }
                }
            }
        }
    }
}

// ---------------- Desplegable reutilizable (Spinner / ComboBox) ----------------
@Composable
fun SelectorDesplegable(
    etiqueta: String,
    opciones: List<String>,
    seleccionado: String,
    onSeleccion: (String) -> Unit
) {
    var abierto by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { abierto = true }, modifier = Modifier.fillMaxWidth()) {
            Text("$etiqueta: $seleccionado")
        }
        DropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion) },
                    onClick = { onSeleccion(opcion); abierto = false }
                )
            }
        }
    }
}*/