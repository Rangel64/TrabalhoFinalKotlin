package com.example.trabalhofinal.classes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.util.ArrayList
import java.util.Date

@Parcelize
data class Pedido(val id_pedido: String, val data: String, val cpf_cliente: String, val id_produto: Map<String,Int>): Parcelable


