package com.approteam.appro.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.approteam.appro.LocationListener
import com.approteam.appro.R
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.map_fragment.*

class MapFragment(ctx: Context) : Fragment(), OnMapReadyCallback, LocationListener {

    private var mapFragment: SupportMapFragment? = null
    private var c = ctx
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    var mapInitialized = false
    var currentLocation = LatLng(60.19,24.94)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.map_fragment, container, false)
        // Floating button for centering to user location
        val centerBtn = view.findViewById<FloatingActionButton>(R.id.btnCenter)
        // Disable center button until first location is updated
        centerBtn.isEnabled = false
        centerBtn.setOnClickListener {
            // Center map to current location and zoom it
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
            mMap.moveCamera(CameraUpdateFactory.zoomTo(15.toFloat()))
            Toast.makeText(this.context, "Centered to your location", Toast.LENGTH_SHORT)
                .show()
            Log.d("DBG", "Map centered")
        }

        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fusedLocationClient = FusedLocationProviderClient(c)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val zoomLevel: Float = 10.toFloat()
        // Zoom in map a bit
        mMap.moveCamera(CameraUpdateFactory.zoomTo(zoomLevel))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
        /*val helsinki = LatLng(60.19,24.94)
        mMap.addMarker(MarkerOptions().position(helsinki).title("Helsinki"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(helsinki))*/
        mapInitialized = true
    }

    override fun onLocationResults(lat: Double, lon: Double) {
        // Wait for map to initialize before updating locations
        if (mapInitialized) {
            currentLocation = LatLng(lat, lon)
            btnCenter.isEnabled = true
            mMap.addMarker(MarkerOptions().position(currentLocation).title("My Location"))
            Log.d("DBG", "MAP FRAGMENT RECEIVED LOCATION")
            Log.d("DBG", "$lat")
            Log.d("DBG", "$lon")
        }

    }

}




