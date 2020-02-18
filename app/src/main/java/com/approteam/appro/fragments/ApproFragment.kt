package com.approteam.appro.fragments

import android.content.Context
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.approteam.appro.*
import com.google.android.gms.vision.barcode.Barcode
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.home_list_item.view.*

class ApproFragment(ctx: Context) : Fragment() {

    private val c = ctx
    private val picasso = Picasso.get()

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
        val aName = view.findViewById<TextView>(R.id.cardTitle)
        val aImage = view.findViewById<ImageView>(R.id.cardImage)
        val aDesc = view.findViewById<TextView>(R.id.cardDesc)
        aName.text = arguments?.getString("approName")
        val imageItem = arguments?.getString("approPic")
        picasso.load(imageItem).into(aImage)
        aDesc?.text = arguments?.getString("approDesc")

    }
}