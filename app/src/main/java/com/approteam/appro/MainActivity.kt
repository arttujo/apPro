package com.approteam.appro

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.get
import androidx.core.view.isEmpty
import com.approteam.appro.fragments.*
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.util.*

//Interface for sending location updates to parts where they are needed
interface LocationListener {
    fun onLocationResults(lat: Double, lon: Double) {
        Log.d("DBG", "Location received")
    }
}

interface ApproStatusListener {
    fun onApproUpdate(enabled: Boolean) {
        if (enabled) {
            Log.d("DBG", "appro active")
        } else {
            Log.d("DBG", "appro not active")
        }
    }

}


const val PREF_APPRO = "PREF_APPRO"
const val DEF_APPRO_VALUE = "NULL"
const val PREF_UNIQUE_ID = "PREF_UNIQUE_ID"

class MainActivity : AppCompatActivity(), ApproStatusListener {

    private var uniqueID: String? = null
    private val READ_STORAGE_CODE = 29
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

    private var latitude: Double? = null
    private var longitude: Double? = null
    private val handler: Handler = Handler()
    private var listener: ApproStatusListener? = null

    // Used to send location data to the server
    private val runnable: Runnable = object : Runnable {
        override fun run() { // The method you want to call every now and then.
            Log.d("DBG", "SENDING LOC DATA")
            sendLocationData(applicationContext,latitude!!,longitude!!)
            handler.postDelayed(this, 30*1000)
            sendLocationData(applicationContext, latitude!!, longitude!!)
            handler.postDelayed(this, 30000) // 30000 = 30 seconds. This time is in millis.
            handler.postDelayed(this, 600000) // 30000 = 30 seconds. This time is in millis.
        }
    }

