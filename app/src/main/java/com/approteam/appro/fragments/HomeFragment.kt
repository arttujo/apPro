package com.approteam.appro.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import com.approteam.appro.*
import kotlinx.android.synthetic.main.appro_fragment.*
import kotlinx.android.synthetic.main.home_fragment.*

class HomeFragment(ctx: Context): Fragment(){

    private val c = ctx

    private val approFragment = ApproFragment(c)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.home_fragment,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        approListRView.layoutManager = LinearLayoutManager(context)
        super.onViewCreated(view, savedInstanceState)
        approListRView.adapter = HomeViewAdapter(Testdata.homeTestData,c){
            approItemClicked(it)
        }

    }
    private fun approItemClicked(item: Sample) {
        Log.d("DBG", "Clicked $item")
    }

}