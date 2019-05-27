package com.example.gabrielfreitas.nac_1_lockit

import android.app.AlertDialog
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.io.IOException
import java.util.*

class abre_armario : AppCompatActivity() {
    companion object {
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var m_bluetoothSocket: BluetoothSocket? = null
        lateinit var m_progress: ProgressDialog
        lateinit var  m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected: Boolean =  false
        lateinit var  m_address: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_abre_armario)
        m_address = intent.getStringExtra(conecta_bluetooth.EXTRA_ADDRESS)

        ConnectToDevice(this ).execute()

        sendCommand("1")
        disconect()
        //control_led_on.setOnClickListener{sendCommand("1")}
        //control_led_off.setOnClickListener{sendCommand("0")}
        //control_led_disconect.setOnClickListener{disconect()}

        val nextAct1 = Intent(this, MapsActivity::class.java)
        startActivity(nextAct1)

    }

    private fun sendCommand(input: String){
        if(m_bluetoothSocket != null){
            try{
                m_bluetoothSocket!!.outputStream.write(input.toByteArray())
                do{
                            val available = m_bluetoothSocket!!.inputStream.available()
                            val bytes = ByteArray(available)
                            m_bluetoothSocket!!.inputStream.read(bytes, 0, available)
                            val text = String(bytes)
                            chamarAlert()
                }while(text != "OK")
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

    private fun chamarAlert() {

        var alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Abrindo armário...") // O Titulo da notificação
        alertDialog.setMessage("Aguarde um momento") // a mensagem ou alerta
        alertDialog.show()
    }

    private fun disconect(){
        if(m_bluetoothSocket != null){
            try{
                m_bluetoothSocket!!.close()
                m_bluetoothSocket = null
                m_isConnected = false}
            catch (e: IOException){
                e.printStackTrace()
            }
        }
        finish()
    }


    private class ConnectToDevice(c: Context): AsyncTask<Void, Void, String>(){
        private var connectSuccess: Boolean = true
        private val context: Context


        init{
            this.context = c

        }



        override fun onPreExecute() {
            super.onPreExecute()
            m_progress = ProgressDialog.show(context,"Conectando...", "Aguarde um momento")
        }

        override fun doInBackground(vararg p0: Void?): String? {
            try {
                if(m_bluetoothSocket == null|| !m_isConnected){
                    m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(m_address)
                    m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    m_bluetoothSocket!!.connect()
                }

            }catch (e: IOException){
                connectSuccess = false
                e.printStackTrace()
            }
            return null
        }


        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if(!connectSuccess){
                Log.i("data","Não foi possível conectar")

            }else{
                m_isConnected = true
            }
            m_progress.dismiss()
        }

    }
}
