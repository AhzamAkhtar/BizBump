package com.example.android.google_sol.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.android.google_sol.databinding.FragmentSellerOrdersBinding
import com.example.android.google_sol.util.SellerViewModal

class FragmentSellerScreen : Fragment() {
    private val binding by lazy { FragmentSellerOrdersBinding.inflate(layoutInflater) }
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

        
    }
}