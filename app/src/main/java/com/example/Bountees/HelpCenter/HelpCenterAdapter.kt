package com.example.Bountees.HelpCenter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.Bountees.R

class HelpCenterAdapter(private val itemList: List<DataClassHelpCenter>) : RecyclerView.Adapter<HelpCenterAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        val tvDescription: TextView = itemView.findViewById(R.id.tv_description)
        val imgExpand: ImageView = itemView.findViewById(R.id.img_expand)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.activity_help_center_display_layout, parent,false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = itemList[position]

        holder.tvTitle.text = currentItem.title
        holder.tvDescription.text = currentItem.description

        holder.imgExpand.setOnClickListener {
            if (holder.tvDescription.visibility == View.GONE){

                holder.tvDescription.visibility == View.VISIBLE

                holder.imgExpand.setImageResource(R.drawable.ic_close)

            }else{
                holder.tvDescription.visibility = View.VISIBLE
                holder.imgExpand.setImageResource(R.drawable.ic_expand)
            }
        }
    }
}