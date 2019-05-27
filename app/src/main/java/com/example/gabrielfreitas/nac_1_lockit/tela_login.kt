package com.example.gabrielfreitas.nac_1_lockit

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_tela_login.*
import org.jetbrains.anko.toast


class tela_login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_login)

        btnLogin.setOnClickListener{
            if(user.text.toString()=="user"&& senha.text.toString()=="user"){
                val nextAct =  Intent(this,MapsActivity::class.java)
                startActivity(nextAct)
            }else{
                toast("Login Incorreto")
            }
        }
        btnRegistrar.setOnClickListener{
                toast("Estamos trabalhando nisso...")
            }
    }


    /* PARA UTILIZAR A TELA DE LOGIN INSERIR nome: user senha: user*/
}
