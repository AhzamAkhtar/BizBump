package com.example.android.google_sol.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.google_sol.R
import com.example.android.google_sol.databinding.FragmentSellerOrdersBinding
import com.example.android.google_sol.util.*
import com.example.android.google_sol.util.CheckoutDTO.PriceDTO
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.FirebaseFirestore
import org.w3c.dom.Text
import kotlin.math.E

class FragmentSellerScreen : Fragment() , SellerOrderInterface  {
    private val binding by lazy { FragmentSellerOrdersBinding.inflate(layoutInflater) }
    private val viewModel : SellerViewModal by activityViewModels()
    private val db = FirebaseFirestore.getInstance()
    private val mainData = ArrayList<SellerOrdersDTO>()
    private val itemAdapter by lazy {SellerOrdersRecyclerView(mainData,this)}
    private var counter = 0
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
                    counter += 1
                    val productName = document.getString("Product Name")
                    val productQuantity = document.getString("Quantity")
                    val price = document.getString("Price")
                    val address = document.getString("Address")
                    val buyerName = document.getString("BuyerName")
                    val buyeremail = document.getString("BuyerEmail")
                    val paymentMethod = document.getString("PaymentMethod")
                    mainData.add(
                        SellerOrdersDTO(
                            productName.toString(),
                            productQuantity.toString(),
                            price.toString(),
                            buyerName.toString(),
                            buyeremail.toString(),
                            address.toString(),
                            paymentMethod.toString()
                        )
                    )

                    itemAdapter.notifyDataSetChanged()
                }
                binding.tvTotalOrders.text = "Total Orders : "+counter.toString()
            }
    }

    private fun showBottomSheet(
        name : String,
        email : String,
        address : String,
        payment : String
    ){
        val dialog = BottomSheetDialog(requireActivity())
        val view = layoutInflater.inflate(R.layout.seller_oders_bottom_sheet,null)
        dialog.setContentView(view)

        val buyerName = view.findViewById<TextView>(R.id.buyerName)
        val buyerEmail = view.findViewById<TextView>(R.id.buyerEmail)
        val buyerAddress = view.findViewById<TextView>(R.id.buyerAddress)
        val paymentMethod = view.findViewById<TextView>(R.id.tvPayment)

        buyerName.text = name
        buyerEmail.text = email
        buyerAddress.text = address
        paymentMethod.text = payment

        dialog.show()
    }



    override fun itemListner(textData: SellerOrdersDTO, position: Int) {
        showBottomSheet(
            textData.buyerName,
            textData.buyerEmail,
            textData.buyerAddress,
            textData.paymentMethod
        )
    }

}