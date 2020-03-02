package com.approteam.appro.fragments

import android.app.AlertDialog
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
import com.approteam.appro.data_models.Appro
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.appro_fragment.*



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
        buttonDisable(json!!)
        btnShowBars.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right,android.R.anim.slide_in_left,android.R.anim.slide_out_right)?.addToBackStack(null)?.replace(R.id.container, barListFragment)?.commit()
        }
        btnJoinAppro.setOnClickListener {
            if (!checkCurrentApproEquals(json)){ //If the appro isn't the same appro as in the preferences user will be alerted.
                buildAlert(c,json)
            } else {
                approToSharedPrefs(arguments?.getString("approJson")!!)
                val homeFragment = HomeFragment(c)
                activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.container,homeFragment)?.commit()
            }
        }

    }
    //Disables the join button if the user is already in the selected appro
    private fun buttonDisable(approFromBundle: String){
        val currApro= getCurrentApproData(c)
        if (currApro != DEF_APPRO_VALUE) {
            val currApproFromPrefs = Gson().fromJson(currApro, Appro::class.java)
            val currApproFromBundle = Gson().fromJson(approFromBundle, Appro::class.java)

            if (currApproFromBundle.name == currApproFromPrefs.name) {
                btnJoinAppro.isEnabled = false
                btnJoinAppro.isClickable = false
                btnJoinAppro.text = getString(R.string.alreadyInAppro)
            }
        }
    }
    //Checks if the current appro being viewed matches the one in shared preferences
    private fun checkCurrentApproEquals(approFromBundle: String):Boolean{
        val currApro= getCurrentApproData(c)
        var bool = true
        if (currApro!= DEF_APPRO_VALUE) {
            val currApproFromPrefs = Gson().fromJson(currApro, Appro::class.java)
            val currApproFromBundle = Gson().fromJson(approFromBundle, Appro::class.java)
            if (currApproFromBundle.name == currApproFromPrefs.name) {
                Log.d("DBG", "EQUALS")
                bool = true
            } else if (currApproFromBundle.name != currApproFromPrefs.name) {
                Log.d("DBG", "NOT EQUAL")
                bool = false
            }
        }
        return bool
    }
    //Alerts the user when they are about to join a new Appro when the old one is active
    private fun buildAlert(ctx: Context,approString:String){
        val builder = AlertDialog.Builder(ctx)
        builder.setTitle(R.string.approReplacementWarn)
        builder.setMessage(R.string.approReplacementMessage)
        builder.setPositiveButton("Ok"){ dialog, which -> approToSharedPrefs(approString) }
        builder.setNegativeButton(R.string.cancel){dialog, which -> Log.d("DBG","Cancelled replacement") }
        val alert: AlertDialog = builder.create()
        alert.setCancelable(true)
        alert.show()
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

    private fun getCurrentApproData(ctx: Context):String{
        val mPrefs = ctx.getSharedPreferences(PREF_APPRO,Context.MODE_PRIVATE)
        val approJsonString = mPrefs.getString(PREF_APPRO, DEF_APPRO_VALUE)
        Log.d("DBG", "GOT APPRO: $approJsonString")
        return approJsonString!!
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            sharedElementEnterTransition = TransitionInflater.from(c).inflateTransition(android.R.transition.slide_top)
            Log.d("DBG","Transition")
        }

    }
}