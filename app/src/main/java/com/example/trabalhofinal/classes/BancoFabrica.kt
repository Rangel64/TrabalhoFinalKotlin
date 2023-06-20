package com.example.trabalhofinal.classes

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BancoFabrica(context: Context): SQLiteOpenHelper(context, NomeDoBanco,null, versaoDoBanco){

    companion object {
        const val versaoDoBanco = 1
        const val NomeDoBanco = "fabricaDePerfume.db"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val cliente = " CREATE TABLE cliente(" +
                "cpf TEXT PRIMARY KEY," +
                "nome TEXT," +
                "telefone TEXT," +
                "endereco TEXT" +
                "instagram TEXT);"

        val produto = "CREATE TABLE produto(" +
                "id_produto TEXT PRIMARY KEY," +
                "descricao TEXT," +
                "valor FLOAT," +
                "foto TEXT);"

        val pedido = "CREATE TABLE pedido(" +
                "id_pedido TEXT," +
                "data DATE," +
                "cpf_cliente TEXT);"

        val item_pedido = "CREATE TABLE item_pedido(" +
                "id_item_pedido TEXT PRIMARY KEY," +
                "id_pedido TEXT," +
                "id_produto TEXT" +
                "quantidade INTEGER);"

        val relacionamentos = "ALDER TABLE pedido ADD FOREIGN KEY (cpf_cliente) REFERENCES cliente (cpf); " +
                "ALDER TABLE item item_pedido ADD FOREIGN KEY (id_pedido) REFERENCES pedido (id_pedido); " +
                "ALDER TABLE item item_pedido ADD FOREIGN KEY (id_produto) REFERENCES produto (id_produto);"

        val SQL_criacao = cliente + produto + pedido + item_pedido + relacionamentos

        if (db != null) {
            db.execSQL(SQL_criacao)
        }

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

}