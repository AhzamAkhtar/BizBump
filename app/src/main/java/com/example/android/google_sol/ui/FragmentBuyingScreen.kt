package com.example.android.google_sol.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.android.google_sol.dataClass.BuyingDTO
import com.example.android.google_sol.util.SellerViewModal
import com.example.android.google_sol.databinding.BuyingBinding

class FragmentBuyingScreen : Fragment() {
    private val binding by lazy { BuyingBinding.inflate(layoutInflater) }
    private val viewModel: SellerViewModal by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View,savedInstanceState: Bundle?) {
        super.onViewCreated(view,savedInstanceState)
        getData()
    }

    private fun getData(){
        viewModel.dataForBuying.observe(requireActivity()){
            val modal = it as BuyingDTO
            Toast.makeText(requireActivity(),modal.toString(), Toast.LENGTH_SHORT).show()
            binding.headText.text = modal.Name
            binding.subText.text = modal.Type
            binding.openText.text = modal.open
            binding.distanceText.text = modal.distance
            binding.tvAddress.text = modal.address
        }
    }
}