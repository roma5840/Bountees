package com.example.Bountees.AddToCart




import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.example.Bountees.R
import com.squareup.picasso.Picasso


class CartAdapter(private val cartItems: MutableList<DataClassNew>, private val cart: Cart) :
    RecyclerView.Adapter<CartAdapter.ViewHolder>() {


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImage: ImageView = itemView.findViewById(R.id.iv_shirts)
        val itemDescription: TextView = itemView.findViewById(R.id.tv_title)
        //added
        val itemDelete: Button = itemView.findViewById(R.id.delete_tv)
        val itemPrice: TextView = itemView.findViewById(R.id.tv_price)
        // added
//        val itemCheckout: Button = itemView.findViewById(R.id.btnCheckout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_cart_product_display_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataClassDashboard = cartItems[position]
        var position = 0
        Picasso.get().load(dataClassDashboard.image).into(holder.itemImage)
        holder.itemDescription.text = dataClassDashboard.title
        holder.itemPrice.text = dataClassDashboard.price.toString()


        holder.itemDelete.setOnClickListener {
            val itemToDelete = cartItems[position]
            cart.deleteCartItem(itemToDelete)
            cartItems.removeAt(position)
            notifyDataSetChanged()
        }

//        holder.itemCheckout.setOnClickListener {
//            val itemToDelete = cartItems[position]
//            while (position < cartItems.size) {
//                cart.deleteCartItem(itemToDelete)
//                cartItems.removeAt(position)
//                notifyDataSetChanged()
//                position++
//            }
//        }

    }
    override fun getItemCount(): Int {
        return cartItems.size
    }


}

