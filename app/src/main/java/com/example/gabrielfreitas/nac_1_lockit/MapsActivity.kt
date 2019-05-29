package com.example.gabrielfreitas.nac_1_lockit

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_maps.*
import org.jetbrains.anko.toast

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btn_sign_out.setOnClickListener {
            toast("Deslogando...")
            mAuth?.signOut()
            val nextAct1 =  Intent(this,LogInScreen::class.java)
            startActivity(nextAct1) }
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
        mMap.setOnMarkerClickListener{ _: Marker? ->
            val intent = Intent(this,conecta_bluetooth::class.java)
            startActivity(intent)
            true
        }
        // Add a marker in Sydney and move the camera
        val fiap = LatLng(-23.573977, -46.623195)
        val MarkerOptions = MarkerOptions().position(fiap).title("FIAP").icon(BitmapDescriptorFactory.fromResource(R.drawable.pointer))
        mMap.addMarker(MarkerOptions)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fiap,17f))
    }
}
