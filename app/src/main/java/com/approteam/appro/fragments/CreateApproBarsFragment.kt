package com.approteam.appro.fragments

import android.content.Context
import android.graphics.drawable.ClipDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.approteam.appro.R
import com.approteam.appro.adapters.CreateApproAdapter
import com.approteam.appro.data_models.Bar
import com.google.gson.Gson
import kotlinx.android.synthetic.main.createappro_bars_fragment.*
import kotlinx.android.synthetic.main.createappro_fragment.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL

class CreateApproBarsFragment(ctx: Context) : Fragment(){

    val c = ctx

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):
            View? {
        return inflater.inflate(R.layout.createappro_bars_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        createApproBarsRV.layoutManager = LinearLayoutManager(c)
        val itemDecor = DividerItemDecoration(c, ClipDrawable.HORIZONTAL)
        createApproBarsRV.addItemDecoration(itemDecor)
        doAsync {
            val json = URL("http://Foxer153.asuscomm.com:3001/bars").readText()
            val bars = Gson().fromJson(json,Array<Bar>::class.java).toList()
            uiThread {
                createApproBarsProgressBar.visibility = View.GONE
                createApproBarsRV.adapter = CreateApproAdapter(bars,c)
                Log.d("DBG",bars.toString())
            }
        }
        super.onViewCreated(view, savedInstanceState)
    }


}