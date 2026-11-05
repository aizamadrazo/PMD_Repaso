
package com.example.mpd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

class ResultadoActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Recibimos los datos que nos manda la otra Activity
        val precio = intent.getDoubleExtra("precio", 0.0)
        val descuento = intent.getIntExtra("descuento", 0)
        val importeDescuento = intent.getDoubleExtra("importeDescuento", 0.0)
        val precioFinal = intent.getDoubleExtra("precioFinal", 0.0)
        val desglose = intent.getStringExtra("desglose") ?: ""

        setContent {
            MaterialTheme {
                PantallaResultado(
                    precio = precio,
                    descuento = descuento,
                    importeDescuento = importeDescuento,
                    precioFinal = precioFinal,
                    desglose = desglose,
                    onVolver = {
                        finish()
                    }
                )
            }
        }
    }
}

//traemos las variables de la otra Activity
@Composable
fun PantallaResultado(
    precio: Double,
    descuento: Int,
    importeDescuento: Double,
    precioFinal: Double,
    desglose: String,
    onVolver: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            "Resultado",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        // TARJETA CON EL RESULTADO
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    "Precio original: ${"%.2f".format(precio)} €"
                )

                Text("Descuento: $descuento%")

                Text(
                    "Ahorras: ${"%.2f".format(importeDescuento)} €"
                )

                HorizontalDivider()

                Text(
                    "Precio final: ${"%.2f".format(precioFinal)} €",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // TARJETA CON EL DESGLOSE
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    "Desglose",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                //si no ets bacio
                if (desglose.isNotBlank()) {

                    desglose.split("\n").forEach { linea ->
                        Text("• $linea")
                    }

                } else {

                    Text("No hay descuentos aplicados.")
                }
            }
        }

        // BOTÓN PARA VOLVER
        Button(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}
