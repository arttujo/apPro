package com.approteam.appro.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.approteam.appro.DEF_APPRO_VALUE
import com.approteam.appro.HomeViewAdapter
import com.approteam.appro.PREF_APPRO
import com.approteam.appro.R
import com.approteam.appro.adapters.StampsViewAdapter
import com.approteam.appro.data_models.Appro
import com.approteam.appro.data_models.Bar
import com.google.android.gms.vision.barcode.Barcode
import com.google.gson.Gson

import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.stamps_fragment.*
import kotlinx.android.synthetic.main.stamps_list_item.*
import kotlinx.android.synthetic.main.stamps_list_item.view.*

class StampsFragment(ctx: Context) : Fragment() {

    val c = ctx

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.stamps_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        stamps_grid_view.layoutManager = GridLayoutManager(c, 2)
        super.onViewCreated(view, savedInstanceState)
        // Get approstring

       if (getCurrentApproData(c) != DEF_APPRO_VALUE) {
           val str = getCurrentApproData(c)
           val appro = Gson().fromJson(str, Appro::class.java)
           // Get bars in appro
           val bars = appro.bars!!
           // Scanned barcode from bundle
           val barcode = arguments?.getString("qrcode")
           if (barcode != null) {
               // Indexing if needed,
               for ((index, bar) in bars.withIndex()) {
                   // Iterate through bars to find matching barcode (barname)
                   when (bar.name) {
                       // When barcode found, either bar is already visited or not
                       barcode -> if (bar.visited) {
                           Log.d("DBG", "Stamp already added")
                           Toast.makeText(
                               c,
                               "You have already collected this stamp",
                               Toast.LENGTH_SHORT
                           ).show()
                       } else {
                           Log.d("DBG, add", "Adding stamp")
                           Toast.makeText(
                               c,
                               "Stamp added for: $barcode",
                               Toast.LENGTH_SHORT
                           ).show()
                           bar.visited = true
                       }
                   }
               }
               // Convert approstring to json
               val approString = Gson().toJson(appro)
               // Apply approstring in sharedpreferences
               applyStamp(approString)
               Log.d("DBG, appro", bars.toString())
           }
           stamps_grid_view.adapter = StampsViewAdapter(bars, c) {
               Log.d("DBG", "Grid item $it Clicked!")

           }
       }
    }

    private fun getCurrentApproData(ctx: Context): String {
        val mPrefs = ctx.getSharedPreferences(PREF_APPRO, Context.MODE_PRIVATE)
        val approJsonString = mPrefs.getString(PREF_APPRO, DEF_APPRO_VALUE)
        Log.d("DBG", "GOT APPRO: $approJsonString")
        return approJsonString!!
    }

    private fun applyStamp(barVisited: String) {
        val mPrefs: SharedPreferences = c.getSharedPreferences(PREF_APPRO, Context.MODE_PRIVATE)
        val editor = mPrefs.edit()
        editor.putString(PREF_APPRO, barVisited)
        editor.apply()
    }
}


