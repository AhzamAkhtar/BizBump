package com.example.android.google_sol.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.google_sol.util.BuyingDTO
import com.example.android.google_sol.util.SellerViewModal
import com.example.android.google_sol.databinding.BuyingBinding
import com.example.android.google_sol.util.RecyclerViewAdapter
import com.example.android.google_sol.util.recyclerDto
import com.google.firebase.firestore.FirebaseFirestore

class FragmentBuyingScreen : Fragment() {
    private val binding by lazy { BuyingBinding.inflate(layoutInflater) }
    private val viewModel: SellerViewModal by activityViewModels()
    private val db = FirebaseFirestore.getInstance()
    private val mainData = ArrayList<recyclerDto>()
    private val itemAdapter by lazy { RecyclerViewAdapter(mainData) }

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

        binding.recyclerViewItem.layoutManager = LinearLayoutManager(
            requireActivity(), RecyclerView.VERTICAL, false
        )
        binding.recyclerViewItem.adapter = itemAdapter
    }

    private fun getData() {
        viewModel.dataForBuying.observe(requireActivity()) {
            val modal = it as BuyingDTO
            binding.headText.text = modal.Name
            binding.subText.text = modal.Type
            binding.openText.text = modal.open
            binding.distanceText.text = modal.distance
            binding.tvAddress.text = modal.address


            db.collection("vendors")
                .whereEqualTo("Name", modal.Name)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val rr: String =
                            document.get("products").toString().removePrefix("[").removeSuffix("]")
                                .removeSuffix(" ")
                                .removePrefix(" ")
                        val result: List<String> = rr.split(",").map { it.trim() }
                        result.forEach {
                            Log.d("FINAL", it)
                            mainData.add(
                                recyclerDto(
                                    it
                                )
                            )
                            itemAdapter.notifyDataSetChanged()
                        }
                    }
                }
        }
    }
}