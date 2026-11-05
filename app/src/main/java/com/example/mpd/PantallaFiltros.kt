package com.example.mpd


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mpd.ui.data.Alumno
/*
@Composable
fun PantallaFiltros(alumnos: List<Alumno>, onAtras: () -> Unit) {
    BackHandler { onAtras() } // botón atrás del sistema

    var fTexto by remember { mutableStateOf("") }
    var fCurso by remember { mutableStateOf("Todos") }
    var fBeca by remember { mutableStateOf(false) }
    var fEstado by remember { mutableStateOf("Todos") }
    var fNotaMin by remember { mutableStateOf(0f) }
    var fOrdenar by remember { mutableStateOf(false) }

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

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(onClick = onAtras) { Text("← Atrás") }
                Spacer(Modifier.width(12.dp))
                Text("Filtros", style = MaterialTheme.typography.titleLarge)
            }
        }
        item {
            OutlinedTextField(
                value = fTexto, onValueChange = { fTexto = it },
                label = { Text("Buscar por nombre") }, modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            SelectorDesplegable("Curso", listOf("Todos") + Cursos.lista, fCurso) { fCurso = it }
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = fBeca, onCheckedChange = { fBeca = it })
                Text("Solo con beca")
            }
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                listOf("Todos", "Aprobados", "Suspendidos").forEach { opcion ->
                    RadioButton(selected = fEstado == opcion, onClick = { fEstado = opcion })
                    Text(opcion)
                    Spacer(Modifier.width(4.dp))
                }
            }
        }
        item {
            Text("Nota mínima: ${fNotaMin.toInt()}")
            Slider(
                value = fNotaMin, onValueChange = { fNotaMin = it },
                valueRange = 0f..10f, steps = 9
            )
        }
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

        items(filtrados, key = { it.id }) { a ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(12.dp)) {
                    Text("${a.nombre} - ${a.curso}", style = MaterialTheme.typography.titleMedium)
                    Text("Edad: ${a.edad} | Nota: ${a.nota} (${a.estado()})")
                    Text(if (a.beca) "Con beca" else "Sin beca")
                }
            }
        }
    }
}*/