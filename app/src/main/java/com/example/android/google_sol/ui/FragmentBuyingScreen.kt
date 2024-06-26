package com.example.android.google_sol.ui

import android.graphics.Color
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
import com.example.android.google_sol.databinding.BuyingBinding
import com.example.android.google_sol.util.*
import com.example.android.google_sol.util.CheckoutDTO.AddressDTO
import com.example.android.google_sol.util.CheckoutDTO.PriceDTO
import com.google.firebase.firestore.FirebaseFirestore
import java.util.HashMap

class FragmentBuyingScreen : Fragment(), ItemClickListener {
    private val binding by lazy { BuyingBinding.inflate(layoutInflater) }
    private val viewModel: SellerViewModal by activityViewModels()
    private val db = FirebaseFirestore.getInstance()
    private val mainData = ArrayList<recyclerDto>()
    private var hashMap: HashMap<String, String> = HashMap()
    private val itemAdapter by lazy { RecyclerViewAdapter(mainData, this ) }
    private var finalPrice:Int = 0
    private var particularPrice:Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnProceed.setOnClickListener{
            viewModel.setPriceForCheckout(
                PriceDTO(
                    finalPrice.toString()
                )
            )
            viewModel.setScreenState(MainActivity.CHECKOUT_SCREEN)
        }

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

            if (modal.open == "Open") {
                binding.openText.setTextColor(Color.parseColor("#80ed99"))
                binding.openText.text = modal.open
            }
            if(modal.open=="Closed"){
                binding.openText.setTextColor(Color.parseColor("#d90429"))
                binding.openText.text = modal.open
            }

            db.collection("vendors")
                .whereEqualTo("Name", modal.Name)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val rr: String =
                            document.get("product").toString().removePrefix("{").removeSuffix("}")
                                .removeSuffix(" ")
                                .removePrefix(" ")
                        val result: List<String> = rr.split(",")
                        result.forEach {
                            val result1: List<String> = rr.split("=")
                            //Toast.makeText(requireActivity(),result1[1],Toast.LENGTH_SHORT).show()
                             Log.d("FINAL", it)
                            mainData.add(
                                recyclerDto(
                                    it.split("=")[0],
                                     0,
                                    0,
                                    it.split("=")[1]+"- per Kg"
                                )
                            )
                            itemAdapter.notifyDataSetChanged()
                        }
                    }
                }
        }

    }

    override fun sub(textData: recyclerDto, position: Int) {
        textData.itemCount -=1
        val price = textData.price.split("-")[0]
        finalPrice -= (1) * price.toInt()
        binding.tvTotalPrice.text = "Rs.$finalPrice"
        itemAdapter.notifyItemChanged(position)

    }

    override fun add(textData: recyclerDto, position: Int) {
        textData.itemCount +=1
        val price = textData.price.split("-")[0]
        particularPrice = price.toInt()
        finalPrice += (1) * price.toInt()
        binding.tvTotalPrice.text = "Rs.$finalPrice"
        itemAdapter.notifyItemChanged(position)

        viewModel.dataForBuying.observe(requireActivity()){
            val modal = it as BuyingDTO
            hashMap.put("sellerName",modal.Name)
        }

        viewModel.priceForCheckout.observe(requireActivity()){
            val price = it as PriceDTO
            hashMap.put("price", price.toString())
        }

        viewModel.addressForCheckout.observe(requireActivity()){
            val modal= it as AddressDTO
            val address = modal.Address
            hashMap["address"] = address
        }

        viewModel.userDetails.observe(requireActivity()){
            val modal = it as UserDetailsDto
            val name = modal.userName
            val email = modal.userEmail
            hashMap["userName"] = name
            hashMap["userEmail"] = email
        }


        val docRef = db.collection("orders").document()
        val newData = hashMapOf(
            "Product Name" to textData.title,
            "ProductName" to textData.title,
            "Quantity" to textData.itemCount.toString(),
            "SellerName" to hashMap["sellerName"],
            "PaymentMethod" to "CashOnDelivery",
            "BuyerName" to hashMap["userName"],
            "BuyerEmail" to hashMap["userEmail"],
            "Price" to particularPrice,
            "Address" to hashMap["address"]
        )
        docRef.set(newData)
    }


}
