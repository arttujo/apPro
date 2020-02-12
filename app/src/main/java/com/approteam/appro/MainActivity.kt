package com.approteam.appro

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


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
