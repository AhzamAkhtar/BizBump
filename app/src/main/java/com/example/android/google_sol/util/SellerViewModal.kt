package com.example.android.google_sol.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.google_sol.util.CheckoutDTO.AddressDTO
import com.example.android.google_sol.util.CheckoutDTO.PriceDTO
import com.example.android.google_sol.util.CheckoutDTO.SellerInfoDTO

class SellerViewModal  : ViewModel(){
    private val _sellerData = MutableLiveData<SellerDto>()
    val sellerData : MutableLiveData<SellerDto>
    get() = _sellerData

    fun setSellerData(data : SellerDto){
        _sellerData.value = data
    }

    private val _dataForBuying = MutableLiveData<BuyingDTO>()
    val dataForBuying : MutableLiveData<BuyingDTO>
    get() = _dataForBuying

    fun setDataForBuying(data : BuyingDTO){
        _dataForBuying.value = data
    }

    private val _screenState = MutableLiveData<Int>()
    val screenState : LiveData<Int>
        get() = _screenState

    fun setScreenState(state : Int){
        _screenState.value = state
    }

    private val _addressForCheckout = MutableLiveData<AddressDTO>()
    val addressForCheckout : MutableLiveData<AddressDTO>
    get() = _addressForCheckout

    fun setAddressForCheckout(data : AddressDTO){
        _addressForCheckout.value = data
    }

    private val _sellerName = MutableLiveData<AddressDTO>()
    val sellerName : MutableLiveData<AddressDTO>
        get() = _sellerName

    fun setSellerName(data : AddressDTO){
        _sellerName.value = data
    }

    private val _priceForCheckout = MutableLiveData<PriceDTO>()
    val priceForCheckout : MutableLiveData<PriceDTO>
        get() = _priceForCheckout

    fun setPriceForCheckout(data : PriceDTO){
        _priceForCheckout.value = data
    }

    private val _sellerDataForCheckout = MutableLiveData<SellerInfoDTO>()
    val sellerDataForCheckout : MutableLiveData<SellerInfoDTO>
        get() = _sellerDataForCheckout

    fun setSellerDataForCheckout(data : SellerInfoDTO){
        _sellerDataForCheckout.value = data
    }


}