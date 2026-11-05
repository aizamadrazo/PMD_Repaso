package com.example.mpd

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.mpd.ui.data.Alumno
import kotlin.math.roundToInt

class AlumnosActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                PantallaAlumnos()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAlumnos() {
    val context = LocalContext.current

    // ---------- DATOS ----------
    //la lisat de alumnos con alumanos ya creados y los nuevos
    val alumnos = remember {
        // Datos de ejemplo para probar los filtros
        mutableStateListOf(
            Alumno("Ane", 18, "1º DAM", false, "Mañana"),
            Alumno("Jon", 22, "2º DAM", true, "Tarde"),
            Alumno("Maite", 19, "1º DAW", false, "Tarde"),
            Alumno("Iker", 31, "2º DAW", true, "Mañana"),
            Alumno("Leire", 25, "2º DAM", false, "Mañana"),
            Alumno("Unai", 40, "1º DAW", true, "Tarde")
        )
    }
    //los datso apra lso selcotrs o combox
    val cursos = listOf("1º DAM", "2º DAM", "1º DAW", "2º DAW")
    val turnos = listOf("Mañana", "Tarde")

    // ---------- ESTADO DEL FORMULARIO ----------
    var nombre by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var curso by remember { mutableStateOf(cursos[0]) }
    var repetidor by remember { mutableStateOf(false) }
    var turno by remember { mutableStateOf(turnos[0]) }
    var menuAbierto by remember { mutableStateOf(false) }

    // ---------- ESTADO DE LA PANTALLA ----------
    //mosatralista se incia cmo false es comao
    var mostrarLista by remember { mutableStateOf(false) }
   //el indice que s eenta editando
    var editandoIndice by remember { mutableStateOf<Int?>(null) }

    // ---------- ESTADO DE LOS FILTROS ----------
    val edadMinima = 14f
    val edadMaxima = 60f
    var filtroTexto by remember { mutableStateOf("") }                // buscador (TextField)
    var filtroCurso by remember { mutableStateOf("Todos") }           // spinner
    var menuFiltroCurso by remember { mutableStateOf(false) }
    var filtroSoloRepetidores by remember { mutableStateOf(false) }   // checkbox
    var filtroSoloNoRepetidores by remember { mutableStateOf(false) } // checkbox
    var filtroTurno by remember { mutableStateOf("Todos") }           // radio buttons
    var filtroEdad by remember { mutableStateOf(edadMinima..edadMaxima) } // selector de rango
    var ordenarAZ by remember { mutableStateOf(false) }               // switch
//esot es un alista con al opcion tods y las demas opcioens
    val opcionesCursoFiltro = listOf("Todos") + cursos
    val opcionesTurnoFiltro = listOf("Todos") + turnos

    // Lista filtrada. Guardamos el índice ORIGINAL (withIndex) para que
    // Editar y Borrar sigan funcionando bien aunque haya filtros activos.
    /* filtros */
    val alumnosFiltrados = alumnos
        //coagemso el index de cada usario
        .withIndex()
        //flire apra filtrs y _ represta el index y a represenat al alumno
        .filter { (_, a) ->
            /*compuba si esta vacio o que el texto contenga alumno name y quitel lo esapcios e ignora mayuscualas y
            el curso es == o el cuaso del alumno es == al de filtrocurso y
            cuando etsa esgat flirto reptidor en false enonce sno flitr apeor sino a.reptidor buscay
            si si fitro norepteidor esta en flaso entoce a busca los no repetidros y
            cuando si filtro turon == todos 0 alumno turosn = a flitroturno
            y y que la edad fleat este en el rango  */
            (filtroTexto.isBlank() || a.nombre.contains(filtroTexto.trim(), ignoreCase = true)) &&
                    (filtroCurso == "Todos" || a.curso == filtroCurso) &&
                    (!filtroSoloRepetidores || a.repetidor) &&
                    (!filtroSoloNoRepetidores || !a.repetidor) &&
                    (filtroTurno == "Todos" || a.turno == filtroTurno) &&
                    (a.edad.toFloat() in filtroEdad)
        }
        //flitro de aprdeanar la lista de a a laz
        .let { lista ->
            if (ordenarAZ) lista.sortedBy { it.value.nombre.lowercase() } else lista
        }

