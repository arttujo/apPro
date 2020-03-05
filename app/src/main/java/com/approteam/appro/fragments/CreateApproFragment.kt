package com.approteam.appro.fragments

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.approteam.appro.MainActivity
import com.approteam.appro.R
import kotlinx.android.synthetic.main.createappro_fragment.*


class CreateApproFragment(ctx: Context) : Fragment() {

    private val c = ctx
    private val barsFrag = CreateApproBarsFragment(c)
    private val IMAGE_PICK_CODE = 999
    private var imageData: ByteArray? = null
    private var date: String? = null
    private var imageUri:Uri? = null



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):
            View? {

        return inflater.inflate(R.layout.createappro_fragment, container, false)
    }

    private fun addListeners(){
        priceField!!.addTextChangedListener(TextWatcher)
        approLocation!!.addTextChangedListener(TextWatcher)
        editApproDesc!!.addTextChangedListener(TextWatcher)
        approTime!!.addTextChangedListener(TextWatcher)
        editApproName!!.addTextChangedListener(TextWatcher)
    }
    private fun setupBundle(){
        val bundle = Bundle()
        bundle.putByteArray("IMAGEDATA",imageData)
        bundle.putDouble("PRICE",priceField.text.toString().toDouble())
        bundle.putString("IMAGEURI", imageUri.toString())
        bundle.putString("NAME",editApproName.text.toString())
        bundle.putString("DESC",editApproDesc.text.toString())
        bundle.putString("LOC",approLocation.text.toString())
        bundle.putString("DATE",date)
        bundle.putString("TIME",approTime.text.toString())
        barsFrag.arguments = bundle
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btn = view.findViewById<Button>(R.id.cancelButton)
        selectBars.isEnabled = false
        addListeners()
        selectBars.setOnClickListener {
            setupBundle()
            activity?.supportFragmentManager?.beginTransaction()?.setCustomAnimations(android.R.anim.slide_in_left,android.R.anim.slide_out_right,android.R.anim.slide_in_left,android.R.anim.slide_out_right)?.addToBackStack(null)
                ?.replace(R.id.container, barsFrag)?.commit()
        }

        selectImageBtn.setOnClickListener {
            launchGallery()
        }

        createApproCalendar.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val monthFix = month + 1
            date = "$year-$monthFix-$dayOfMonth"
            Log.d("DBG","DATE: $date")
            checkForm()
        }

        btn?.setOnClickListener {
            // return to home fragment
            activity?.supportFragmentManager?.popBackStack()
        }
    }

    private val TextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(
            s: CharSequence,
            start: Int,
            count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(
            s: CharSequence,
            start: Int,
            before: Int,
            count: Int
        ) {
            checkForm()
        }

        override fun afterTextChanged(s: Editable) {}
    }

    private fun checkForm(){
        val priceInput = priceField!!.text.toString().trim { it <= ' ' }
        val locationInput = approLocation!!.text.toString().trim { it <= ' ' }
        val nameInput = editApproName!!.text.toString().trim{ it <= ' '}
        val descInput = editApproDesc!!.text.toString().trim { it <= ' ' }
        val timeInput = approTime!!.text.toString().trim{it <= ' '}
        selectBars.isEnabled = priceInput.isNotEmpty() && locationInput.isNotEmpty() && nameInput.isNotEmpty() && descInput.isNotEmpty() && timeInput.isNotEmpty() && date != null && imageData != null
    }

    //Creates Image data for the selected image
    private fun createImageData(uri: Uri){
        val inputStream = c.contentResolver.openInputStream(uri)
        inputStream?.buffered()?.use {
            imageData = it.readBytes()
        }
        checkForm()
    }
    //Launches the gallery for image selection
    private fun launchGallery(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent,IMAGE_PICK_CODE)
    }
    //returns image data
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            var uri = data?.data

            if (uri!=null){
                approImage.setImageURI(uri)
                createImageData(uri)
                imageUri = uri
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }



}


