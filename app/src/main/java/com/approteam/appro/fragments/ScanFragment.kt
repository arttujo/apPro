package com.approteam.appro.fragments

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.util.SparseArray
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.approteam.appro.DEF_APPRO_VALUE
import com.approteam.appro.LocationListener
import com.approteam.appro.PREF_APPRO
import com.approteam.appro.R
import com.approteam.appro.data_models.Appro
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.gson.Gson
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

@Suppress("NAME_SHADOWING")
class ScanFragment(ctx: Context) : Fragment(),LocationListener {

    private lateinit var detector: BarcodeDetector
    private lateinit var imageScanView: SurfaceView
    private lateinit var camera: CameraSource
    private lateinit var barCodes: SparseArray<Barcode>
    private lateinit var tV: TextView
    private lateinit var blinkAnimation: ObjectAnimator
    private var cameraEnabled = false
    private var c = ctx
    private val CAMERA_REQUEST_CODE = 200
    private val stampsFragment = StampsFragment(c)
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var MAX_DISTANCE_TO_BAR = 50.0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.scan_fragment, container, false)
        tV = view.findViewById(R.id.scanTV)
        tV.text = getString(R.string.QR_search_text)
        imageScanView = view.findViewById(R.id.scanImage)
        detector = BarcodeDetector.Builder(c).setBarcodeFormats(Barcode.ALL_FORMATS).build()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // QR-code detector operational -> enable camera
        if (detector.isOperational) {
            requestCamera()
            if (cameraEnabled) {
                initScanner()
            }
        } else {
            Log.d("DBG", "Imagescanner is not operational")
        }


    }

    // Initialize QR-code scanner
    private fun initScanner() {
        // Camera build parameters
        // context and detector, back camera, fps: 30, 1024*768 size, autofocus (needs permission in manifest)
        camera = CameraSource.Builder(c, detector)
            .setFacing(CameraSource.CAMERA_FACING_BACK)
            .setRequestedFps(30f).setRequestedPreviewSize(1024, 768)
            .setAutoFocusEnabled(true)
            .build()
        // callbacks
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

            // start camera and start blinking of scanTV (Scanning textview)
            override fun surfaceCreated(holder: SurfaceHolder?) {
                camera.start(holder)
                scanBlinkEffectEnable()

            }
        })
        detector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {}
            override fun receiveDetections(detections: Detector.Detections<Barcode>?) {
                barCodes = detections!!.detectedItems
                // Returns value of barcode and is accessible with onActivityResult
                if (barCodes.size() > 0) {
                    val barcode = barCodes.valueAt(0).displayValue
                    val intent = Intent()
                    intent.putExtra("barcode", barcode)
                    onActivityResult(CommonStatusCodes.SUCCESS, 666, intent)
                }
            }

        })
    }

    // Camera destroy
    override fun onDestroy() {
        super.onDestroy()
        if (cameraEnabled) {
            camera.stop()
            camera.release()
        }
    }

    override fun onResume() {
        super.onResume()
        initScanner()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE) {
            handlePermissionResult(grantResults)
            Log.d("DBG", "camera permission approved right now")
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    // Handle camera permissions
    private fun handlePermissionResult(grantResults: IntArray) {
        val denied =
            grantResults.indices.filter { grantResults[it] != PackageManager.PERMISSION_GRANTED }
        if (denied.isEmpty()) {
            cameraEnabled = true
            activity?.supportFragmentManager?.beginTransaction()?.detach(this)?.attach(this)
                ?.commit()
        } else {
            Toast.makeText(c, "Camera permission is needed for using this App!", Toast.LENGTH_LONG)
                .show()

        }
    }

    // Camera permissions
    private fun requestCamera() {
        val permissionStatus =
            ContextCompat.checkSelfPermission(c, android.Manifest.permission.CAMERA)
        if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
        }
    }

    private fun getApproFromPrefs():String{
        val mPrefs = c.getSharedPreferences(PREF_APPRO,Context.MODE_PRIVATE)
        val approJsonString = mPrefs.getString(PREF_APPRO, DEF_APPRO_VALUE)
        return approJsonString!!
    }

    // Function to check if the user is near the bar. returns true if so, False otherwise
    private fun checkIfNear(bar: String):Float{
        val appro = Gson().fromJson(getApproFromPrefs(),Appro::class.java)
        val approBars = appro.bars
        val bar = approBars!!.filter { s -> s.name == bar }
        Log.d("DBG","LAT AND LONG IN SCAN: $latitude $longitude")
        val barLat = bar[0].latitude
        val barLon = bar[0].longitude
        Log.d("DBG","BAR LON AND LAT: $barLon $barLat")
        val loc1 = Location("Bar")
        val loc2 = Location("Self")
        loc1.longitude = barLon!!
        loc1.latitude = barLat!!
        loc2.latitude = latitude!!
        loc2.longitude = longitude!!
        val distance = loc1.distanceTo(loc2)
        Log.d("DBG", distance.toString())
        return distance
    }



    override fun onLocationResults(lat: Double, lon: Double) {
        Log.d("DBG", "Location received in Scan")
        longitude = lon
        latitude = lat

    }

    // Returns a value from QR-code (barcode)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0) {
            if (resultCode == 666) {
                if (data != null) {
                    val barcode = data.getStringExtra("barcode")
                    //TODO Check if near bar
                    val distance = checkIfNear(barcode!!)
                    if(distance<MAX_DISTANCE_TO_BAR) {
                        scanBlinkEffectDisable()
                        tV.text = getString(R.string.QR_code_found)
                        Log.d("DBG, QRSCAN", barcode)
                        val bundle = Bundle()
                        bundle.putString("qrcode", barcode)
                        stampsFragment.arguments = bundle
                        activity?.supportFragmentManager?.beginTransaction()?.setCustomAnimations(
                            android.R.anim.slide_in_left,
                            android.R.anim.slide_out_right,
                            android.R.anim.slide_in_left,
                            android.R.anim.slide_out_right
                        )?.addToBackStack(null)
                            ?.replace(R.id.container, stampsFragment)?.commit()
                    } else {
                        Log.d("DBG","Distance too far")
                    }
                } else {
                    tV.text = getString(R.string.noData)
                }
            }
        }
    }

    // Create a custom blinking text for scanTV (Scan textview)
    private fun scanBlinkEffectEnable() {
        blinkAnimation =
            ObjectAnimator.ofInt(tV, "textColor", Color.WHITE, Color.TRANSPARENT, Color.WHITE)
        blinkAnimation.duration = 1000
        blinkAnimation.repeatCount = ObjectAnimator.INFINITE
        blinkAnimation.repeatMode = ObjectAnimator.REVERSE
        blinkAnimation.start()
    }

    // Disable blinking of scanTV (Scan textview)
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