    // =====================================================
    // FUNCIONES SUELTAS
    // =====================================================
    //deja el fomulario limpio
    fun limpiarFormulario() {
        nombre = ""; edad = ""; curso = cursos[0]; repetidor = false; turno = turnos[0]
        editandoIndice = null
    }
    //limpi laos fltros
    fun limpiarFiltros() {
        filtroTexto = ""
        filtroCurso = "Todos"
        filtroSoloRepetidores = false
        filtroSoloNoRepetidores = false
        filtroTurno = "Todos"
        filtroEdad = edadMinima..edadMaxima
        ordenarAZ = false
    }
    //eda pasom int, si son null o bavio erro  sino
    fun anadirAlumno() {
        val e = edad.toIntOrNull()
        if (nombre.isBlank() || e == null) {
            Toast.makeText(context, "Rellena nombre y edad", Toast.LENGTH_SHORT).show()
            return
        }
        //le metmeos los datos a alumnso usadno al clasee alumno
        alumnos.add(Alumno(nombre.trim(), e, curso, repetidor, turno))
        Toast.makeText(context, "Alumno añadido", Toast.LENGTH_SHORT).show()
        limpiarFormulario()
    }
    //editar coge el numero de indice ose pororden de como etsan en el array pord ecirlos asi
    //hay tien un numeor y usamo eso como indice de reconciminto
    fun editarAlumno(indice: Int) {
        //a es de alimnos el que use el indice y los usamos para cargar
        val a = alumnos[indice]
        nombre = a.nombre
        edad = a.edad.toString()
        curso = a.curso
        repetidor = a.repetidor
        turno = a.turno
        //lo metemos apra que luego cuando editemso se ap si se ets editando osa al ora de gaurda comose
        //usa el mismo botn peus apr auqe identifique si vien de editar o para crear
        editandoIndice = indice
    }
    //el de guarda
    fun guardarCambios() {
        //trameoa el indice del que se edita
        val indice = editandoIndice ?: return
        //pasoamo edad  a int para manejarlo
        val e = edad.toIntOrNull()
        if (nombre.isBlank() || e == null) {
            Toast.makeText(context, "Rellena nombre y edad", Toast.LENGTH_SHORT).show()
            return
        }
        //sacmaos de alumnso el de ese idice y le amtemos acla casle alumn lso cambios
        alumnos[indice] = Alumno(nombre.trim(), e, curso, repetidor, turno)
        Toast.makeText(context, "Alumno modificado", Toast.LENGTH_SHORT).show()
        limpiarFormulario()
    }
    //elima usando incie del selcioando ya demas si se etab editanod se limpia le fomualrio
    fun eliminarAlumno(indice: Int) {
        alumnos.removeAt(indice)
        if (editandoIndice != null) limpiarFormulario()
        Toast.makeText(context, "Alumno eliminado", Toast.LENGTH_SHORT).show()
    }
    //funcioan para el botno que mutra los filtros para desactiva y atcivar al darle albotn
    fun mostrarAlumnos() {
        mostrarLista = !mostrarLista
    }

