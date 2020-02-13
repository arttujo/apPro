package com.approteam.appro

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_maps.*
import java.io.IOException
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val bundle = Bundle()

    companion object {
        var latitude = 0.0
        var longtitude = 0.0
        const val REQUEST_CHECK_SETTINGS = 43
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mLocationRequest: LocationRequest
    private val mapLocation = LatLng(latitude, longtitude)
    var locationData: MutableList<LatLng> = ArrayList()
    private lateinit var mMap: GoogleMap



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
            getCurrentLocation()
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mapLocation, 10.toFloat()))
            snackB.show()
        }
        fusedLocationClient = FusedLocationProviderClient(this)

    }
    private fun getCurrentLocation() {
        mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = (10 * 1000).toLong()
        mLocationRequest.fastestInterval = 2000

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest)
        val locationSettingsRequest = builder.build()

        val result = LocationServices.getSettingsClient(this).checkLocationSettings(locationSettingsRequest)
        result.addOnCompleteListener { task ->
            try {
                val response = task.getResult(ApiException::class.java)
                if (response!!.locationSettingsStates.isLocationPresent){
                    getLastLocation()
                }
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        val resolvable = exception as ResolvableApiException
                        resolvable.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
                    } catch (e: IntentSender.SendIntentException) {
                    } catch (e: ClassCastException) {
                    }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> { }
                }
            }
        }
    }
    private fun getLastLocation() {
        fusedLocationClient.lastLocation
            .addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    val mLastLocation = task.result

                    var address = "No known address"

                    /**
                    // Geocoder, disabled to save requests
                     val gcd = Geocoder(this, Locale.getDefault())
                    val addresses: List<Address>
                    try {
                        addresses = gcd.getFromLocation(mLastLocation!!.latitude, mLastLocation.longitude, 1)
                        locationData.add(LatLng(mLastLocation.latitude, mLastLocation.longitude))
                        if (addresses.isNotEmpty()) {
                            address = addresses[0].getAddressLine(0)

                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }*/
                    mMap.addMarker(
                        MarkerOptions()
                            .position(LatLng(mLastLocation!!.latitude, mLastLocation.longitude))
                            .title("Current Location")
                            .snippet(address)

                    )
                    Log.d("DBG", "$mLastLocation.latitude")

                    val cameraPosition = CameraPosition.Builder()
                        .target(LatLng(mLastLocation.latitude, mLastLocation.longitude))
                        .zoom(17f)
                        .build()
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                } else {
                    Toast.makeText(this, "No current location found", Toast.LENGTH_LONG).show()
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> {
                if (resultCode == Activity.RESULT_OK) {
                    getCurrentLocation()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)

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

    // can calculate distance between 2 markers
    private fun haversineDistance(point1: LatLng, point2: LatLng): Double {
        val r = 6371 // Radius of earth in KM
        val rlat1 = point1.latitude * (Math.PI / 180)
        val rlat2 = point2.latitude * (Math.PI / 180)
        val difflat = rlat1 - rlat2
        val difflon = (point1.longitude - point2.longitude) * (Math.PI / 180)
        val d = 2 * (r * Math.asin(
            Math.sqrt(
                Math.sin(difflat / 2) * Math.sin(difflat / 2) + Math.cos(rlat1) * Math.cos(rlat2) * Math.sin(
                    difflon / 2
                ) * Math.sin(difflon / 2)
            )
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
        getCurrentLocation()
    }

    override fun onResume() {
        super.onResume()
        getCurrentLocation()
    }
}




