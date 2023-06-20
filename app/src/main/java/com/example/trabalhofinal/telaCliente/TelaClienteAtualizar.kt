package com.example.trabalhofinal.telaCliente

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.trabalhofinal.classes.Cliente
import com.example.trabalhofinal.telaCliente.ui.theme.TrabalhoFinalTheme
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class TelaClienteAtualizar : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle: Bundle? = intent?.extras
        bundle?.apply {
            val cliente: Cliente? = getParcelable("cliente")
            setContent {
                TrabalhoFinalTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Greeting4(name = "Android", cliente = cliente)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Greeting4(name: String, modifier: Modifier = Modifier, cliente: Cliente?) {
    val referencia = Firebase.database.getReference("/cliente")
    var text_cpf by remember { mutableStateOf(TextFieldValue(cliente!!.cpf)) }
    var text_nome by remember { mutableStateOf(TextFieldValue(cliente!!.nome)) }
    var text_telefone by remember { mutableStateOf(TextFieldValue(cliente!!.telefone)) }
    var text_endereco by remember { mutableStateOf(TextFieldValue(cliente!!.endereco)) }
    var text_instagram by remember { mutableStateOf(TextFieldValue(cliente!!.instagram)) }
    val context = LocalContext.current
    val activity = (LocalContext.current as? Activity)
    var carregamento by remember { mutableStateOf(false) }

    if (carregamento) {
        LoadingIndicator()
    }
    else {
        Row(
            modifier = Modifier
                .padding(top = 56.dp)
                .fillMaxHeight()
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Atualizar cliente.")
        }

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = text_cpf,
                onValueChange = { newText ->
                    text_cpf = newText
                },
                label = { Text(text = "Informe o CPF") }

            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = text_nome,
                onValueChange = { newText ->
                    text_nome = newText
                },
                label = { Text(text = "Informe o nome") }

            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = text_telefone,
                onValueChange = { newText ->
                    text_telefone = newText
                },
                label = { Text(text = "Informe o telefone") }

            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = text_endereco,
                onValueChange = { newText ->
                    text_endereco = newText
                },
                label = { Text(text = "Informe o endereco") }

            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = text_instagram,
                onValueChange = { newText ->
                    text_instagram = newText
                },
                label = { Text(text = "Informe o instagram") }
            )
            Spacer(modifier = Modifier.height(40.dp))
            Button(
                modifier = Modifier.width(152.dp),
                onClick = {
                    carregamento = true
                    cliente!!.cpf = text_cpf.text
                    cliente!!.nome = text_nome.text
                    cliente!!.telefone = text_telefone.text
                    cliente!!.endereco = text_endereco.text
                    cliente!!.instagram = text_instagram.text
                    Log.i("cliente", cliente!!.toString())
                    text_cpf = TextFieldValue("")
                    text_nome = TextFieldValue("")
                    text_telefone = TextFieldValue("")
                    text_endereco = TextFieldValue("")
                    text_instagram = TextFieldValue("")
                    referencia.child(cliente.cpf).setValue(cliente)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Cliente atualizado", Toast.LENGTH_SHORT).show()
                            activity?.finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Falha ao atualizar", Toast.LENGTH_SHORT).show()
                            activity?.finish()
                        }
                }
            ) {
                Text(text = "Atualizar")
            }
        }
    }
}

