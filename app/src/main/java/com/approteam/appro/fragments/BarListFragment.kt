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
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.bar_list_fragment.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.lang.Exception

class BarListFragment(ctx: Context) : Fragment(), OnMapReadyCallback, LocationListener {

    private var mapFragment: SupportMapFragment? = null
    private var c = ctx
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    private lateinit var bars: List<Appro.ApproBar>
    private lateinit var coords: MutableList<LatLng>
    var mapInitialized = false
    private lateinit var averageLocation: LatLng


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bar_list_fragment, container, false)
        mapFragment = childFragmentManager.findFragmentById(R.id.barMap) as SupportMapFragment?
        val json = arguments?.getString("approJson")
        Log.d("DBG", "JSON IN BARLIST: $json")
        val selectedAppro = Gson().fromJson(json, Appro::class.java)
        bars = selectedAppro.bars!!
        coords = mutableListOf()
        for (item in bars) {
            coords.add(LatLng(item.latitude!!, item.longitude!!))
        }
        mapFragment?.getMapAsync(this)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        barListRV.layoutManager = LinearLayoutManager(c)
        fusedLocationClient = FusedLocationProviderClient(c)
        barListRV.adapter = ApproBarAdapter(bars, c)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d("DBG", "Map ready")
        mMap = googleMap
        mMap.isMyLocationEnabled = false
        val helsinki = LatLng(60.19, 24.94)
        mapInitialized = true
        when (mMap) {
            googleMap -> addMarkers()
        }
        centerMapAfterUpdate()
    }

    private fun addMarkers() {
        Log.d("DBG", "added markers")
        for (coord in coords) {
            Log.d("DBG", coord.latitude.toString())
            Log.d("DBG", coord.longitude.toString())
            mMap.addMarker(
                MarkerOptions().position(
                    LatLng(coord.longitude, coord.latitude)
                ).title("Test")
            )
        }
    }
    private fun centerMapAfterUpdate() {
        val latSum = coords.sumByDouble { it.longitude }
        val lonSum = coords.sumByDouble { it.latitude }
        Log.d("DBG latsum", latSum.toString())
        Log.d("DBG lonsum", lonSum.toString())
        val latAvg = latSum / coords.size.toDouble()
        val lonAvg = lonSum / coords.size.toDouble()
        Log.d("DBG latavg", latAvg.toString())
        Log.d("DBG lonavg", lonAvg.toString())
        averageLocation = LatLng(latAvg, lonAvg)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(averageLocation))
        mMap.moveCamera(CameraUpdateFactory.zoomTo(10.toFloat()))
        Log.d("DBG", "Centered map to average location {$averageLocation}")
    }
}







