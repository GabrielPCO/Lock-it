package com.example.gabrielfreitas.nac_1_lockit

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.zxing.integration.android.IntentIntegrator
import android.widget.Toast

class ScannerQr : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        val scanner = IntentIntegrator(this)
        scanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        scanner.setBeepEnabled(false)
        scanner.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK){
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents == null) {
                    Toast.makeText(this, "Operação Cancelada", Toast.LENGTH_LONG).show()
                    val nextAct1 = Intent(this, seleciona_armario::class.java)
                    startActivity(nextAct1)
                } else {
                    if(result.contents == "SENHA") {
                        Toast.makeText(this, "Abertura Autorizada", Toast.LENGTH_LONG).show()
                        val nextAct1 = Intent(this, abre_armario::class.java)
                        startActivity(nextAct1)
                    }else {
                        Toast.makeText(this, "QRcode Inválido!", Toast.LENGTH_LONG).show()
                        val nextAct1 = Intent(this, seleciona_armario::class.java)
                        startActivity(nextAct1)
                    }
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }
}
