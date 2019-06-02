package com.example.gabrielfreitas.nac_1_lockit

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import java.io.IOException
import kotlin.random.Random

class GeraPassword: AppCompatActivity(){

    private var passWord: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        passWord = generateRandomPassword()
        Log.d("GeraPassword: !!!!!!!! ",passWord)
        sendCommand("0")
        val intent = Intent(this, ScannerQr::class.java)
        intent.putExtra("password",passWord)
        startActivity(intent)
    }

    private fun sendCommand(input: String) {
        if (ConectaDireto.m_bluetoothSocket != null) {
            try {
                ConectaDireto.m_bluetoothSocket!!.outputStream.write(input.toByteArray())
                ConectaDireto.m_bluetoothSocket!!.outputStream.write(passWord!!.toByteArray())
                Log.d("CONECTA DIRETO","ABRILLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun generateRandomPassword(): String{
        val chars = "123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        var passWord = ""
        for (i in 0..10) {
            passWord += chars[Random.nextInt(0, chars.length)]
        }
        return passWord
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

