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
import com.approteam.appro.Testdata
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
        stamps_grid_view.adapter = StampsViewAdapter(Testdata.homeTestData,c){
            Log.d("DBG", "Grid item $it Clicked!")
        }

    }


}