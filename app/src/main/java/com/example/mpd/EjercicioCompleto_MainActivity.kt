package com.example.alumnos

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

// ---------------------------------------------------------------
// 1) CLASES Y OBJETOS
// ---------------------------------------------------------------

// "object" = singleton (un unico objeto en toda la app)
object Cursos {
    val lista = listOf("1º DAM", "2º DAM", "1º DAW", "2º DAW")
}

// Clase con constructor, propiedades y un metodo.
// Es data class para poder usar .copy() al editar (Compose detecta el cambio).
data class Alumno(
    val id: Int,
    val nombre: String,
    val curso: String,
    val nota: Double,
    val beca: Boolean
) {
    fun aprobado(): Boolean = nota >= 5.0
    fun estado(): String = if (aprobado()) "Aprobado" else "Suspendido"
}

// ---------------------------------------------------------------
// 2) crearAlumno(): crea el objeto validando (try/catch, rango, vacios)
//    Devuelve null si algo esta mal (y avisa con Toast)
// ---------------------------------------------------------------
fun crearAlumno(
    context: Context,
    id: Int,
    nombre: String,
    curso: String,
    notaTxt: String,
    beca: Boolean
): Alumno? {
    // Datos vacios
    if (nombre.isBlank() || notaTxt.isBlank()) {
        Toast.makeText(context, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
        return null
    }
    // Nota es un numero (try/catch)
    val nota: Double = try {
        notaTxt.replace(",", ".").toDouble()
    } catch (e: NumberFormatException) {
        Toast.makeText(context, "La nota debe ser un numero", Toast.LENGTH_SHORT).show()
        return null
    }
    // Nota entre 0 y 10
    if (nota < 0.0 || nota > 10.0) {
        Toast.makeText(context, "La nota debe estar entre 0 y 10", Toast.LENGTH_SHORT).show()
        return null
    }
    // Crear el objeto
    return Alumno(id, nombre.trim(), curso, nota, beca)
}

// ---------------------------------------------------------------
// 3) Activity
// ---------------------------------------------------------------
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PantallaAlumnos()
                }
            }
        }
    }
}

