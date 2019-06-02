package com.example.gabrielfreitas.nac_1_lockit

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.zxing.integration.android.IntentIntegrator
import android.widget.Toast

class ScannerQr : AppCompatActivity(){

    private var passwordQr: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        Log.d("ScannerQr","On Create")
        passwordQr = intent.getStringExtra("password")
        Log.d("ScannerQr: !!!!!!!!!!!!",passwordQr)

        val scanner = IntentIntegrator(this)
        scanner.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        scanner.setBeepEnabled(false)
        scanner.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("ScannerQr","On Activity Result")
        if(resultCode == Activity.RESULT_OK){
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents == null) {
                    Toast.makeText(this, "Operação Cancelada", Toast.LENGTH_LONG).show()
                    val nextAct1 = Intent(this, SelecionaArmario::class.java)
                    startActivity(nextAct1)
                } else {
                    if(result.contents == passwordQr) {
                        Toast.makeText(this, "Abertura Autorizada", Toast.LENGTH_LONG).show()
                        val nextAct2 = Intent(this, ArmarioAberto::class.java)
                        startActivity(nextAct2)
                    }else {
                        Toast.makeText(this, "QRcode Inválido!", Toast.LENGTH_LONG).show()
                        val nextAct3 = Intent(this, SelecionaArmario::class.java)
                        startActivity(nextAct3)
                    }
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    /*override fun onRestart() {
        super.onRestart()
        Log.d("ScannerQr","On Restart")
        val intent = Intent(this, SelecionaArmario::class.java)
        startActivity(intent)
    }*/
}

