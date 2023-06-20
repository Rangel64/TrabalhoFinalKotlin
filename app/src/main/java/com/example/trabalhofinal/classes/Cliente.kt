package com.example.trabalhofinal.classes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Cliente(var cpf:String, var nome:String, var telefone:String, var endereco:String, var instagram: String):Parcelable
