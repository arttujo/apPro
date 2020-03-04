package com.approteam.appro.fragments

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ClipDrawable.HORIZONTAL
import android.net.Uri
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
import java.io.IOException
import java.io.InputStream
import java.net.URL


class CreateApproFragment(ctx: Context) : Fragment() {

    private val c = ctx
    private val barsFrag = CreateApproBarsFragment(c)
    private var cMonth:Int? =null
    private var cYear:Int?=null
    private var cDay:Int?=null
    private val IMAGE_PICK_CODE = 999
    private var imageData: ByteArray? = null

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
            launchGallery()
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

    @Throws(IOException::class)
    private fun createImageData(uri: Uri){
        val inputStream = c.contentResolver.openInputStream(uri)
        inputStream?.buffered()?.use {
            imageData = it.readBytes()
        }
    }

    private fun launchGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent,IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            var uri = data?.data
            if (uri!=null){
                approImage.setImageURI(uri)
                createImageData(uri)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}


