package com.approteam.appro.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.approteam.appro.*
import com.approteam.appro.LocationListener
import com.approteam.appro.R
import com.approteam.appro.data_models.Appro
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import java.lang.Exception

class MapFragment(ctx: Context) : Fragment(), OnMapReadyCallback, LocationListener {

    private var mapFragment: SupportMapFragment? = null
    private var c = ctx
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    private var mapInitialized = false
    private var currentLocation = LatLng(60.19, 24.94)
    private lateinit var coords: MutableList<LatLng>
    private lateinit var bars: List<Appro.ApproBar>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
            val view = inflater.inflate(R.layout.map_fragment, container, false)
            mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            mapFragment?.getMapAsync(this)
            return view
    }

    private fun addMarkers() {
        if (!coords.isNullOrEmpty()) {
            for (bar in bars) {
                Log.d("DBG", bar.latitude.toString())
                Log.d("DBG", bar.longitude.toString())
                mMap.addMarker(
                    MarkerOptions().position(
                        LatLng(bar.latitude!!, bar.longitude!!)
                    ).title(bar.name)
                )
            }
            Log.d("DBG", "added markers")
        } else {
            Log.d("DBG","Coords empty")
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = FusedLocationProviderClient(c)
        coords = mutableListOf()
        if (getCurrentApproData(c) != DEF_APPRO_VALUE){
            val approString = getCurrentApproData(c)
            val appro = Gson().fromJson(approString, Appro::class.java)
            val approBars = appro.bars!!
            bars = approBars
            for (item in approBars){
                coords.add(LatLng(item.latitude!!,item.longitude!!))
            }
        }


    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.setAllGesturesEnabled(true)
        mMap.isMyLocationEnabled = true
        val zoomLevel: Float = 10.toFloat()
        // Zoom in map a bit
        mMap.moveCamera(CameraUpdateFactory.zoomTo(zoomLevel))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
        mapInitialized = true
        when (mMap){
            googleMap -> addMarkers() }
        centerMapAfterUpdate()
    }

    private fun centerMapAfterUpdate() {
        if (!coords.isNullOrEmpty()) {

            val latSum = coords.sumByDouble { it.latitude }
            val lonSum = coords.sumByDouble { it.longitude }
            Log.d("DBG latsum", latSum.toString())
            Log.d("DBG lonsum", lonSum.toString())
            val latAvg = latSum / coords.size.toDouble()
            val lonAvg = lonSum / coords.size.toDouble()
            Log.d("DBG latavg", latAvg.toString())
            Log.d("DBG lonavg", lonAvg.toString())
            val averageLocation = LatLng(latAvg, lonAvg)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(averageLocation, 13.toFloat()))

            Log.d("DBG", "Centered map to average location {$averageLocation}")
        }
    }


    override fun onLocationResults(lat: Double, lon: Double) {
        // Wait for map to initialize before updating locations
        if (mapInitialized) {
            try {
                currentLocation = LatLng(lat, lon)
                //mMap.addMarker(MarkerOptions().position(currentLocation).title("My Location"))
                Log.d("DBG", "MAP FRAGMENT RECEIVED LOCATION")
                Log.d("DBG", "$lat")
                Log.d("DBG", "$lon")
            } catch (e: Exception) {
                Log.d("DBG", e.toString())
                Log.d("DBG", "Caused by navigating off from the view")
            }
        }
    }

    private fun getCurrentApproData(ctx: Context):String{
        val mPrefs = ctx.getSharedPreferences(PREF_APPRO,Context.MODE_PRIVATE)
        val approJsonString = mPrefs.getString(PREF_APPRO, DEF_APPRO_VALUE)
        Log.d("DBG", "GOT APPRO: $approJsonString")
        return approJsonString!!
    }

}




