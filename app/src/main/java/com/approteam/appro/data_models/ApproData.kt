package com.approteam.appro.data_models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
//Model for Appro data received from the server
class Appro {
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("date")
    @Expose
    var date: String? = null
    @SerializedName("time")
    @Expose
    var time: String? = null
    @SerializedName("price")
    @Expose
    var price: Int? = null
    @SerializedName("location")
    @Expose
    var location: String? = null
    @SerializedName("description")
    @Expose
    var description: String? = null
    @SerializedName("image")
    @Expose
    var image: String?= null
    @SerializedName("bars")
    @Expose
    var bars: List<ApproBar>? = null


    class ApproBar {
        @SerializedName("name")
        @Expose
        var name: String? = null
        @SerializedName("address")
        @Expose
        var address: String? = null
        @SerializedName("latitude")
        @Expose
        var latitude: Double? = null
        @SerializedName("longitude")
        @Expose
        var longitude: Double? = null
        @SerializedName("image")
        @Expose
        var image: String? = null
        @SerializedName("barcode")
        @Expose
        var barcode: String? = null

    }

}
