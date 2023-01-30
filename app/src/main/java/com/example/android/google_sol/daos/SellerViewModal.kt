package com.example.android.google_sol.daos

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.google_sol.DataClass.SellerDto

class SellerViewModal  : ViewModel(){
    private val _sellerData = MutableLiveData<SellerDto>()
    val sellerData : MutableLiveData<SellerDto>
    get() = _sellerData

    fun setSellerData(data : SellerDto){
        _sellerData.value = data
    }

    private val _sellerDisplayForBottomNavigation = MutableLiveData<SellerDto>()
    val sellerDisplayForBottomNavigation : MutableLiveData<SellerDto>
    get() = _sellerDisplayForBottomNavigation

    fun setSellerDisplayForBottomNavigation(data : SellerDto){
        _sellerDisplayForBottomNavigation.value = data
    }
}