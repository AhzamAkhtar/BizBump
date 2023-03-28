package com.example.android.google_sol.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.google_sol.databinding.FragmentSellerOrdersBinding
import com.example.android.google_sol.util.CheckoutDTO.PriceDTO
import com.example.android.google_sol.util.ItemClickListener
import com.example.android.google_sol.util.SellerOrdersDTO
import com.example.android.google_sol.util.SellerOrdersRecyclerView
import com.example.android.google_sol.util.SellerViewModal
import com.google.firebase.firestore.FirebaseFirestore

class FragmentSellerScreen : Fragment()  {
    private val binding by lazy { FragmentSellerOrdersBinding.inflate(layoutInflater) }
    private val viewModel : SellerViewModal by activityViewModels()
    private val db = FirebaseFirestore.getInstance()
    private val mainData = ArrayList<SellerOrdersDTO>()
    private val itemAdapter by lazy {SellerOrdersRecyclerView(mainData)}
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
        binding.recyclerViewForSellerOrder.layoutManager = LinearLayoutManager(
            requireActivity(),RecyclerView.VERTICAL,false
        )
        binding.recyclerViewForSellerOrder.adapter = itemAdapter


    }



    private fun getData(){
        db.collection("orders")
            .whereEqualTo("SellerName","Raju")
            .get()
            .addOnSuccessListener { documents ->
                for(document in documents){
                    val productName = document.getString("Product Name")
                    val productQuantity = document.getString("Quantity")
                    val price = document.getString("Price")
                    mainData.add(
                        SellerOrdersDTO(
                            productName.toString(),
                            productQuantity.toString(),
                            price.toString()
                        )
                    )
                    itemAdapter.notifyDataSetChanged()
                }
            }
    }
}