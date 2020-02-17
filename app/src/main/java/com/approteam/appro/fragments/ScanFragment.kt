package com.approteam.appro.fragments

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Parcel
import android.os.Parcelable
import android.renderscript.Sampler
import android.util.Log
import android.util.SparseArray
import android.view.*
import android.view.animation.Animation
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.approteam.appro.MainActivity
import com.approteam.appro.R
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class ScanFragment(ctx: Context) : Fragment() {

    private lateinit var detector: BarcodeDetector
    private lateinit var imageScanView: SurfaceView
    private lateinit var camera: CameraSource
    private lateinit var barCodes: SparseArray<Barcode>
    private lateinit var tV: TextView
    private lateinit var blinkAnimation: ObjectAnimator
    var cameraEnabled = false
    private var c = ctx
    private val CAMERA_REQUEST_CODE = 200


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.scan_fragment, container, false)
        tV = view.findViewById(R.id.scanTV)
        tV.text = getString(R.string.QR_search_text)
        imageScanView = view.findViewById(R.id.scanImage)
        requestCamera()
        detector = BarcodeDetector.Builder(c).setBarcodeFormats(Barcode.ALL_FORMATS).build()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (detector.isOperational) {
            if (cameraEnabled) {
                initScanner()
            }
        } else {
            Log.d("DBG", "Imagescanner is not operational")
        }


    }

    private fun initScanner() {
        // Camera build parameters
        camera = CameraSource.Builder(c, detector)
            .setFacing(CameraSource.CAMERA_FACING_BACK)
            .setRequestedFps(30f).setRequestedPreviewSize(1024, 768)
            .setAutoFocusEnabled(true)
            .build()
        imageScanView.holder.addCallback(object : SurfaceHolder.Callback2 {
            override fun surfaceRedrawNeeded(holder: SurfaceHolder?) {}

            override fun surfaceChanged(
                holder: SurfaceHolder?,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                camera.stop()
            }

            override fun surfaceCreated(holder: SurfaceHolder?) {
                camera.start(holder)
                scanBlinkEffectEnable()

            }
        })
        detector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {}
            override fun receiveDetections(detections: Detector.Detections<Barcode>?) {
                barCodes = detections!!.detectedItems
                val builder = StringBuilder()
                if (barCodes.size() > 0) {
                    val intent = Intent()
                    intent.putExtra("barcode", barCodes.valueAt(0))
                    onActivityResult(CommonStatusCodes.SUCCESS, 666, intent)
                }
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        camera.stop()
        camera.release()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE) {
            handlePermissionResult(permissions, grantResults)
            Log.d("DBG", "camera permission approved right now")
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun handlePermissionResult(permissions: Array<out String>, grantResults: IntArray) {
        val denied =
            grantResults.indices.filter { grantResults[it] != PackageManager.PERMISSION_GRANTED }
        if (denied.isEmpty()) {
            cameraEnabled = true
        } else {
            Toast.makeText(c, "Camera permission denied", Toast.LENGTH_SHORT).show()

        }
    }

    private fun requestCamera() {
        val permissionStatus =
            ContextCompat.checkSelfPermission(c, android.Manifest.permission.CAMERA)
        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            cameraEnabled = true
        } else {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            if (resultCode == 666) {
                if (data != null) {
                    val barcode = data.getParcelableExtra<Barcode>("barcode")
                    scanBlinkEffectDisable()
                    tV.text = getString(R.string.QR_code_found)
                } else {
                    tV.text = "No data"
                }
            }
        }
    }

    private fun scanBlinkEffectEnable() {
        blinkAnimation = ObjectAnimator.ofInt(tV, "textColor", Color.WHITE, Color.TRANSPARENT, Color.WHITE)
        blinkAnimation.duration = 1000
        blinkAnimation.repeatCount = ObjectAnimator.INFINITE
        blinkAnimation.repeatMode = ObjectAnimator.REVERSE
        blinkAnimation.start()
    }
    private fun scanBlinkEffectDisable() {
        doAsync {
            uiThread {
                blinkAnimation.removeAllListeners()
                blinkAnimation.end()
                blinkAnimation.cancel()
            }
        }
    }
}






