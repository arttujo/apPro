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
        val aName = view.findViewById<TextView>(R.id.cardTitleAp)
        val aImage = view.findViewById<ImageView>(R.id.cardImageAp)
        val aDesc = view.findViewById<TextView>(R.id.cardDescAp)
        val transName = arguments?.getString("approName")
        aImage.transitionName = transName
        aName.text = arguments?.getString("approName")
        val imageItem = arguments?.getString("approPic")
        picasso.load(imageItem).into(aImage)
        aDesc?.text = arguments?.getString("approDesc")
        val btn = view.findViewById<Button>(R.id.btnShowBars)
        btn.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.addToBackStack(null)?.replace(R.id.container, barListFragment)?.commit()
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            sharedElementEnterTransition = TransitionInflater.from(c).inflateTransition(android.R.transition.slide_top)
            Log.d("DBG","Transition")
        }

    }
}