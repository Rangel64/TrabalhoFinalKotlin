package com.example.trabalhofinal.TelaPedido
import android.app.Activity
import android.app.Instrumentation.ActivityResult
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.trabalhofinal.TelaPedido.ui.theme.TrabalhoFinalTheme
import com.example.trabalhofinal.classes.Produto

@Suppress("UNCHECKED_CAST", "DEPRECATION")
class TelaPedidoEscolherProduto : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle: Bundle? = intent?.extras
        val contexto = LocalContext
        bundle?.apply {
            val produto: ArrayList<Produto> = getSerializable("lista_produto") as ArrayList<Produto>
            val produtoAdicionados: ArrayList<Produto> = getSerializable("lista_produtos_adicionados") as ArrayList<Produto>
            setContent {
                TrabalhoFinalTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Greeting11("Android", produtos = produto, produtosAdicionados = produtoAdicionados){
                                selectedProdutos ->
                            returnSelectedProdutos(selectedProdutos)
                        }
                    }
                }
            }
        }
    }
    private fun returnSelectedProdutos(selectedProdutos: List<Produto>) {
        val resultIntent = Intent().apply {
            putExtra("lista_produtos_adicionados", ArrayList(selectedProdutos))
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
}

@Composable
fun Greeting11(
    name: String,
    modifier: Modifier = Modifier,
    produtos: ArrayList<Produto>,
    produtosAdicionados: ArrayList<Produto>,
    onProdutosSelected: (List<Produto>) -> Unit
) {
    val checkedProdutos = remember { mutableStateListOf<Produto>() }
    var flag = true

    if(flag){
        produtosAdicionados.forEach {item->
            checkedProdutos.add(item)
        }
        flag = false
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Escolher produtos.",)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        onProdutosSelected(checkedProdutos)
                    }
                ) {
                    Text(text = "Adicionar Produtos.")
                }
            }
        }
        LazyColumn(
            verticalArrangement = Arrangement.Center
        ) {
            items(produtos) { produto ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    CardProduto(produto = produto, checkedProdutos)
                }
            }
        }
    }
}

@Composable
fun CardProduto(produto: Produto, checkedProdutos: SnapshotStateList<Produto>) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ){
        Row(modifier = Modifier.fillMaxWidth()){
            Column(
                modifier = Modifier.padding(vertical = 32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Checkbox(
                    checked = (checkedProdutos.contains(produto)),
                    onCheckedChange = { checked ->
                        if (checked) {
                            checkedProdutos.add(produto)
                        } else {
                            checkedProdutos.remove(produto)
                        }
                    }
                )
            }
            Column(
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Text(text = "id: "+produto.id_produto, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text =  "Decricao: "+produto.descricao)
                Text(text =  "Valor: "+produto.valor)
            }
        }
    }
}

