package com.example.gabrielfreitas.nac_1_lockit

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.util.Log
//import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
//import android.widget.Button
import kotlinx.android.synthetic.main.activity_conecta_bluetooth.*
import org.jetbrains.anko.toast


class conecta_bluetooth : AppCompatActivity() {
    private var  m_bluetoothAdapter: BluetoothAdapter? = null
    private lateinit var m_pairedDevices: Set<BluetoothDevice>
    private val REQUEST_ENABLE_BLUETOOTH = 1
    //private var btnVoltar: Button? = null

    companion object {
        val EXTRA_ADDRESS: String = "Device_address"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conecta_bluetooth)
        m_bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        //btnVoltar = findViewById<View>(R.id.btn_voltar) as Button
        //btnVoltar!!.setOnClickListener { updateUserInfoAndUI() }
        if(m_bluetoothAdapter == null) {
            toast("Dispositivo não encontrado")
            return
        }
        if(!m_bluetoothAdapter!!.isEnabled){
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent,REQUEST_ENABLE_BLUETOOTH)
        }

        select_device_refresh.setOnClickListener{ pairedDeviceList()}

    }

    private fun pairedDeviceList (){
        m_pairedDevices = m_bluetoothAdapter!!.bondedDevices
        val list : ArrayList<BluetoothDevice> = ArrayList()

        if(!m_pairedDevices.isEmpty()){
            for(device: BluetoothDevice in m_pairedDevices){
                list.add(device)
                Log.i("device",""+device)
            }
        }else{
            toast("Nenhum dispositivo bluetooth encontrado")
        }

        val adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1, list)
        select_device_list.adapter = adapter
        select_device_list.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val device: BluetoothDevice = list[position]
            val address: String = device.address
            val intent = Intent(this,seleciona_armario::class.java)
            val intent2 = Intent(this,abre_armario::class.java)
            intent2.putExtra(EXTRA_ADDRESS,address)
            startActivity(intent)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_ENABLE_BLUETOOTH){
            if(resultCode == Activity.RESULT_OK){
                if(m_bluetoothAdapter!!.isEnabled){
                    toast("Bluetooth Ativado!")
                } else {
                    toast("Bluetooth Desativado!")
                }
            } else if (requestCode == Activity.RESULT_CANCELED){
                toast("Bluetoth Cancelado!")
            }
        }

    }

    /*private fun updateUserInfoAndUI() {
        //start next activity
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }*/
}
