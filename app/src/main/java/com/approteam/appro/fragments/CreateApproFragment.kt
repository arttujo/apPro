package com.approteam.appro.fragments

import android.content.Context
import android.graphics.drawable.ClipDrawable.HORIZONTAL
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.approteam.appro.data_models.Bar
import com.approteam.appro.R
import com.approteam.appro.adapters.CreateApproAdapter
import com.google.gson.Gson
import kotlinx.android.synthetic.main.createappro_fragment.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL


class CreateApproFragment(ctx: Context) : Fragment() {

    private val c = ctx
    private val barsFrag = CreateApproBarsFragment(c)
    private var cMonth:Int? =null
    private var cYear:Int?=null
    private var cDay:Int?=null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):
            View? {
        return inflater.inflate(R.layout.createappro_fragment, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        val btn = view.findViewById<Button>(R.id.cancelButton)
        val editName = view.findViewById<EditText>(R.id.editApproName)
        val editDesc = view.findViewById<EditText>(R.id.editApproDesc)

        selectBars.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right,android.R.anim.slide_in_left,android.R.anim.slide_out_right)?.addToBackStack(null)
                ?.replace(R.id.container, barsFrag)?.commit()
        }

        selectImageBtn.setOnClickListener {

        }

        createApproCalendar.setOnDateChangeListener { view, year, month, dayOfMonth ->
            cYear = year
            cMonth = month
            cDay = dayOfMonth
        }


        btn?.setOnClickListener {
            // return to home fragment
            activity?.supportFragmentManager?.popBackStack()
        }
    }
}


