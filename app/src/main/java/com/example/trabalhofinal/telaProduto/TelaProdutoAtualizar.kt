package com.example.trabalhofinal.telaProduto

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.trabalhofinal.classes.Cliente
import com.example.trabalhofinal.classes.Produto
import com.example.trabalhofinal.classes.ProdutoMostrar
import com.example.trabalhofinal.telaProduto.ui.theme.TrabalhoFinalTheme
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream


class TelaProdutoAtualizar : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle: Bundle? = intent?.extras
        bundle?.apply {
            val produto: ProdutoMostrar? = getParcelable("produto")
            setContent {
                TrabalhoFinalTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Greeting8("Android",produto = produto!!)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Greeting8(name: String, modifier: Modifier = Modifier, produto: ProdutoMostrar) {
    val referencia = Firebase.database.getReference("/produtos")
    val referenciaStorage = Firebase.storage.getReference("/images")
    val context = LocalContext.current
    var id_produto by remember { mutableStateOf(TextFieldValue(produto.id_produto))}
    var descricao by remember { mutableStateOf(TextFieldValue(produto.descricao))}
    var valor by remember { mutableStateOf(TextFieldValue(produto.valor.toString()))}
    val foto = remember { mutableStateOf<Bitmap?>(null)}
    var imageUri by remember{ mutableStateOf<Uri?>(null) }
    var carregamento by remember{ mutableStateOf(true)}
    var fotoData: ByteArray? = null
    val activity = (LocalContext.current as? Activity)
    var flag by remember{ mutableStateOf(true)}

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            imageUri = uri
        }


    imageUri?.let {

        if (Build.VERSION.SDK_INT < 28) {
            foto.value = MediaStore.Images.Media.getBitmap(context.contentResolver, it)
        } else {
            val source = ImageDecoder.createSource(context.contentResolver, it)
            foto.value = ImageDecoder.decodeBitmap(source)
        }
    }

    if(carregamento) {
        LoadingIndicator()

        if (flag == true){
            Firebase.storage.getReference("/images").child(produto.id_produto)
                .getBytes(Long.MAX_VALUE)
                .addOnSuccessListener { bytes ->
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    produto.foto = bitmap
                    foto.value = bitmap
                    Log.i("foto", produto.foto.toString())
                    carregamento = false
                    flag = false
                }
                .addOnFailureListener { exception ->
                    // Trate qualquer erro de download da imagem
                    Log.e("Erro de download", exception.message ?: "")
                }
        }
    }
    else{
        Row(
            modifier = Modifier
                .padding(top = 56.dp)
                .fillMaxHeight()
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ){
            Text(text = "Atualizar Produto.")
        }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(text = "id: "+produto.id_produto, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = descricao,
                    onValueChange = { newText ->
                        descricao = newText
                    },
                    label = { Text(text = "Informe a descricao") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = valor,
                    onValueChange = { newText ->
                        valor = newText
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

                if (foto.value != null) {
                    Image(
                        bitmap = foto.value!!.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(20.dp)
                            .height(200.dp)
                            .width(360.dp)
                    )
                    val outputStream = ByteArrayOutputStream()
                    foto.value!!.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    fotoData = outputStream.toByteArray()
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    modifier = Modifier.width(152.dp),
                    onClick = {
                        if (foto.value != null) {
                            carregamento = true
                            val imageRef = Firebase.storage.getReference("/images")
                            val uploadTask = imageRef.child(id_produto.text).putBytes(fotoData!!)
                            uploadTask.addOnSuccessListener { taskSnapshot ->
//                                Toast.makeText(context, "Upload Concluido", Toast.LENGTH_SHORT).show()
                                val imageUrl = taskSnapshot.metadata?.reference?.downloadUrl

                                val produtoRef = Firebase.database.getReference("/produtos")
                                val produtoSalvar = Produto(
                                    produto.id_produto,
                                    descricao.text,
                                    valor.text.toDouble(),
                                    imageUrl.toString()
                                )
                                produtoRef.child(produtoSalvar.id_produto).setValue(produtoSalvar)
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Produto atualizado", Toast.LENGTH_SHORT)
                                            .show()
                                        carregamento = false
                                        activity?.finish()
                                    }
                                    .addOnFailureListener { exception ->
                                        Toast.makeText(
                                            context,
                                            "Falha em inserir o produto",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        carregamento = false
                                    }
                            }.addOnFailureListener { exception ->
                                Toast.makeText(context, "Falha no upload", Toast.LENGTH_SHORT).show()
                                carregamento = false
                            }
                        }
                    }
                ) {
                    Text(text = "Atualizar")
                }
            }
        }
    }
}

