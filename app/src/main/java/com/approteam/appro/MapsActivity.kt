package com.approteam.appro

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.markus.labw2_d3_map.MainActivity.Companion.latitude
import com.markus.labw2_d3_map.MainActivity.Companion.longtitude
import kotlinx.android.synthetic.main.activity_maps.*
import kotlin.math.roundToLong


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val mapLocation = LatLng(latitude, longtitude)

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Snackbar
        val snackB = Snackbar.make(
            findViewById(R.id.mapActivity),
            "Centered map to your location",
            Snackbar.LENGTH_LONG
        )
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        // Add back navigate arrow
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        btnCenter.setOnClickListener {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapLocation, 10.toFloat()))
            snackB.show()
        }


    }

    // Add navigation back to main page
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addMarker(latLng: LatLng) {
        mMap.addMarker(MarkerOptions().position(latLng).title("$latitude $longtitude"))
    }

    fun haversineDistance(point1: LatLng, point2: LatLng): Double {
        val r = 6371 // Radius of earth in KM
        val rlat1 = point1.latitude * (Math.PI / 180)
        val rlat2 = point2.latitude * (Math.PI / 180)
        val difflat = rlat1 - rlat2
        val difflon = (point1.longitude - point2.longitude) * (Math.PI/180)
        val d = 2 * (r * Math.asin(
            Math.sqrt(
                Math.sin(difflat / 2) * Math.sin(difflat / 2) + Math.cos(rlat1) * Math.cos(rlat2) * Math.sin(
                    difflon / 2
                ) * Math.sin(difflon / 2))
            )
        )
        return d
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
        // Add a marker in Sydney and move the camera
        mMap.addMarker(MarkerOptions().position(mapLocation).title("$latitude $longtitude"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapLocation, 10.toFloat()))
        mMap.setOnMapClickListener {
            val newMarker = it
            addMarker(newMarker)
            mMap.addPolyline(PolylineOptions().add(mapLocation, newMarker))
            val distance = haversineDistance(mapLocation, newMarker)
            val distSnack = Snackbar.make(
                findViewById(R.id.mapActivity),
                "Distance between markers is: $distance kilometers",
                Snackbar.LENGTH_LONG
            )
            distSnack.show()


        }
    }
}