    // =====================================================
    // PANTALLA
    // =====================================================
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        //si no hhy indice el titlos se queda como nuevo alumno
        Text(
            if (editandoIndice == null) "Nuevo alumno" else "Editando alumno",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        // Nombre
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Edad
        OutlinedTextField(
            value = edad,
            //flitra parque se asolo digitos
            onValueChange = { edad = it.filter { c -> c.isDigit() } },
            label = { Text("Edad") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        //el combosx fucnion de saber si el menu esta abieto
        // Curso (Spinner)
        ExposedDropdownMenuBox(
            expanded = menuAbierto,
            onExpandedChange = { menuAbierto = it }
        ) {
            //
            OutlinedTextField(
                value = curso,
                onValueChange = {},
                readOnly = true,
                label = { Text("Curso") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuAbierto) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = menuAbierto,
                onDismissRequest = { menuAbierto = false }
            ) {
                cursos.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion) },
                        onClick = { curso = opcion; menuAbierto = false }
                    )
                }
            }
        }

        // Repetidor (CheckBox)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = repetidor, onCheckedChange = { repetidor = it })
            Text("Repetidor")
        }

        // Turno (RadioButtons)
        Text("Turno")
        Row(verticalAlignment = Alignment.CenterVertically) {
            turnos.forEach { opcion ->
                RadioButton(selected = turno == opcion, onClick = { turno = opcion })
                Text(opcion)
                Spacer(Modifier.width(16.dp))
            }
        }

        // ---------- BOTONES DEL FORMULARIO ----------
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { if (editandoIndice == null) anadirAlumno() else guardarCambios() },
                modifier = Modifier.weight(1f)
            ) { Text(if (editandoIndice == null) "Añadir" else "Guardar") }

            Button(onClick = { mostrarAlumnos() }, modifier = Modifier.weight(1f)) {
                Text(if (mostrarLista) "Ocultar" else "Mostrar")
            }
        }

        // ---------- LISTA + FILTROS (solo si se ha pulsado Mostrar) ----------
        if (mostrarLista) {
            HorizontalDivider()

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Filtros", style = MaterialTheme.typography.titleMedium)

                    // 1) Buscador por nombre (TextField)
                    OutlinedTextField(
                        value = filtroTexto,
                        onValueChange = { filtroTexto = it },
                        label = { Text("Buscar por nombre") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // 2) Curso (Spinner con opción "Todos")
                    ExposedDropdownMenuBox(
                        expanded = menuFiltroCurso,
                        onExpandedChange = { menuFiltroCurso = it }
                    ) {
                        OutlinedTextField(
                            value = filtroCurso,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Curso") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuFiltroCurso)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = menuFiltroCurso,
                            onDismissRequest = { menuFiltroCurso = false }
                        ) {
                            opcionesCursoFiltro.forEach { opcion ->
                                DropdownMenuItem(
                                    text = { Text(opcion) },
                                    onClick = { filtroCurso = opcion; menuFiltroCurso = false }
                                )
                            }
                        }
                    }

                    // 3) Checkboxes (repetidores / no repetidores)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = filtroSoloRepetidores,
                            onCheckedChange = {
                                filtroSoloRepetidores = it
                                if (it) filtroSoloNoRepetidores = false
                            }
                        )
                        Text("Solo repetidores")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = filtroSoloNoRepetidores,
                            onCheckedChange = {
                                filtroSoloNoRepetidores = it
                                if (it) filtroSoloRepetidores = false
                            }
                        )
                        Text("Solo no repetidores")
                    }

                    // 4) Turno (RadioButtons con opción "Todos")
                    Text("Turno")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        opcionesTurnoFiltro.forEach { opcion ->
                            RadioButton(
                                selected = filtroTurno == opcion,
                                onClick = { filtroTurno = opcion }
                            )
                            Text(opcion)
                            Spacer(Modifier.width(8.dp))
                        }
                    }

                    // 5) Rango de edad (RangeSlider)
                    Text("Edad: ${filtroEdad.start.roundToInt()} - ${filtroEdad.endInclusive.roundToInt()} años")
                    RangeSlider(
                        value = filtroEdad,
                        onValueChange = { filtroEdad = it },
                        valueRange = edadMinima..edadMaxima,
                        steps = (edadMaxima - edadMinima).toInt() - 1
                    )

                    // 6) Ordenar A-Z (Switch)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Switch(checked = ordenarAZ, onCheckedChange = { ordenarAZ = it })
                        Spacer(Modifier.width(8.dp))
                        Text("Ordenar por nombre (A-Z)")
                    }

                    // Botón limpiar filtros
                    OutlinedButton(
                        onClick = { limpiarFiltros() },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Limpiar filtros") }
                }
            }

            Text(
                "Mostrando ${alumnosFiltrados.size} de ${alumnos.size} alumnos",
                style = MaterialTheme.typography.titleLarge
            )

            if (alumnosFiltrados.isEmpty()) {
                Text("No hay alumnos que coincidan")
            }

            // indice = posición REAL en la lista "alumnos" (no en la filtrada)
            alumnosFiltrados.forEach { (indice, a) ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text("${a.nombre} (${a.edad} años)", style = MaterialTheme.typography.titleMedium)
                        Text("${a.curso} - Turno ${a.turno}")
                        Text(if (a.repetidor) "Repetidor" else "No repetidor")

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { editarAlumno(indice) }) { Text("Editar") }
                            OutlinedButton(onClick = { eliminarAlumno(indice) }) { Text("Borrar") }
                        }
                    }
                }
            }
        }
    }
}