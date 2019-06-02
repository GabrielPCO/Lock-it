package com.example.gabrielfreitas.nac_1_lockit

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import java.io.IOException

class DestravaArmario: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sendCommand("1")
        val nextAct1 = Intent(this, ArmarioAberto::class.java)
        startActivity(nextAct1)
    }

    private fun sendCommand(input: String) {
        if (ConectaDireto.m_bluetoothSocket != null) {
            try {
                ConectaDireto.m_bluetoothSocket!!.outputStream.write(input.toByteArray())
                Log.d("CONECTA DIRETO","ABRILLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun disconect() {
        if (ConectaDireto.m_bluetoothSocket != null) {
            try {
                ConectaDireto.m_bluetoothSocket!!.close()
                ConectaDireto.m_bluetoothSocket = null
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        finish()
    }

    public override fun onDestroy() {
        disconect()

        super.onDestroy()
    }

}
