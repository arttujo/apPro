package com.approteam.appro.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.approteam.appro.*
import com.approteam.appro.adapters.HomeViewAdapter
import com.approteam.appro.data_models.Appro
import com.github.kittinunf.fuel.Fuel
import com.google.gson.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.home_fragment.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.lang.Exception
import java.lang.RuntimeException


class HomeFragment(ctx: Context) : Fragment() {

    private val c = ctx
    private val approFragment = ApproFragment(c)
    private val createApproFragment = CreateApproFragment(c)
    private var listener: ApproStatusListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.home_fragment, container, false)
        val btn = view.findViewById<Button>(R.id.createButton)
        val lvBtn = view.findViewById<Button>(R.id.leaveApproBtn)
        btn.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.addToBackStack(null)
                ?.replace(R.id.container, createApproFragment)?.commit()
        }
        val jsonString: String? = getCurrentApproData(c)
        if (jsonString == "NULL") {
            lvBtn.visibility = View.INVISIBLE
            listener?.onApproUpdate(false)
        } else {
            listener?.onApproUpdate(true)
        }

        return view
    }

    // Attach context to ApproStatusListener
    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as ApproStatusListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //Fetches appros from the server
        doAsync {
            Fuel.get("http://foxer153.asuscomm.com:3001/test")
                .response { _, _, result ->
                    val (bytes, error) = result
                    if (bytes != null) {
                        Log.d("DBG", "Bytes received!")
                        Log.d("DBG", String(bytes))
                        val appros =
                            Gson().fromJson(String(bytes), Array<Appro>::class.java).toList()
                        uiThread {
                            Log.d("DBG", appros.toString())
                            Log.d("DBG", "UI THREAD")
                            try {
                                homeProgressBar.visibility = View.INVISIBLE
                            } catch (error: Exception) {
                                Log.e("homeprogressBar", error.toString())
                            }
                            approClick(appros, String(bytes))
                        }
                    }
                }
        }

        approListRView.layoutManager = LinearLayoutManager(context)
        super.onViewCreated(view, savedInstanceState)
        leaveApproBtn.setOnClickListener {
            buildAlert(c)
        }
    }

    //Removes the appro from shared preferences
    private fun removeAppro() {
        val mPrefs: SharedPreferences = c.getSharedPreferences(PREF_APPRO, Context.MODE_PRIVATE)
        val editor = mPrefs.edit()
        val str = mPrefs.getString(PREF_APPRO, DEF_APPRO_VALUE)
        if (str != null) {
            editor.putString(PREF_APPRO, DEF_APPRO_VALUE)
            editor.apply()
            approLeft()
            activity?.supportFragmentManager?.beginTransaction()?.detach(this)!!.attach(this)
                .commit()
        }
    }
    // Modify Navigation menu when leaving appro
    private fun approLeft() {
        listener?.onApproUpdate(false)
    }


    //Returns json string for the current appro the user is a part of
    private fun getCurrentApproData(ctx: Context): String {
        val mPrefs = ctx.getSharedPreferences(PREF_APPRO, Context.MODE_PRIVATE)
        val approJsonString = mPrefs.getString(PREF_APPRO, DEF_APPRO_VALUE)
        Log.d("DBG", "GOT APPRO: $approJsonString")
        return approJsonString!!
    }

    //Alerts the user when they are about to leave an appro
    private fun buildAlert(ctx: Context) {
        val builder = AlertDialog.Builder(ctx)
        builder.setTitle(R.string.approLeaveWarn)
        builder.setMessage(R.string.approLeaveMessage)
        builder.setPositiveButton("Ok") { _, _ -> removeAppro() }
        builder.setNegativeButton(R.string.cancel) { _, _ -> Log.d("DBG", "Cancelled leave") }
        val alert: AlertDialog = builder.create()
        alert.setCancelable(true)
        alert.show()
    }


    //Click handler for opening an appro
    private fun approClick(appros: List<Appro>, approJson: String) {
        try {
            approListRView.adapter = HomeViewAdapter(appros, c) {
                val bundle = Bundle()
                val approString = Gson().toJson(it)
                bundle.putString("approName", it.name)
                bundle.putString("approDesc", it.description)
                bundle.putString("approPic", it.image)
                bundle.putString("approJson", approString)
                approFragment.arguments = bundle
                activity?.supportFragmentManager?.beginTransaction()?.setCustomAnimations(
                    android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right,
                    android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right
                )?.addToBackStack(null)
                    ?.replace(R.id.container, approFragment)?.commit()
            }
        } catch (error: Exception) {
            Log.e("approCLICK", error.toString())

        }
    }


}