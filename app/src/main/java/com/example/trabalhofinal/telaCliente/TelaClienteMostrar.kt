package com.example.trabalhofinal.telaCliente

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import com.example.trabalhofinal.classes.Cliente
import com.example.trabalhofinal.telaCliente.ui.theme.TrabalhoFinalTheme
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class TelaClienteMostrar : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrabalhoFinalTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting3("Android")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Greeting3(name: String, modifier: Modifier = Modifier) {
    lateinit var referencia: DatabaseReference
    referencia = Firebase.database.getReference("/cliente")
    var carregamento by remember{ mutableStateOf(true)}
    val clientesState = remember { mutableStateListOf<Cliente>() }
    val listState = rememberLazyListState()

    if(carregamento){
        LoadingIndicator()
        LaunchedEffect(Unit) {
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
                    carregamento = false
                }
                Log.i("listaBanco", clientesState.toString())
            }
        }
    }
    else{
        Scaffold(
            content = {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 16.dp,vertical = 8.dp)
                ) {

                    items(clientesState) { cliente ->
                        Log.i("Produto Mostrar: ", cliente.toString())
                        CardCliente(cliente)
                    }
                }
            }
        )
    }
}

@Composable
fun CardCliente(cliente: Cliente) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ){
        Row(modifier = Modifier.fillMaxWidth()){
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(text = "Nome: "+cliente.nome, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text =  "CPF: "+cliente.cpf)
                Text(text =  "Telefone: "+cliente.telefone)
                Text(text =  "Instagram: "+cliente.instagram)
                Text(text =  "Endereco: "+cliente.endereco)
            }

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                MenuTresPontos(cliente)
            }
        }
    }
}

@Composable
fun ConfimationScreen(cliente: Cliente,showDialog: MutableState<Boolean>) {
    val referencia = Firebase.database.getReference("/cliente")
    val activity = (LocalContext.current as? Activity)
    AlertDialog(
        onDismissRequest = { },
        title = { Text("Confirmação") },
        text = { Text("Você deseja confirmar esta ação?") },
        confirmButton = {
            Button(
                onClick = {
                    referencia.child(cliente.cpf).removeValue()
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
fun MenuTresPontos(cliente: Cliente){
    val contexto = LocalContext.current
    var isOpened by remember{mutableStateOf(false)}
    val activity = (LocalContext.current as? Activity)
    val showDialog = remember{mutableStateOf(false)}

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
                    val bundle = Bundle().apply {
                        putParcelable("cliente",cliente)
                    }
                    contexto.startActivity(Intent(contexto,TelaClienteAtualizar::class.java).putExtras(bundle))
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
        ConfimationScreen(cliente,showDialog)
    }

}

@Composable
fun LoadingIndicator(){
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .size(40.dp)
                .padding(8.dp)
        )
    }
}