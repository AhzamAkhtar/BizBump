package com.example.android.google_sol.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.android.google_sol.R
import com.example.android.google_sol.databinding.FragmentCheckoutBinding
import com.example.android.google_sol.util.CheckoutDTO.AddressDTO
import com.example.android.google_sol.util.CheckoutDTO.PriceDTO
import com.example.android.google_sol.util.CheckoutDTO.SellerInfoDTO
import com.example.android.google_sol.util.SellerViewModal

class FragmentCheckoutScreen : Fragment() {
    val CHANNEL_ID = "channelID"
    val CHANNEL_NAME = "channelName"
    val NOTIFICATION_ID = 0
    private val binding by lazy { FragmentCheckoutBinding.inflate(layoutInflater) }
    private val viewModel : SellerViewModal by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getData()
        createNotificationChannel()

        val notification = NotificationCompat.Builder(requireActivity() , CHANNEL_ID)
            .setContentTitle("Order Placed")
            .setContentText("Yooooo! Your Order Has Been Placed")
            .setSmallIcon(R.drawable.arrow)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val notificationManager = NotificationManagerCompat.from(requireActivity())

        binding.btnConfirmOrder.setOnClickListener{
            notificationManager.notify(NOTIFICATION_ID,notification)
            viewModel.setScreenState(MainActivity.ORDER_PLACED)
        }
    }

    private fun getData(){
        viewModel.priceForCheckout.observe(requireActivity()){
            val modal = it as PriceDTO
            binding.tvPrice.text = "₹"+modal.Price
        }

        viewModel.addressForCheckout.observe(requireActivity()){
            val modal = it as AddressDTO
            binding.tvAddress.text = modal.Address
        }

        viewModel.sellerDataForCheckout.observe(requireActivity()){
            val modal = it as SellerInfoDTO
            binding.tvSellerName.text = modal.Name
            binding.tvSellerPhoneNumber.text = modal.PhoneNumber

        }
    }

    fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(CHANNEL_ID , CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT).apply {
                lightColor = Color.BLUE
                enableLights(true)
            }
            val manager = requireActivity().getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}