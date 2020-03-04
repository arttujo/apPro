package com.approteam.appro.data_models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Bar {
    @SerializedName("Id")
    @Expose
    var id: Int? = null
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
    var image: Any? = null
    @SerializedName("qr")
    @Expose
    var qr: String? = null

}