// ---------------------------------------------------------------
// 4) Pantalla principal
// ---------------------------------------------------------------
@Composable
fun PantallaAlumnos() {
    val context = LocalContext.current

    // ----- Estado del FORMULARIO -----
    var nombre by remember { mutableStateOf("") }
    var curso by remember { mutableStateOf(Cursos.lista[0]) }
    var notaTxt by remember { mutableStateOf("") }
    var beca by remember { mutableStateOf(false) }

    // ----- Estado de la LISTA -----
    val alumnos = remember { mutableStateListOf<Alumno>() }  // la MutableList de Compose
    var siguienteId by remember { mutableStateOf(1) }
    var editandoId by remember { mutableStateOf<Int?>(null) } // null = modo añadir
    var mostrar by remember { mutableStateOf(false) }

    // ----- Estado de los FILTROS -----
    var fTexto by remember { mutableStateOf("") }          // texto
    var fCurso by remember { mutableStateOf("Todos") }     // desplegable (combobox)
    var fBeca by remember { mutableStateOf(false) }        // checkbox
    var fEstado by remember { mutableStateOf("Todos") }    // radio buttons
    var fNotaMin by remember { mutableStateOf(0f) }        // slider
    var fOrdenar by remember { mutableStateOf(false) }     // switch

    // ----- Funciones -----
    fun limpiarEntradas() {
        nombre = ""
        curso = Cursos.lista[0]
        notaTxt = ""
        beca = false
        editandoId = null
    }

    // AÑADIR (o GUARDAR si estamos editando)
    fun guardarAlumno() {
        val idUsar = editandoId ?: siguienteId
        val nuevo = crearAlumno(context, idUsar, nombre, curso, notaTxt, beca) ?: return

        if (editandoId == null) {
            alumnos.add(nuevo)
            siguienteId++
            Toast.makeText(context, "Alumno añadido a la lista", Toast.LENGTH_SHORT).show()
        } else {
            val pos = alumnos.indexOfFirst { it.id == editandoId }
            if (pos != -1) alumnos[pos] = nuevo   // reemplazar => Compose se actualiza
            Toast.makeText(context, "Alumno modificado", Toast.LENGTH_SHORT).show()
        }
        limpiarEntradas()
    }

    fun editar(a: Alumno) {
        nombre = a.nombre
        curso = a.curso
        notaTxt = a.nota.toString()
        beca = a.beca
        editandoId = a.id
    }

    fun borrar(a: Alumno) {
        alumnos.remove(a)
        if (editandoId == a.id) limpiarEntradas()
        Toast.makeText(context, "Alumno eliminado", Toast.LENGTH_SHORT).show()
    }

    // ----- Lista filtrada (se recalcula sola cuando cambia cualquier estado) -----
    val filtrados = alumnos
        .filter { it.nombre.contains(fTexto, ignoreCase = true) }
        .filter { fCurso == "Todos" || it.curso == fCurso }
        .filter { !fBeca || it.beca }
        .filter {
            when (fEstado) {
                "Aprobados" -> it.aprobado()
                "Suspendidos" -> !it.aprobado()
                else -> true
            }
        }
        .filter { it.nota >= fNotaMin }
        .let { if (fOrdenar) it.sortedByDescending { a -> a.nota } else it }

    // ----- UI -----
    LazyColumn(
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
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            SelectorDesplegable("Curso", Cursos.lista, curso) { curso = it }
        }
        item {
            OutlinedTextField(
                value = notaTxt,
                onValueChange = { notaTxt = it },
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
                Button(onClick = { guardarAlumno() }) {
                    Text(if (editandoId == null) "Añadir alumno" else "Guardar cambios")
                }
                if (editandoId != null) {
                    OutlinedButton(onClick = { limpiarEntradas() }) { Text("Cancelar") }
                }
                Button(onClick = { mostrar = !mostrar }) {
                    Text(if (mostrar) "Ocultar" else "Mostrar alumnos")
                }
            }
        }

        // ===== FILTROS (solo si se muestra la lista) =====
        if (mostrar) {
            item { HorizontalDivider() }
            item { Text("Filtros", style = MaterialTheme.typography.titleLarge) }

            // 1) Filtro de TEXTO
            item {
                OutlinedTextField(
                    value = fTexto,
                    onValueChange = { fTexto = it },
                    label = { Text("Buscar por nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            // 2) Filtro DESPLEGABLE (combobox / spinner)
            item {
                SelectorDesplegable("Curso", listOf("Todos") + Cursos.lista, fCurso) { fCurso = it }
            }
            // 3) Filtro CHECKBOX
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = fBeca, onCheckedChange = { fBeca = it })
                    Text("Solo con beca")
                }
            }
            // 4) Filtro RADIO BUTTONS (puntos de seleccion)
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    listOf("Todos", "Aprobados", "Suspendidos").forEach { opcion ->
                        RadioButton(selected = fEstado == opcion, onClick = { fEstado = opcion })
                        Text(opcion)
                        Spacer(Modifier.width(4.dp))
                    }
                }
            }
            // 5) Filtro SLIDER (nota minima)
            item {
                Text("Nota minima: ${fNotaMin.toInt()}")
                Slider(
                    value = fNotaMin,
                    onValueChange = { fNotaMin = it },
                    valueRange = 0f..10f,
                    steps = 9
                )
            }
            // 6) SWITCH (ordenar)
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(checked = fOrdenar, onCheckedChange = { fOrdenar = it })
                    Spacer(Modifier.width(8.dp))
                    Text("Ordenar por nota (mayor a menor)")
                }
            }
            item {
                OutlinedButton(onClick = {
                    fTexto = ""; fCurso = "Todos"; fBeca = false
                    fEstado = "Todos"; fNotaMin = 0f; fOrdenar = false
                }) { Text("Limpiar filtros") }
            }

            item { HorizontalDivider() }
            item {
                Text(
                    "Resultados: ${filtrados.size} de ${alumnos.size}",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // ===== LISTA =====
            items(filtrados, key = { it.id }) { a ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text("${a.nombre} - ${a.curso}", style = MaterialTheme.typography.titleMedium)
                        Text("Nota: ${a.nota} (${a.estado()})")
                        if (a.beca) Text("Con beca")
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

// ---------------------------------------------------------------
// 5) Desplegable reutilizable (equivale al Spinner / ComboBox)
// ---------------------------------------------------------------
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
                    onClick = {
                        onSeleccion(opcion)
                        abierto = false
                    }
                )
            }
        }
    }
}
