package com.approteam.appro

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val bundle = Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        // request location permission

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        bottom_navigation.setOnNavigationItemSelectedListener {menuitem ->
            when (menuitem.itemId){
                R.id.navigation_home -> {
                    Log.d("DBG","Home clicked")
                }
                R.id.navigation_map -> {
                    Log.d("DBG", "Map clicked")
                    val intent = Intent(this, MapsActivity::class.java)
                    startActivity(intent)
                }
                R.id.navigation_scan -> {
                    Log.d("DBG", "Scan Clicked")
                }
            }
         false
        }
    }


}
