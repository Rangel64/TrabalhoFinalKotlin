@file:Suppress("DEPRECATION")

package com.example.trabalhofinal.TelaPedido

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.trabalhofinal.TelaPedido.ui.theme.TrabalhoFinalTheme
import com.example.trabalhofinal.classes.Cliente
import com.example.trabalhofinal.classes.Pedido
import com.example.trabalhofinal.classes.Produto
import com.example.trabalhofinal.telaCliente.LoadingIndicator
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDate

class TelaPedidoInserir : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        var produtosAdicionados = SnapshotStateList<Produto>()
        val register =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    result.data?.let {
                        if (it.hasExtra("lista_produtos_adicionados")) {
                            val listaRetorno =
                                it.getSerializableExtra("lista_produtos_adicionados") as? ArrayList<Produto>
                            Log.i("Teste", "Teste1")
                            if (listaRetorno != null) {
                                produtosAdicionados.clear()
                                listaRetorno.forEach {item->
                                    produtosAdicionados.add(item)
                                }

                                Log.i("Teste", produtosAdicionados.toString())
                            }
                        }
                    }
                }
            }
        super.onCreate(savedInstanceState)
        setContent {
            TrabalhoFinalTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    Greeting10("Android", produtosAdicionados = produtosAdicionados,register = register)

                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MutableCollectionMutableState")
@Composable
fun Greeting10(name: String, modifier: Modifier = Modifier, produtosAdicionados: SnapshotStateList<Produto>, register: ActivityResultLauncher<Intent>) {
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
    val produtoSelecionados = ArrayList<Produto>()
    var referenciaPedido = Firebase.database.getReference("/pedidos")
    val activity = (LocalContext.current as? Activity)

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

                Button(
                    onClick = {
                        produtoList.clear()
                        produtoSelecionados.clear()
                        produtoState.forEach { item->
                            produtoList.add(item)
//                            produtosAdicionados.add(item)
                        }
                        produtosAdicionados.forEach {item->
                            produtoSelecionados.add(item)
                        }
                        register.launch(
                            Intent(contexto,TelaPedidoEscolherProduto::class.java).let {
                                it.putExtra("lista_produto",produtoList)
                                it.putExtra("lista_produtos_adicionados",produtoSelecionados)
                            }
                        ) },
                    modifier = Modifier.width(200.dp)
                ) {
                    Text(text = "Escolher produtos")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        listaProdutos.clear()
                        produtosAdicionados.forEach {item->
                            listaProdutos.add(item.id_produto)
                        }
                        val pedido = Pedido(id_pedido.text,LocalDate.now().toString(),selectedText!!.cpf,listaProdutos as List<String>)
                        Log.i("pedido_teste",pedido.toString())
                        referenciaPedido.child(pedido.id_pedido).setValue(pedido)
                            .addOnSuccessListener {
                                Toast.makeText(contexto, "Pedido inserido com sucesso", Toast.LENGTH_SHORT).show()
                                activity?.finish()
                            }
                            .addOnFailureListener{
                                Toast.makeText(contexto, "Falha ao inserir o pedido", Toast.LENGTH_SHORT).show()
                                activity?.finish()
                            }
                    },
                    modifier = Modifier.width(200.dp)
                ) {
                    Text(text = "Inserir Pedido")
                }

            }
        }
    }
}

