package com.approteam.appro.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionInflater
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.approteam.appro.*
import com.google.android.gms.common.api.CommonStatusCodes
import kotlinx.android.synthetic.main.appro_fragment.*
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.home_list_item.*


class HomeFragment(ctx: Context) : Fragment() {

    private val c = ctx
    private val approFragment = ApproFragment(c)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        approListRView.layoutManager = LinearLayoutManager(context)
        super.onViewCreated(view, savedInstanceState)
        approListRView.adapter = HomeViewAdapter(Testdata.homeTestData, c){
            val bundle = Bundle()
            bundle.putString("approName", it.name)
            bundle.putString("approDesc", it.desc)
            bundle.putString("approPic", it.pic)


            approFragment.arguments = bundle
            activity?.supportFragmentManager?.beginTransaction()?.addSharedElement(cardImage,it.name)?.addToBackStack(null)
                ?.replace(R.id.container, approFragment)?.commit()


        }



    }

}