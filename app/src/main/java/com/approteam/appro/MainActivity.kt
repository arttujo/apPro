package com.approteam.appro

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val bundle = Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        // request location permission

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btn = findViewById<Button>(R.id.openMap)
        btn.setOnClickListener {
            Log.d("DBG", "Button pressed")
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

    }
}
