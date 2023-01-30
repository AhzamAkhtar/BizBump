package com.example.android.google_sol.DataClass

import com.google.gson.annotations.SerializedName

data class SellerDto(
    @SerializedName("Name")
    val name : String,
    @SerializedName("Type")
    val Type : String,
    @SerializedName("Lat")
    val Lat : Int,
    @SerializedName("Lng")
    val Lng : Int
)
