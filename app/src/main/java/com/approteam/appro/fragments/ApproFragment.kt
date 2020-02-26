package com.approteam.appro.fragments

import android.content.Context
import android.content.Intent
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.approteam.appro.*
import com.google.android.gms.vision.barcode.Barcode
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.appro_fragment.*

class ApproFragment(ctx: Context) : Fragment() {

    private val c = ctx
    private val picasso = Picasso.get()
    private val barListFragment = BarListFragment(c)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):
            View? {
        return inflater.inflate(R.layout.appro_fragment, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val transName = arguments?.getString("approName")
        cardImageAp.transitionName = transName
        cardTitleAp.text = arguments?.getString("approName")
        val imageItem = arguments?.getString("approPic")
        cardDescAp.text = arguments?.getString("approDesc")
        picasso.load(imageItem).into(cardImageAp)
        setupBundle()
        btnShowBars.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.addToBackStack(null)?.replace(R.id.container, barListFragment)?.commit()
        }

    }

    fun setupBundle(){
        val approJsonString = arguments?.getString("approJson")
        val bundle = Bundle()
        bundle.putString("approJson",approJsonString)
        barListFragment.arguments = bundle
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            sharedElementEnterTransition = TransitionInflater.from(c).inflateTransition(android.R.transition.slide_top)
            Log.d("DBG","Transition")
        }

    }
}