package com.approteam.appro.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.approteam.appro.MainActivity
import com.approteam.appro.R
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView

class ScanFragment(ctx:Context):Fragment(), ZXingScannerView.ResultHandler {

    private lateinit var zXingScannerView: ZXingScannerView
    var imageScanView: View? = view?.findViewById(R.id.scanImage)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        imageScanView = view?.findViewById(R.id.scanImage)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        qrScan(imageScanView!!)
    }
    private fun qrScan(view : View) {
        zXingScannerView = ZXingScannerView(context)
        zXingScannerView.setResultHandler(this)
        zXingScannerView.startCamera()
    }

    override fun handleResult(p0: Result?) {
        zXingScannerView.resumeCameraPreview(this)
    }

}





