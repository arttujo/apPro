package com.approteam.appro.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.createBitmap
import androidx.fragment.app.Fragment
import com.approteam.appro.MainActivity
import com.approteam.appro.R
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.MultiDetector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import java.io.File

class ScanFragment(ctx: Context) : Fragment() {

    private lateinit var thisCode: Barcode
    private lateinit var barcodes: SparseArray<Barcode>
    private lateinit var detector: BarcodeDetector
    private lateinit var myBitmap: Bitmap
    private lateinit var frame: Frame
    private lateinit var imageScanView: ImageView
    private var c = ctx


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.scan_fragment, container, false)
        detector = BarcodeDetector.Builder(c).setBarcodeFormats(Barcode.ALL_FORMATS).build()
        myBitmap = BitmapFactory.decodeResource(c.resources, R.drawable.qr_placeholder)
        val btn = view?.findViewById<Button>(R.id.scanButton)
        frame = Frame.Builder().setBitmap(myBitmap).build()
        barcodes = detector.detect(frame)
        Log.d("DBG", "$barcodes")
        thisCode = barcodes.valueAt(0)
        val tV: TextView? = view.findViewById(R.id.barCodeInfo)
        btn?.setOnClickListener {
            tV?.text = thisCode.rawValue
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageScanView = view.findViewById(R.id.scanImage)
        imageScanView.setImageBitmap(myBitmap)
        Log.d("DBG", "$thisCode")

    }

}





