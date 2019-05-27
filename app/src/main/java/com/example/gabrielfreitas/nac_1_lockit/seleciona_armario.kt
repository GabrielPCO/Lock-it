package com.example.gabrielfreitas.nac_1_lockit

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_seleciona_armario.*
import org.jetbrains.anko.toast

class seleciona_armario : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seleciona_armario)
        armario1.setOnClickListener{
            val nextAct1 =  Intent(this,ScannerQr::class.java)
            startActivity(nextAct1)
        }
        armario2.setOnClickListener{
            toast("Armario Ocupado")
        }
        armario3.setOnClickListener{
            val nextAct1 =  Intent(this,ScannerQr::class.java)
            startActivity(nextAct1)
        }
    }
}
