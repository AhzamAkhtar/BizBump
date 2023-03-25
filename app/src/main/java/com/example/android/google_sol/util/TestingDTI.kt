package com.example.android.google_sol.util

import com.google.gson.annotations.SerializedName

data class TestingDTI(
    @SerializedName("Title")
    val title : String,
    @SerializedName("Count")
    val itemCount : String
)
