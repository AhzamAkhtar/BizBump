package com.example.android.google_sol.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.android.google_sol.R

class RecyclerViewAdapter(
    private var itemList: ArrayList<recyclerDto>
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
        holder.title.text = itemList[position].title
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.textView)

        init {
            itemView.setOnClickListener{
                val position : Int = adapterPosition
                title.setText("ggggg")
                Toast.makeText(itemView.context,position.toString(),Toast.LENGTH_SHORT).show()
            }
        }
    }


}