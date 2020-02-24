package com.approteam.appro

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class Bar {
    @SerializedName("phonenumber")
    @Expose
    var phonenumber: String? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("address")
    @Expose
    var address: String? = null
    @SerializedName("latitude")
    @Expose
    var latitude: Double? = null
    @SerializedName("tags")
    @Expose
    var tags: List<String>? = null
    @SerializedName("homepage")
    @Expose
    var homepage: String? = null
    @SerializedName("longitude")
    @Expose
    var longitude: Double? = null
    @SerializedName("description")
    @Expose
    var description: String? = null

}