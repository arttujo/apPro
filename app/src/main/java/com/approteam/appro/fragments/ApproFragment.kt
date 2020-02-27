package com.approteam.appro.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.approteam.appro.*
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.appro_fragment.*

const val PREF_APPRO = "PREF_APPRO"

class ApproFragment(ctx: Context) : Fragment() {

    private val c = ctx
    private val picasso = Picasso.get()
    private val barListFragment = BarListFragment(c)
    private var approJsonString:String? = null


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
        cardImageAp.transitionName = arguments?.getString("approPic")
        cardTitleAp.transitionName = arguments?.getString("approName")
        cardDescAp.transitionName = arguments?.getString("approDesc")


        cardTitleAp.text = arguments?.getString("approName")
        val imageItem = arguments?.getString("approPic")
        cardDescAp.text = arguments?.getString("approDesc")
        picasso.load(imageItem).into(cardImageAp)
        setupBundle()
        val json = arguments?.getString("approJson")
        Log.d("DBG","received: "+json)
        btnShowBars.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right,android.R.anim.slide_in_left,android.R.anim.slide_out_right)?.addToBackStack(null)?.replace(R.id.container, barListFragment)?.commit()
        }
        btnJoinAppro.setOnClickListener {
            approToSharedPrefs(arguments?.getString("approJson")!!)
        }

    }

    private fun setupBundle(){
        val approJsonString = arguments?.getString("approJson")
        val bundle = Bundle()
        bundle.putString("approJson",approJsonString)
        barListFragment.arguments = bundle
    }
    //Puts Appro json string into shared prefs
    private fun approToSharedPrefs(approString:String){
        val mPrefs: SharedPreferences = c.getSharedPreferences(PREF_APPRO,Context.MODE_PRIVATE)
        val editor = mPrefs.edit()
        approJsonString = mPrefs.getString(PREF_APPRO,null)
        if (approJsonString == null){
            editor.putString(PREF_APPRO,approString)
            editor.apply()
            makeToast("Joined Appro")
        } else if (approJsonString != null){
            editor.putString(PREF_APPRO,approString)
            editor.apply()
            makeToast("Replaced Appro")
        }
    }

    private fun makeToast(text: String){
        val text = text
        val duration = Toast.LENGTH_SHORT
        val toast = Toast.makeText(context,text,duration)
        toast.show()
    }





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            sharedElementEnterTransition = TransitionInflater.from(c).inflateTransition(android.R.transition.slide_top)
            Log.d("DBG","Transition")
        }

    }
}