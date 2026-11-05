
package com.example.mpd

import android.content.Intent
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

class DescuentoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                PantallaDescuento()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDescuento() {

    val context = LocalContext.current

    // Opciones de categoría y forma de pago
    val categorias = listOf(
        "Normal" to 0,
        "Familia numerosa" to 15,
        "Beca" to 20
    )

    val pagos = listOf(
        "Tarjeta" to 0,
        "Transferencia" to 3,
        "Efectivo" to 5
    )

    // ESTADOS DEL FORMULARIO Admes aeto coge lso cambios
    var precio by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf(categorias[0]) }
    var menuAbierto by remember { mutableStateOf(false) }
    var pago by remember { mutableStateOf(pagos[0]) }
    var repetidor by remember { mutableStateOf(false) }

    // FUNCIÓN PARA CALCULAR EL DESCUENTO
    fun calcular() {
        //par auqe coag comoas y punto
        val p = precio.replace(',', '.').toDoubleOrNull()

        if (p == null || p <= 0) {
            Toast.makeText(
                context,
                "Introduce un precio válido",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val desglose = mutableListOf<String>()
        var descuento = 0

        // Descuento por categoría
        if (categoria.second > 0) {
            descuento += categoria.second
            desglose.add("${categoria.first}: ${categoria.second}%")
        }

        // Descuento por forma de pago
        if (pago.second > 0) {
            descuento += pago.second
            desglose.add(
                "Pago por ${pago.first.lowercase()}: ${pago.second}%"
            )
        }

        // Descuento por no repetidor
        if (repetidor) {
            descuento += 5
            desglose.add("No repetidor: 5%")
        }

        // Límite máximo del descuento
        if (descuento > 50) {
            descuento = 50
            desglose.add("Límite máximo aplicado: 50%")
        }

        val importeDescuento = p * descuento / 100
        val precioFinal = p - importeDescuento

        // Guardamos los resultados
        // Ahora enviamos los resultados a la otra Activity mediante Intent

        /*Intet usar ao rapasr datso*/

        val intent = Intent(context, ResultadoActivity::class.java)

        intent.putExtra("precio", p)
        intent.putExtra("descuento", descuento)
        intent.putExtra("importeDescuento", importeDescuento)
        intent.putExtra("precioFinal", precioFinal)
        intent.putExtra("desglose", desglose.joinToString("\n"))

        context.startActivity(intent)
    }

    // PANTALLA PRINCIPAL
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            "Calculadora de descuento",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        // PRECIO
        OutlinedTextField(
            value = precio,
            onValueChange = {
                precio = it.filter { c ->
                    c.isDigit() || c == '.' || c == ','
                }
            },
            label = {
                Text("Precio (€)")
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // CATEGORÍA
        ExposedDropdownMenuBox(
            expanded = menuAbierto,
            onExpandedChange = {
                menuAbierto = it
            }
        ) {
            OutlinedTextField(
                value = categoria.first,
                onValueChange = {},
                readOnly = true,
                label = {
                    Text("Categoría")
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = menuAbierto
                    )
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = menuAbierto,
                onDismissRequest = {
                    menuAbierto = false
                }
            ) {
                categorias.forEach { opcion ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                "${opcion.first} (+${opcion.second}%)"
                            )
                        },
                        onClick = {
                            categoria = opcion
                            menuAbierto = false
                        }
                    )
                }
            }
        }

        // FORMA DE PAGO
        Text("Forma de pago")

        pagos.forEach { opcion ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = pago == opcion,
                    onClick = {
                        pago = opcion
                    }
                )

                Text("${opcion.first} (+${opcion.second}%)")
            }
        }

        // CHECKBOX
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = repetidor,
                onCheckedChange = {
                    repetidor = it
                }
            )

            Text("No repetidor (+5%)")
        }

        // BOTÓN RESULTADO
        Button(
            onClick = {
                calcular()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Resultado")
        }
    }
}
