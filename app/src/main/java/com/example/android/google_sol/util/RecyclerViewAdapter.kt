package com.example.android.google_sol.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.android.google_sol.R

class RecyclerViewAdapter(
    private var itemList: ArrayList<recyclerDto>,
    private val itemClickListner : ItemClickListener
) : RecyclerView.Adapter<RecyclerViewAdapter.viewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): viewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_layout,
            parent, false
        )
        return viewHolder(itemView)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        ///holder.title.text = itemList[position].title

        holder.bindData(itemList[position])

        holder.addButton.setOnClickListener{
            itemClickListner.add(itemList[position],position)
        }
        holder.subButton.setOnClickListener{
            itemClickListner.sub(itemList[position],position)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.stringText)
        val counterText:TextView = itemView.findViewById(R.id.itemCount)
        val priceText:TextView = itemView.findViewById(R.id.tvprice)
        val addButton : Button = itemView.findViewById(R.id.add)
        val subButton : Button = itemView.findViewById(R.id.minus)


        fun bindData(recyclerDto: recyclerDto){
            title.text = recyclerDto.title
            priceText.text = recyclerDto.price
            counterText.text  = "${recyclerDto.itemCount}"
        }
//        init {
//            itemView.setOnClickListener{
//                val position : Int = adapterPosition
//                title.setText("ggggg")
//                Toast.makeText(itemView.context,position.toString(),Toast.LENGTH_SHORT).show()
//            }
//        }
    }


}