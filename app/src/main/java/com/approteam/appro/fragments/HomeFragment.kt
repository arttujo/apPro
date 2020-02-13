package com.approteam.appro.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import com.approteam.appro.HomeViewAdapter
import com.approteam.appro.MainActivity
import com.approteam.appro.R
import com.approteam.appro.Sample
import kotlinx.android.synthetic.main.home_fragment.*

class HomeFragment(ctx: Context): Fragment(){


    private val sampleData = listOf(
        Sample("First appro", "Nice appro", Uri.parse("splash.png")),
        Sample("Second appro", "Another nice appro", Uri.parse("splash.png"))
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.home_fragment,container,false)
    }


}