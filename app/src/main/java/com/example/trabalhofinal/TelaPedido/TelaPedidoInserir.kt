package com.example.trabalhofinal.TelaPedido


import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.trabalhofinal.TelaPedido.ui.theme.TrabalhoFinalTheme
import com.example.trabalhofinal.classes.Cliente
import com.example.trabalhofinal.classes.Produto
import com.example.trabalhofinal.classes.ProdutoMostrar
import com.example.trabalhofinal.telaCliente.LoadingIndicator
import com.example.trabalhofinal.telaCliente.MenuTresPontos
import com.example.trabalhofinal.telaProduto.TelaProdutoAtualizar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.ArrayList

class TelaPedidoInserir : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrabalhoFinalTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting10("Android")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MutableCollectionMutableState")
@Composable
fun Greeting10(name: String, modifier: Modifier = Modifier) {
    var id_pedido by remember { mutableStateOf(TextFieldValue("")) }
    var data by remember { mutableStateOf(TextFieldValue("")) }
    var id_cliente by remember { mutableStateOf(TextFieldValue("")) }
    var listaProdutos by remember { mutableStateOf<ArrayList<String>>(ArrayList()) }
    var clienteOk by remember { mutableStateOf(false) }
    var carregamento by remember{ mutableStateOf(true)}
    lateinit var referencia: DatabaseReference
    referencia = Firebase.database.getReference("/cliente")
    var referenciaProduto = Firebase.database.getReference("/produtos")
    val clientesState = remember { mutableStateListOf<Cliente>() }
    val listState = rememberLazyListState()
    var expanded by remember{ mutableStateOf(false) }
    var selectedText by remember { mutableStateOf<Cliente?>(null) }
    val contexto = LocalContext.current
    val produtoState = remember { mutableStateListOf<Produto>() }
    val produtoList = ArrayList<Produto>()


    if(carregamento) {
        LoadingIndicator()
        LaunchedEffect(Unit){
            val snapshot = withContext(Dispatchers.IO) {
                referencia.get().await() // Obtém uma única vez os dados do banco de dados
            }

            if (snapshot.exists()) {
                val gson = Gson()
                for (i in snapshot.children) {
                    val json = gson.toJson(i.value)
                    val cliente = gson.fromJson(json, Cliente::class.java)
                    clientesState.add(
                        Cliente(
                            cliente.cpf,
                            cliente.nome,
                            cliente.telefone,
                            cliente.endereco,
                            cliente.instagram
                        )
                    )

                }
                Log.i("listaBanco", clientesState.toString())
            }
        }
        LaunchedEffect(Unit) {
            val snapshot = withContext(Dispatchers.IO) {
                referenciaProduto.get().await() // Obtém uma única vez os dados do banco de dados
            }

            if (snapshot.exists()) {
                for (i in snapshot.children) {
                    val gson = Gson()
                    val json = gson.toJson(i.value)
                    val produto = gson.fromJson(json, Produto::class.java)
                    produtoState.add(
                        Produto(
                            produto.id_produto,
                            produto.descricao,
                            produto.valor,
                            produto.foto
                        )
                    )
                    produtoList.add(
                        Produto(
                            produto.id_produto,
                            produto.descricao,
                            produto.valor,
                            produto.foto
                        )
                    )
                    carregamento = false
                }
                Log.i("listaBanco", produtoState.toString())
            }
        }
    }
    else{
        if(!clienteOk){
            selectedText = clientesState.get(0)
            clienteOk = true
        }
        Row(
            modifier = Modifier
                .padding(top = 48.dp)
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.Center

        ) {
            Spacer(modifier = Modifier.height(40.dp))
            Text(text = "Inserir pedido.")
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Selecione o cliente.")
                Spacer(modifier = Modifier.height(16.dp))
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {
                        expanded = !expanded
                    }
                ) {

                    TextField(
                        value = "id: "+ selectedText!!.cpf+", Nome: " + selectedText!!.nome,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)},
                        modifier = Modifier.menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false}
                    ) {
                        clientesState.forEach { item->
                            DropdownMenuItem(
                                text = {Text(text = "id: "+item.cpf+", Nome: " + item.nome)},
                                onClick = {
                                    selectedText = item
                                    expanded = false
                                    Toast.makeText(contexto, "Cliente selecionado", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                        clientesState.forEach { item->
                            DropdownMenuItem(
                                text = {Text(text = "id: "+item.cpf+", Nome: " + item.nome)},
                                onClick = {
                                    selectedText = item
                                    expanded = false
                                    Toast.makeText(contexto, "Cliente selecionado", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = id_pedido,
                    onValueChange = { newText ->
                        id_pedido = newText
                    },
                    label = { Text(text = "Informe o id do pedido")}
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "Selecione os produtos.")

                Spacer(modifier = Modifier.height(16.dp))



                Button(
                    onClick = {
                        val bundle = Bundle().apply {
                            putParcelableArrayList("lista_produto", produtoList)
                        }
                        contexto.startActivity(Intent(contexto, TelaPedidoEscolherProduto::class.java).putExtras(bundle))},
                    modifier = Modifier.width(200.dp)
                ) {
                    Text(text = "Escloher produtos")
                }

            }
        }
    }
}

