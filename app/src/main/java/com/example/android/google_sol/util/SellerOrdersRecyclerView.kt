package com.example.android.google_sol.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android.google_sol.R


class SellerOrdersRecyclerView(
    private val itemList : ArrayList<SellerOrdersDTO>,
    private val itemClickListner : SellerOrderInterface
) : RecyclerView.Adapter<SellerOrdersRecyclerView.viewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): viewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
             R.layout.seller_order_recyclerview_layout,
            parent,
            false
        )
        return viewHolder(itemView)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
       holder.bindData(itemList[position])

        holder.title.setOnClickListener{
            itemClickListner.itemListner(itemList[position],position)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class viewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val title : TextView = itemView.findViewById(R.id.oder_title)
        val quantity : TextView = itemView.findViewById(R.id.order_quantity)
        val price : TextView = itemView.findViewById(R.id.tvAmountRecieved)
        fun bindData(sellerOrdersDTO: SellerOrdersDTO){
            title.text = "Product Name : "+sellerOrdersDTO.ProductName
            quantity.text = "Total Orders : "+sellerOrdersDTO.ProductQuantity + "Kg"
            price.text = ""
        }
    }


}