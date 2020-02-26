package com.approteam.appro.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.approteam.appro.LocationListener
import com.approteam.appro.R
import com.approteam.appro.adapters.ApproBarAdapter
import com.approteam.appro.data_models.Appro
import com.github.kittinunf.fuel.Fuel
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.bar_list_fragment.*
import kotlinx.android.synthetic.main.home_fragment.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.lang.Exception

class BarListFragment(ctx: Context) : Fragment(), OnMapReadyCallback, LocationListener {

    private var mapFragment: SupportMapFragment? = null
    private var c = ctx
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    var mapInitialized = false
    var currentLocation = LatLng(60.19, 24.94)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bar_list_fragment, container, false)
        mapFragment = childFragmentManager.findFragmentById(R.id.barMap) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        barListRV.layoutManager = LinearLayoutManager(c)
        fusedLocationClient = FusedLocationProviderClient(c)
        doAsync {
            Fuel.get("http://foxer153.asuscomm.com:3001/appro")
                .response { _, _, result ->
                    val (bytes, error) = result
                    if (bytes != null) {
                        Log.d("DBG", "Bytes received!")
                        Log.d("DBG", String(bytes))
                        val json = bytes.toString(Charsets.UTF_8)
                        val appro = Gson().fromJson(json, Array<Appro.ApproBar>::class.java).toList()
                        uiThread {
                            Log.d("DBG", "UI THREAD")
                            barListRV.adapter = ApproBarAdapter(appro, c)
                        }
                    }
                }
        }

    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isMyLocationEnabled = false
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
}





