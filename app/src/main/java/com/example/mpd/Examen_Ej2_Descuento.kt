package com.example.descuento

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/*
 IMPORTANTE: registrar la segunda Activity en AndroidManifest.xml, dentro de <application>:
     <activity android:name=".ResultadoActivity" />
*/

// ---------------- Funciones de calculo ----------------

fun calcularDescuento(tipoCliente: String, cupon: Boolean): Double {
    var descuento = when (tipoCliente) {
        "Estudiante" -> 0.10
        "Vip" -> 0.20
        else -> 0.0
    }
    if (cupon) descuento += 0.05
    return descuento
}

fun calcularPrecioFinal(precio: Double, descuento: Double): Double {
    return precio * (1 - descuento)
}
/*
// ---------------- MainActivity ----------------

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) { PantallaPrincipal() }
            }
        }
    }
}*/

@Composable
fun PantallaPrincipal() {
    val context = LocalContext.current

    var precioTxt by remember { mutableStateOf("") }
    var tipoCliente by remember { mutableStateOf("Estudiante") }
    var cupon by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Calcular precio final", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = precioTxt,
            onValueChange = { precioTxt = it },
            label = { Text("Precio") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        // Radio buttons: tipo de cliente
        listOf("Estudiante", "Vip", "Normal").forEach { tipo ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = tipoCliente == tipo, onClick = { tipoCliente = tipo })
                Text(tipo)
            }
        }

        // Checkbox: cupon
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = cupon, onCheckedChange = { cupon = it })
            Text("Cupon (5% extra)")
        }

        Button(onClick = {
            // 1) try/catch: comprobar que el precio es un numero
            val precio: Double = try {
                precioTxt.replace(",", ".").toDouble()
            } catch (e: NumberFormatException) {
                Toast.makeText(context, "El precio debe ser un numero", Toast.LENGTH_SHORT).show()
                return@Button   // no dejar continuar
            }

            // 2) calcular
            val descuento = calcularDescuento(tipoCliente, cupon)
            val precioFinal = calcularPrecioFinal(precio, descuento)

            // 3) pasar el resultado a la otra pantalla con un Intent
            val intent = Intent(context, ResultadoActivity::class.java)
            intent.putExtra("precioFinal", precioFinal)
            context.startActivity(intent)
        }) {
            Text("Calcular")
        }
    }
}

// ---------------- ResultadoActivity ----------------

class ResultadoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Recoger el dato del Intent
        val precioFinal = intent.getDoubleExtra("precioFinal", 0.0)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PantallaResultado(precioFinal) { finish() } // finish() = volver atras
                }
            }
        }
    }
}

@Composable
fun PantallaResultado(precioFinal: Double, onVolver: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Precio final: ${"%.2f".format(precioFinal)} €", style = MaterialTheme.typography.headlineMedium)
        Button(onClick = onVolver) { Text("Volver") }
    }
}
