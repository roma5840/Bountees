package com.example.Bountees.Product


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.Bountees.Dashboard.Dashboard
import com.example.Bountees.Dashboard.DataClassDashboard
import com.example.Bountees.R
import com.squareup.picasso.Picasso

class DashboardAdapter (private val getActivity: Dashboard, private val productList: List<DataClassDashboard>):
    RecyclerView.Adapter<DashboardAdapter.MyViewHolder>() {


    var onItemClick : ((DataClassDashboard) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_dashboard_product_display_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val product = productList[position]
        holder.productTitle.text = productList[position].title
        //holder.productPrice.text = productList[position].price
        holder.productPrice.text = productList[position].price.toString()

        Picasso.get().load(product.image).into(holder.productImage)

//        holder.productImage.setImageResource(productList[position].image)
//        holder.productImage.setImageResource(product.image)

        holder.itemView.setOnClickListener{
            onItemClick?.invoke(product)
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productTitle : TextView = itemView.findViewById(R.id.tv_title)
        val productImage : ImageView = itemView.findViewById(R.id.iv_shirts)
        val productPrice: TextView = itemView.findViewById(R.id.tv_price)


    }

}