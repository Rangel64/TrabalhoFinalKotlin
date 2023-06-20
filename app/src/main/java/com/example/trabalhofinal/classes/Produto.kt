package com.example.trabalhofinal.classes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Produto(val id_produto: String, val descricao: String, val valor: Double, val foto: String): Parcelable
