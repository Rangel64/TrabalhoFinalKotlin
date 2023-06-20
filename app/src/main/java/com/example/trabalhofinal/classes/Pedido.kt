package com.example.trabalhofinal.classes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.ArrayList
import java.util.Date

@Parcelize
data class Pedido(val id_pedido: String, val data: Date, val cpf_cliente: String, val id_produto: ArrayList<String>): Parcelable


