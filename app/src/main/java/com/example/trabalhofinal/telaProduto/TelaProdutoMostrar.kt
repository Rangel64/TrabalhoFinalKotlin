@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.trabalhofinal.telaProduto

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.trabalhofinal.classes.Cliente
import com.example.trabalhofinal.classes.Produto
import com.example.trabalhofinal.telaCliente.CardCliente
import com.example.trabalhofinal.telaCliente.TelaClienteAtualizar
import com.example.trabalhofinal.telaProduto.ui.theme.TrabalhoFinalTheme
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.withContext
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.ui.graphics.asImageBitmap
import com.example.trabalhofinal.classes.ProdutoMostrar

class TelaProdutoMostrar : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrabalhoFinalTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting6("Android")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Greeting6(name: String, modifier: Modifier = Modifier) {
    Log.i("Produto Mostrar: ", "teste")
    lateinit var referencia: DatabaseReference
    referencia = Firebase.database.getReference("/produtos")
    val referenciaStorage =  Firebase.storage.getReference("/images")
    val produtoState = remember { mutableStateListOf<ProdutoMostrar>() }
    val listState = rememberLazyListState()
    var carregamento by remember{ mutableStateOf(true)}

    if(carregamento){
        LoadingIndicator()
        LaunchedEffect(Unit) {
            val snapshot = withContext(Dispatchers.IO) {
                referencia.get().await() // Obtém uma única vez os dados do banco de dados
            }

            if (snapshot.exists()) {
                for (i in snapshot.children) {
                    val gson = Gson()
                    val json = gson.toJson(i.value)
                    val produto = gson.fromJson(json, Produto::class.java)
                    referenciaStorage.child(produto.id_produto).getBytes(Long.MAX_VALUE)
                        .addOnSuccessListener { bytes ->
                            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                            produtoState.add(
                                ProdutoMostrar(
                                    produto.id_produto,
                                    produto.descricao,
                                    produto.valor,
                                    bitmap
                                )
                            )
                            carregamento = false
                        }
                        .addOnFailureListener { exception ->
                            // Trate qualquer erro de download da imagem
                            Log.e("Erro de download", exception.message ?: "")
                        }
                }
                Log.i("listaBanco", produtoState.toString())
            }
        }
    }else{
        Scaffold(
            content = {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 16.dp,vertical = 8.dp)
                ) {

                    items(produtoState) { produto ->
                        Log.i("Produto Mostrar: ", produto.toString())
                        CardProduto(produto)
                    }
                }
            }
        )
    }
}

@Composable
fun CardProduto(produto: ProdutoMostrar) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ){
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
           Column(
               horizontalAlignment = Alignment.CenterHorizontally,
               modifier = Modifier.fillMaxWidth()
           ) {
               Image(
                   bitmap = produto.foto!!.asImageBitmap(),
                   contentDescription = null,
                   modifier = Modifier
                       .padding(horizontal = 30.dp, vertical = 30.dp)
                       .height(200.dp)
                       .width(200.dp)
               )
           }
        }
        Row(modifier = Modifier.fillMaxWidth()){
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "id: "+produto.id_produto, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text =  "Decricao: "+produto.descricao)
                Text(text =  "Valor: "+produto.valor)
            }

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                MenuTresPontos(produto)
            }
        }
    }
}

@Composable
fun ConfimationScreen(produto: ProdutoMostrar,showDialog: MutableState<Boolean>) {
    val referencia = Firebase.database.getReference("/produtos")
    val referenciaStorage = Firebase.storage.getReference("/images")
    val activity = (LocalContext.current as? Activity)
    AlertDialog(
        onDismissRequest = { },
        title = { Text("Confirmação") },
        text = { Text("Você deseja confirmar esta ação?") },
        confirmButton = {
            Button(
                onClick = {
                    referencia.child(produto.id_produto).removeValue()
                    referenciaStorage.child(produto.id_produto).delete()
                    activity?.finish()
                }
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    showDialog.value = false
                    return@Button
                }
            ) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun MenuTresPontos(produto: ProdutoMostrar){
    val contexto = LocalContext.current
    var isOpened by remember{ mutableStateOf(false) }
    val activity = (LocalContext.current as? Activity)
    val showDialog = remember{ mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.TopEnd)
    ){
        IconButton(onClick = { isOpened = !isOpened}) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More vert"
            )
        }
        DropdownMenu(
            expanded = isOpened,
            onDismissRequest = {isOpened = false}
        ) {
            DropdownMenuItem(
                text = {Text("Editar")},
                onClick = {
                    produto.foto = null
                    val bundle = Bundle().apply {
                        putParcelable("produto",produto)
                    }
                    contexto.startActivity(Intent(contexto, TelaProdutoAtualizar::class.java).putExtras(bundle))
                    activity?.finish()
                    isOpened = !isOpened
                }
            )

            DropdownMenuItem(
                onClick = {
                    showDialog.value = true
                    isOpened = !isOpened
                },
                text = {Text("Excluir")}
            )

        }
    }

    if(showDialog.value){
        ConfimationScreen(produto,showDialog)
    }
}

fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
    return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
}

