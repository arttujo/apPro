package com.approteam.appro

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.approteam.appro.fragments.*
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*


interface LocationListener {
    fun onLocationResults(lat: Double, lon: Double) {
        Log.d("DBG", "Location received")
    }
}

const val PREF_APPRO = "PREF_APPRO"
const val DEF_APPRO_VALUE = "NULL"


class MainActivity : AppCompatActivity() {

    var activityCallback: LocationListener? = null

    //Fragments
    private val mapFragment = MapFragment(this)
    private val scanFragment = ScanFragment(this)
    private val homeFragment = HomeFragment(this)
    private val stampsFragment = StampsFragment(this)

    //Location
    private val LOCATION_REQUEST_CODE = 101
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private var requestingLocationUpdates = false
    private var locationRequest = LocationRequest.create()?.apply {
        interval = 5 * 500
        fastestInterval = 10 * 1000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        // smallestDisplacement = 3f
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        permissionRequest()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        supportFragmentManager.beginTransaction().add(R.id.container, homeFragment).commit()
        bottom_navigation.selectedItemId = R.id.navigation_home
        activityCallback = mapFragment
        onLocationResults(this)
        bottomNavListener()

    }

    //creates a listener for the bottom navigation buttons
    private fun bottomNavListener() {
        bottom_navigation.setOnNavigationItemSelectedListener { menuitem ->
            when (menuitem.itemId) {
                R.id.navigation_home -> {
                    Log.d("DBG", "Home clicked")
                    supportFragmentManager.beginTransaction().replace(R.id.container, homeFragment)
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_map -> {
                    Log.d("DBG", "Map clicked")
                    supportFragmentManager.beginTransaction().replace(R.id.container, mapFragment)
                        .commit()

                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_scan -> {
                    Log.d("DBG", "Scan Clicked")
                    supportFragmentManager.beginTransaction().replace(R.id.container, scanFragment)
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.naigation_stamps -> {
                    Log.d("DBG", "Stamps Selected")
                    supportFragmentManager.beginTransaction().replace(R.id.container,stampsFragment).commit()
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }
    }



    //Handles Location results.
    private fun onLocationResults(ctx: Context) {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    val lat = location.latitude
                    val lon = location.longitude
                    activityCallback!!.onLocationResults(lat, lon)
                    Log.d("DBG", "Lat : $lat, Lon: $lon")
                }
            }
        }
    }

    //Stops location updates when called
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        Log.d("DBG", "Stopping Location updates")
        requestingLocationUpdates = false
    }

    //Stats location updates when called
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
        Log.d("DBG", "Starting Location Updates")
        requestingLocationUpdates = true
    }


    override fun onResume() {
        super.onResume()
        Log.d("DBG", "Resuming...")
        if (!requestingLocationUpdates) startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        Log.d("DBG", "Paused")
        stopLocationUpdates()
    }

    //Request background and fine location perms
    private fun permissionRequest() {
        val permissionAccessFineLocationApproved = ActivityCompat
            .checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
        if (Build.VERSION.SDK_INT >= 24 && permissionAccessFineLocationApproved) {
            val backgroundLocationPermissionApproved = ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED
            if (backgroundLocationPermissionApproved) {
                // App can access location both in the foreground and in the background.
                // Start your service that doesn't have a foreground service type
                // defined.
            } else {
                // App can only access location in the foreground. Display a dialog
                // warning the user that your app must have all-the-time access to
                // location in order to function properly. Then, request background
                // location.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), LOCATION_REQUEST_CODE

                )
            }
        } else {
            // App doesn't have access to the device's location at all. Make full request
            // for permission.
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ), LOCATION_REQUEST_CODE
            )
        }
    }
}


