package com.example.trabalhofinal.TelaPedido

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.trabalhofinal.TelaPedido.ui.theme.TrabalhoFinalTheme
import com.example.trabalhofinal.classes.Produto

class TelaPedidoEscolherProduto : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle: Bundle? = intent?.extras
        bundle?.apply {
            val produto: ArrayList<Produto?> = getParcelable("lista_produto")!!
            setContent {
                TrabalhoFinalTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Greeting11("Android", produtos = produto)
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting11(name: String, modifier: Modifier = Modifier, produtos: ArrayList<Produto?>) {
    val checkedProdutos = remember { mutableStateListOf<Produto>() }

    Row(
        modifier = Modifier
            .padding(top = 48.dp)
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalArrangement = Arrangement.Center

    ) {
        Spacer(modifier = Modifier.height(40.dp))
        Text(text = "Escolher produto.")
    }

    LazyColumn {
        items(produtos) { produto ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Checkbox(
                    checked = checkedProdutos.contains(produto),
                    onCheckedChange = { checked ->
                        if (checked) {
                            checkedProdutos.add(produto!!)
                        } else {
                            checkedProdutos.remove(produto)
                        }
                    }
                )
                Text(text = ("id: "+produto!!.id_produto+", nome: "+produto.descricao), modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

