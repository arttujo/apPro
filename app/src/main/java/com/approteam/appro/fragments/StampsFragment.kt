package com.approteam.appro.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.approteam.appro.HomeViewAdapter
import com.approteam.appro.R
import com.approteam.appro.adapters.StampsViewAdapter
import com.approteam.appro.data_models.Appro
import com.google.gson.Gson

import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.stamps_fragment.*

class StampsFragment(ctx: Context):Fragment(){

    val c = ctx

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.stamps_fragment,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        stamps_grid_view.layoutManager = GridLayoutManager(c,2)
        super.onViewCreated(view, savedInstanceState)
        val appro = Gson().fromJson(getCurrentApproData(c),Appro::class.java)
        val bars = appro.bars


        stamps_grid_view.adapter = StampsViewAdapter(bars!!,c){
            Log.d("DBG", "Grid item $it Clicked!")
        }


    }

    private fun getCurrentApproData(ctx: Context):String{
        val mPrefs = ctx.getSharedPreferences(PREF_APPRO,Context.MODE_PRIVATE)
        val approJsonString = mPrefs.getString(PREF_APPRO,null)
        Log.d("DBG", "GOT APPRO: $approJsonString")
        return approJsonString!!
    }


}