package com.example.gabrielfreitas.nac_1_lockit

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_maps.*
import org.jetbrains.anko.toast



class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var mAuth: FirebaseAuth? = null
    private var isConnected: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        Log.d("MapsActivity: ", "onCreate")

        val imageButton = findViewById<ImageButton>(R.id.btn_login)
        imageButton?.setOnClickListener { updateUI() }

        val logButton = findViewById<Button>(R.id.textView6)
        logButton?.setOnClickListener { updateUI() }

        val sairButton = findViewById<Button>(R.id.btn_sair)
        sairButton?.setOnClickListener { updateExit() }

        isConnected()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btn_sign_out.setOnClickListener {
                toast("Deslogando...")
                isConnected=false
                mAuth?.signOut()
                val nextAct1 = Intent(this, MapsActivity::class.java)
                startActivity(nextAct1)
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMarkerClickListener {
            /*_: Marker? ->*/
            if(isConnected) {
                val intent = Intent(this, ConectaDireto::class.java)
                startActivity(intent)
                true
            }else{
                toast("Por favor, faça seu Login!")
                false
            }
        }
        // Add a marker in Sydney and move the camera
        val fiap = LatLng(-23.573977, -46.623195)
        val markerOptions = MarkerOptions().position(fiap).title("FIAP").icon(BitmapDescriptorFactory.fromResource(R.drawable.pointer))
        mMap.addMarker(markerOptions)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fiap,17f))

    }

    /*override fun onResume() {
        super.onResume()
        isConnected()
        Log.d("MapsActivity: ", "onResume")
    }

    override fun onStart() {
        super.onStart()
        isConnected()
        Log.d("MapsActivity: ", "onStart")
    }

    override fun onStop() {
        super.onStop()
        isConnected()
        Log.d("MapsActivity: ", "onStop")
    }

    override fun onPause() {
        super.onPause()
        Log.d("MapsActivity: ", "onPause")
    }*/

    private fun updateUI() {
        //start next activity
        val intent = Intent(this, FaceRecognition::class.java)
        startActivity(intent)
        /*val intent = Intent(this, LogInScreen::class.java)
        startActivity(intent)*/
    }

    private fun updateExit(){
        moveTaskToBack(true)
        android.os.Process.killProcess(android.os.Process.myPid())
        System.exit(1)
    }

    private fun isConnected(){
        Log.d("MapsActivity: ", "isConnected")

        isConnected = intent.getBooleanExtra("etConnected",false)

        if(isConnected){
            btn_sign_out.visibility = View.VISIBLE
            btn_login.visibility = View.GONE
            textView6.visibility = View.GONE
            lucas.visibility = View.VISIBLE
            btn_sair.visibility = View.GONE
        }else{
            btn_sign_out.visibility = View.GONE
            btn_login.visibility = View.VISIBLE
            textView6.visibility = View.VISIBLE
            lucas.visibility = View.GONE
            btn_sair.visibility = View.VISIBLE
        }
    }

    /*override fun onDestroy() {
        super.onDestroy()
    }*/

}
