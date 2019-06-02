//NÃO ESTÁ SENDO USADO NA VERSÃO FINAL
package com.example.gabrielfreitas.nac_1_lockit

import android.app.Activity
//import android.app.AlertDialog
//import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import org.jetbrains.anko.toast
//import android.widget.ProgressBar
//import kotlinx.android.synthetic.main.activity_login_screen.*
import java.io.IOException
import java.util.*
import android.content.SharedPreferences



class AntigoConectaDireto : AppCompatActivity() {

    private val REQUEST_ENABLE_BLUETOOTH = 1
    private var prefs: SharedPreferences? = null

    companion object {
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        var m_bluetoothSocket: BluetoothSocket? = null
        //lateinit var m_progress: ProgressDialog
        lateinit var m_bluetoothAdapter: BluetoothAdapter
        var m_isConnected: Boolean = false
        lateinit var m_address: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        /*if(m_bluetoothAdapter == null) {
        toast("Dispositivo não encontrado")
        return
        }*/

        if (!m_bluetoothAdapter.isEnabled) {
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
        }

        m_address = "20:15:07:27:77:94"

        ConnectToDevice(this).execute()

        //Roda esse código só uma vez e manda o password para o arduino
        if (!prefs!!.getBoolean("firstTime", false)) {
            // <---- run your one time code here
            sendCommand("0")
            val available = m_bluetoothSocket!!.inputStream.available()
            val bytes = ByteArray(available)
            m_bluetoothSocket!!.inputStream.read(bytes, 0, available)
            val intent = Intent(this, ScannerQr::class.java)
            val text = String(bytes)
            intent.putExtra("password",text)

            // mark first time has ran.
            val editor = prefs?.edit()
            editor?.putBoolean("firstTime", true)
            editor?.apply()
            Log.d("CONECTA DIRETO","EXECUTOSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS")
        }

        sendCommand("1")
        //control_led_on.setOnClickListener{sendCommand("1")}
        //control_led_off.setOnClickListener{sendCommand("0")}
        //control_led_disconect.setOnClickListener{disconect()}
        val nextAct1 = Intent(this, ArmarioAberto::class.java)
        startActivity(nextAct1)
    }

/*private fun chamarAlert() {

    val alertDialog = AlertDialog.Builder(this)
    alertDialog.setTitle("Abrindo armário...") // O Titulo da notificação
    alertDialog.setMessage("Aguarde um momento") // a mensagem ou alerta
    alertDialog.show()
}*/


    private class ConnectToDevice(c: Context) : AsyncTask<Void, Void, String>() {
        private var connectSuccess: Boolean = true
        private val context: Context


        init {
            this.context = c

        }


        /*override fun onPreExecute() {
            super.onPreExecute()
            //progress.visibility = ProgressBar.VISIBLE
            //m_progress = ProgressDialog.show(context,"Conectando...", "Aguarde um momento")
        }*/

        override fun doInBackground(vararg p0: Void?): String? {
            try {
                if (m_bluetoothSocket == null || !m_isConnected) {
                    m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                    val device: BluetoothDevice = m_bluetoothAdapter.getRemoteDevice(m_address)
                    m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery()
                    m_bluetoothSocket!!.connect()
                    Log.d("CONECTA DIRETO","LEU O CÓDIGOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO")
                }

            } catch (e: IOException) {
                connectSuccess = false
                Log.d("CONECTA DIRETO","NÃO LEU O CÓDIGOXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")
                e.printStackTrace()
            }
            return null
        }


        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (!connectSuccess) {
                Log.i("data", "Não foi possível conectar")

            } else {
                m_isConnected = true
            }
            //progress.visibility = ProgressBar.INVISIBLE
            //m_progress.dismiss()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {
                if (m_bluetoothAdapter.isEnabled) {
                    toast("Bluetooth Ativado!")
                } else {
                    toast("Bluetooth Desativado!")
                }
            } else if (requestCode == Activity.RESULT_CANCELED) {
                toast("Bluetoth Cancelado!")
            }
        }

    }

    private fun sendCommand(input: String) {
        if (m_bluetoothSocket != null) {
            try {
                m_bluetoothSocket!!.outputStream.write(input.toByteArray())
                Log.d("CONECTA DIRETO","ABRILLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL")
                /*do{
                    val available = m_bluetoothSocket!!.inputStream.available()
                    val bytes = ByteArray(available)
                    m_bluetoothSocket!!.inputStream.read(bytes, 0, available)
                    val text = String(bytes)
                    chamarAlert()
                }while(text != "OK")*/
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun disconect() {
        if (m_bluetoothSocket != null) {
            try {
                m_bluetoothSocket!!.close()
                m_bluetoothSocket = null
                m_isConnected = false
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
