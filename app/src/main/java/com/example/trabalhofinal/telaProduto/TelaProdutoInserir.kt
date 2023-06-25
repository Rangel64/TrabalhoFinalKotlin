package com.example.trabalhofinal.telaProduto

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.trabalhofinal.classes.Produto
import com.example.trabalhofinal.telaProduto.ui.theme.TrabalhoFinalTheme
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import com.google.firebase.firestore.FirebaseFirestore

class TelaProdutoInserir : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrabalhoFinalTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting7("Android")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Greeting7(name: String, modifier: Modifier = Modifier) {
    var text_id_produto by remember { mutableStateOf(TextFieldValue("")) }
    var text_descricao by remember { mutableStateOf(TextFieldValue("")) }
    var text_valor by remember { mutableStateOf(TextFieldValue("")) }
    val bitmap = remember{ mutableStateOf<Bitmap?>(null) }
    var imageUri by remember{ mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    var bitmapData: ByteArray? = null
    var carregamento by remember{ mutableStateOf(false)}
    val activity = (LocalContext.current as? Activity)

    if(carregamento) {
        LoadingIndicator()
    }
    else{
        val launcher =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
                imageUri = uri
            }

        imageUri?.let {

            if (Build.VERSION.SDK_INT < 28) {
                bitmap.value = MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, it)
                bitmap.value = ImageDecoder.decodeBitmap(source)
            }
        }

        Row(
            modifier = Modifier
                .padding(top = 48.dp)
                .fillMaxHeight()
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Inserir Produto.")
        }

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            TextField(
                value = text_id_produto,
                onValueChange = { newText ->
                    text_id_produto = newText
                },
                label = { Text(text = "Informe o id do produto") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = text_descricao,
                onValueChange = { newText ->
                    text_descricao = newText
                },
                label = { Text(text = "Informe a descricao") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = text_valor,
                onValueChange = { newText ->
                    text_valor = newText
                },
                label = { Text(text = "Informe o valor R$") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    launcher.launch("image/*")
                }
            ) {
                Text(text = "Pick Image")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (bitmap.value != null) {
                Image(
                    bitmap = bitmap.value!!.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(20.dp)
                        .height(200.dp)
                        .width(360.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                modifier = Modifier.width(152.dp),
                onClick = {
                    if (bitmap.value != null) {
                        carregamento = true
                        val outputStream = ByteArrayOutputStream()
                        bitmap.value!!.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                        bitmapData = outputStream.toByteArray()
                        val imageRef = Firebase.storage.getReference("/images")
                        val uploadTask = imageRef.child(text_id_produto.text).putBytes(bitmapData!!)
                        uploadTask.addOnSuccessListener { taskSnapshot ->
//                            Toast.makeText(context, "Upload Concluido", Toast.LENGTH_SHORT).show()
                            val imageUrl = taskSnapshot.metadata?.reference?.downloadUrl

                            val produtoRef = Firebase.database.getReference("/produtos")
                            val produto = Produto(
                                text_id_produto.text,
                                text_descricao.text,
                                text_valor.text.toDouble(),
                                imageUrl.toString()
                            )
                            produtoRef.child(produto.id_produto).setValue(produto)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Produto inserido", Toast.LENGTH_SHORT)
                                        .show()
                                    text_id_produto = TextFieldValue("")
                                    text_descricao = TextFieldValue("")
                                    text_valor = TextFieldValue("")
                                    bitmap.value = null
                                    carregamento = false
                                    activity?.finish()
                                }
                                .addOnFailureListener { exception ->
                                    Toast.makeText(
                                        context,
                                        "Falha em inserir o produto",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    activity?.finish()
                                    carregamento = false
                                }

                        }.addOnFailureListener { exception ->
                            Toast.makeText(context, "Falha no upload", Toast.LENGTH_SHORT).show()
                            carregamento = false
                        }
                    }
                }
            ) {
                Text(text = "Inserir")
            }
        }
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

