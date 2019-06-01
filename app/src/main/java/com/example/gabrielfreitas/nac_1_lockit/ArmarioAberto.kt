package com.example.gabrielfreitas.nac_1_lockit


import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_abre_armario.*
import kotlinx.android.synthetic.main.activity_abre_armario.btn_voltar

class ArmarioAberto : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_abre_armario)

        btn_abrir.setOnClickListener{
            val nextAct1 =  Intent(this,ConectaDireto::class.java)
            startActivity(nextAct1)
        }

        btn_voltar.setOnClickListener{
            val nextAct1 =  Intent(this,SelecionaArmario::class.java)
            startActivity(nextAct1)
        }

    }

}
