package com.example.android.google_sol.util

interface ItemClickListener {
    fun sub(textData: recyclerDto, position: Int)
    fun add(textData: recyclerDto, position: Int)
}
