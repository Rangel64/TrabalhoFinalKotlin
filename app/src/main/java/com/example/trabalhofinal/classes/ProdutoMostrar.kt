package com.example.trabalhofinal.classes

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProdutoMostrar(val id_produto: String, val descricao: String, val valor: Double, var foto: Bitmap?) : Parcelable