    private fun sendLocationData(ctx: Context, lat: Double, lon: Double) {
        Fuel.post("http://foxer153.asuscomm.com:3001/updateUserLocation")
            .jsonBody("{\"name\":\"${getUUID(ctx)}\", \"lat\":\"${lat}\", \"lon\":\"${lon}\"}")
            .response { result ->
                val (bytes, _) = result
                if (bytes != null) {
                    Log.d("DBG", String(bytes))
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createUUID(this)
        uuidToDB()
        handler.postDelayed(runnable, 2000)
        verifyStoragePermissions(this)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        permissionRequest()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        supportFragmentManager.beginTransaction().add(R.id.container, homeFragment).commit()
        bottom_navigation.selectedItemId = R.id.navigation_home
        activityCallback = mapFragment
        activityCallback = scanFragment
        onLocationResults()
        bottomNavListener()
        listener = this
        val approJson = getCurrentApproData(this)
        if (approJson == "NULL") {
            listener?.onApproUpdate(false)
        } else {
            listener?.onApproUpdate(true)
        }

    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        val approJson = getCurrentApproData(this)
        if (approJson == "NULL") {
            listener?.onApproUpdate(false)
        } else {
            listener?.onApproUpdate(true)
        }
    }

    //creates a listener for the bottom navigation buttons
    private fun bottomNavListener() {
        bottom_navigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    Log.d("DBG", "Home clicked")
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, homeFragment)
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_map -> {
                    Log.d("DBG", "Map clicked")
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, mapFragment)
                        .commit()

                    return@setOnNavigationItemSelectedListener true
                }
                R.id.navigation_scan -> {
                    Log.d("DBG", "Scan Clicked")
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, scanFragment)
                        .commit()
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.navigation_stamps -> {
                    Log.d("DBG", "Stamps Selected")
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, stampsFragment).commit()
                    return@setOnNavigationItemSelectedListener true
                }
            }

            false
        }
    }

    override fun onApproUpdate(enabled: Boolean) {
        val layout: ConstraintLayout = findViewById(R.id.mainLayout)
        val params: ViewGroup.LayoutParams = layout.layoutParams
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val enabledHeight = displayMetrics.heightPixels
        val disabledHeight = displayMetrics.heightPixels + bottom_navigation.height
        Log.d("params, height", params.height.toString())
        Log.d("params, eheight", enabledHeight.toString())
        Log.d("params, dheight", disabledHeight.toString())

        if (enabled) {
            // Appro exists -> SHOW EVERY BUTTON
            bottom_navigation.visibility = View.VISIBLE
            bottom_navigation
                .animate()
                .translationY(0.toFloat())
                .alpha(1.0f)
            params.height = enabledHeight
            Log.d("DBG approupdate", "Enabled")
        } else {
            // No current appro -> HIDE EVERYTHING ELSE EXCEPT HOME SCREEN
            bottom_navigation
                .animate()
                .translationY(bottom_navigation.height.toFloat())
                .alpha(0.0f)
            params.height = disabledHeight

            Log.d("DBG Approupdate", "Disabled")
        }
    }

    //Handles Location results.
    private fun onLocationResults() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    val lat = location.latitude
                    val lon = location.longitude
                    latitude = lat
                    longitude = lon
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

    private val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    //Asks for storage permissions. Used in appro creation
    private fun verifyStoragePermissions(activity: Activity?) { // Check if we have write permission
        val permission = ActivityCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (permission != PackageManager.PERMISSION_GRANTED) { // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                activity,
                PERMISSIONS_STORAGE,
                READ_STORAGE_CODE
            )
        }
    }


    //Request background and fine location perms
    @SuppressLint("InlinedApi", "ObsoleteSdkInt")
    private fun permissionRequest() {
        val permissionAccessFineLocationApproved = ActivityCompat
            .checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
        if (Build.VERSION.SDK_INT >= 24 && permissionAccessFineLocationApproved) {
            val backgroundLocationPermissionApproved = ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED
            if (!backgroundLocationPermissionApproved) {
                // App can only access location in the foreground. Display a dialog
                // warning the user that your app must have all-the-time access to
                // location in order to function properly. Then, request background
                // location.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    LOCATION_REQUEST_CODE

                )
            }
            // App can access location both in the foreground and in the background.
            // Start your service that doesn't have a foreground service type
            // defined.
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

    // Creates an Unique Identifier for the app user
    // Will stay the same until user reinstalls the app
    private fun createUUID(ctx: Context) {
        if (uniqueID == null) {
            val sharedPrefs: SharedPreferences =
                ctx.getSharedPreferences(PREF_UNIQUE_ID, Context.MODE_PRIVATE)
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null)
            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString()
                val editor = sharedPrefs.edit()
                editor.putString(PREF_UNIQUE_ID, uniqueID)
                editor.apply()
            }
        }
    }

    // Returns UUID from shared prefs
    private fun getUUID(ctx: Context): String {
        val sharedPref = ctx.getSharedPreferences(PREF_UNIQUE_ID, Context.MODE_PRIVATE)
        val uuid = sharedPref.getString(PREF_UNIQUE_ID, null)
        return uuid!!
    }

    private fun uuidToDB() {
        Fuel.post("http://foxer153.asuscomm.com:3001/newUser")
            .jsonBody("{\"name\":\"${getUUID(this)}\"}")
            .response { result ->
                val (bytes, _) = result
                if (bytes != null) {
                    val responseJson = JSONObject(String(bytes))
                    val created = responseJson.getBoolean("playerCreated")
                    if (created) {
                        Log.d("DBG", "USER SAVED IN DB")
                    }
                }
            }
    }
    private fun getCurrentApproData(ctx: Context): String {
        val mPrefs = ctx.getSharedPreferences(PREF_APPRO, Context.MODE_PRIVATE)
        val approJsonString = mPrefs.getString(PREF_APPRO, DEF_APPRO_VALUE)
        Log.d("DBG", "GOT APPRO: $approJsonString")
        return approJsonString!!
    }

}


