package com.example.gabrielfreitas.nac_1_lockit

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import kotlinx.android.synthetic.main.activity_seleciona_armario.*
import org.jetbrains.anko.toast

class SelecionaArmario : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seleciona_armario)

        val imageButton = findViewById<ImageButton>(R.id.btn_voltar)
        imageButton?.setOnClickListener { updateUI() }

        armario1.setOnClickListener{
            val nextAct1 =  Intent(this,ScannerQr::class.java)
            startActivity(nextAct1)
        }
        armario2.setOnClickListener{
            toast("Armario Ocupado")
        }
        armario3.setOnClickListener{
            val nextAct2 =  Intent(this,ScannerQr::class.java)
            startActivity(nextAct2)
        }
    }

    private fun updateUI() {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }
}
