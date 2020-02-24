package com.approteam.appro.fragments

import android.app.ActionBar
import android.content.Context
import android.drm.DrmStore
import android.graphics.drawable.ClipDrawable.HORIZONTAL
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.approteam.appro.Bar
import com.approteam.appro.R
import com.approteam.appro.adapters.CreateApproAdapter
import com.google.gson.Gson
import kotlinx.android.synthetic.main.createappro_fragment.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL


class CreateApproFragment(ctx: Context) : Fragment() {
    val c = ctx

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):
            View? {
        return inflater.inflate(R.layout.createappro_fragment, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        createApproRec.layoutManager = LinearLayoutManager(c)
        val itemDecor = DividerItemDecoration(c,HORIZONTAL)
        createApproRec.addItemDecoration(itemDecor)
        doAsync {
            val json = URL("http://Foxer153.asuscomm.com:3001/all").readText()
            val bars = Gson().fromJson(json,Array<Bar>::class.java).toList()
            uiThread {
                createApproRec.adapter = CreateApproAdapter(bars,c)
            }
        }
        super.onViewCreated(view, savedInstanceState)
        val btn = view.findViewById<Button>(R.id.cancelButton)
        val editName = view.findViewById<EditText>(R.id.editApproName)
        val editDesc = view.findViewById<EditText>(R.id.editApproDesc)



        btn?.setOnClickListener {
            // return to home fragment
            activity?.supportFragmentManager?.popBackStack()
        }
    }
}


