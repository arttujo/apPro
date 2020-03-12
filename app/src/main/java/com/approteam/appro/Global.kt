package com.approteam.appro

import android.util.Log

//Interface for sending location updates to parts where they are needed
interface LocationListener {
    fun onLocationResults(lat: Double, lon: Double) {
        Log.d("DBG", "Location received")
    }
}

interface ApproStatusListener {
    fun onApproUpdate(enabled: Boolean) {
        if (enabled) {
            Log.d("DBG", "appro active")
        } else {
            Log.d("DBG", "appro not active")
        }
    }

}


const val PREF_APPRO = "PREF_APPRO"
const val DEF_APPRO_VALUE = "NULL"
const val PREF_UNIQUE_ID = "PREF_UNIQUE_ID"