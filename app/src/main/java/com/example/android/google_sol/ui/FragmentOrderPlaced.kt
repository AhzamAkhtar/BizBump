package com.example.android.google_sol.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.android.google_sol.databinding.FragmentOrderPlacedBinding
import com.example.android.google_sol.util.SellerViewModal

class FragmentOrderPlaced : Fragment() {
    private val binding by lazy { FragmentOrderPlacedBinding.inflate(layoutInflater) }
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

        binding.btnorderPlaced.setOnClickListener{
            viewModel.setScreenState(MainActivity.MAIN_SCREEN)
        }
    }
}