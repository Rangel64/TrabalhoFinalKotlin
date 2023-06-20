package com.example.trabalhofinal.TelaPedido

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.trabalhofinal.TelaPedido.ui.theme.TrabalhoFinalTheme
import com.example.trabalhofinal.telaCliente.TelaClienteInserir
import com.example.trabalhofinal.telaCliente.TelaClienteMostrar

class TelaPedidoPrincipal : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrabalhoFinalTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting9("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting9(name: String, modifier: Modifier = Modifier) {
    val contexto = LocalContext.current
    Row(
        modifier = Modifier
            .padding(top = 48.dp)
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalArrangement = Arrangement.Center

    ) {
        Text(text = "Pedidos.")
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Button(
            onClick = {
                contexto.startActivity(Intent(contexto, TelaPedidoInserir::class.java))
            },
            modifier = Modifier.width(304.dp)
        ) {
            Text(text = "Inserir")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
//                contexto.startActivity(Intent(contexto, TelaClienteMostrar::class.java))
            },
            modifier = Modifier.width(304.dp)
        ) {
            Text(text = "Mostrar")
        }


    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview5() {
    TrabalhoFinalTheme {
        Greeting9("Android")
    }
}