package com.example.trabalhofinal

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
import com.example.trabalhofinal.TelaPedido.TelaPedidoPrincipal
import com.example.trabalhofinal.ui.theme.TrabalhoFinalTheme
import com.example.trabalhofinal.telaCliente.TelaClienteInserir
import com.example.trabalhofinal.telaCliente.TelaClientePrincipal
import com.example.trabalhofinal.telaProduto.TelaProdutoPrincipal

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrabalhoFinalTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TelaPrincipal("Android")
                }
            }
        }
    }
}

@Composable
fun TelaPrincipal(name: String, modifier: Modifier = Modifier) {
    val contexto = LocalContext.current
    Row(
        modifier = Modifier
            .padding(top = 48.dp)
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalArrangement = Arrangement.Center

        ) {

            Text(text = "Fabrica de sapatos Pé sujo.")

    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {

        Button(
            onClick = {
                contexto.startActivity(Intent(contexto,TelaClientePrincipal::class.java))
            },
            modifier = Modifier.width(304.dp)
        ) {
            Text(text = "Cliente")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                contexto.startActivity(Intent(contexto, TelaProdutoPrincipal::class.java))
            },
            modifier = Modifier.width(304.dp)
        ) {
            Text(text = "Produto")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                contexto.startActivity(Intent(contexto, TelaPedidoPrincipal::class.java))
            },
            modifier = Modifier.width(304.dp)
        ) {
            Text(text = "Pedido")
        }

    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TrabalhoFinalTheme {
        TelaPrincipal("Android")
    }
}