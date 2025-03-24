package com.example.Bountees.Dashboard

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ViewFlipper
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.Bountees.AddToCart.Cart
import com.example.Bountees.AddToCart.CartActivity
import com.example.Bountees.AddToCart.DataClassNew
import com.example.Bountees.AddToCart.ItemAdapter


import com.example.Bountees.Product.DashboardAdapter
import com.example.Bountees.Profile.ProfilePage
import com.example.Bountees.R
import com.squareup.picasso.Picasso

class Dashboard : AppCompatActivity() {
    private var recyclerView: RecyclerView? = null
    private var productRecyclerviewAdapter: DashboardAdapter? = null


    private var productList = mutableListOf<DataClassDashboard>()
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page_dashboard)


        val profileButton = findViewById<ImageView>(R.id.iv_profile)
        val cartButton = findViewById<ImageView>(R.id.iv_cart)

        profileButton.setOnClickListener {
            val intent = Intent(this, ProfilePage::class.java)
            startActivity(intent)
            finish()
        }
        cartButton.setOnClickListener {
            val intent = Intent (this, CartActivity::class.java)
            startActivity(intent)
        }


        productList = ArrayList()
        val items = mutableListOf(
            DataClassNew("RSO","https://kind-lime-slug-tux.cyclic.app/api/v1/product/product-photo/65f8071fcc972dd5a2b7f0b3",200),
            DataClassNew("PE","https://kind-lime-slug-tux.cyclic.app/api/v1/product/product-photo/65f7e366d2e39c555b2dba62",150),
            DataClassNew("Two Piece Swimsuit","https://kind-lime-slug-tux.cyclic.app/api/v1/product/product-photo/65f1b16617a64db2321a2fcb",79),
            DataClassNew("Jacket","https://kind-lime-slug-tux.cyclic.app/api/v1/product/product-photo/65f1b01e17a64db2321a2fc3",50),
            DataClassNew("Women's Short","https://kind-lime-slug-tux.cyclic.app/api/v1/product/product-photo/65e16a56e44249c1e7bf84f3",45),
            DataClassNew("Women's Short","https://kind-lime-slug-tux.cyclic.app/api/v1/product/product-photo/65e16a35e44249c1e7bf84ea",19)
        )
        recyclerView = findViewById<View>(R.id.rv_dashboard) as RecyclerView
        this.productRecyclerviewAdapter = DashboardAdapter(this@Dashboard, productList)
        val layoutManager : RecyclerView.LayoutManager = GridLayoutManager(this,2)
        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.adapter = productRecyclerviewAdapter
        val cart = Cart(this)
        val adapter = ItemAdapter (items,cart)

        recyclerView!!.adapter = adapter


//        val recyclerViewNew = findViewById<RecyclerView>(R.id.rv_dashboard)
//        val adapter = ItemAdapter (items,cart)
//        recyclerViewNew.layoutManager = LinearLayoutManager(this)
//        recyclerViewNew.adapter = adapter

        val viewFlipper = findViewById<ViewFlipper>(R.id.view_flipper)

        val imageURLs = listOf(
            "https://kind-lime-slug-tux.cyclic.app/api/v1/product/product-photo/65f8071fcc972dd5a2b7f0b3",
            "https://kind-lime-slug-tux.cyclic.app/api/v1/product/product-photo/65f7e366d2e39c555b2dba62",
            "https://kind-lime-slug-tux.cyclic.app/api/v1/product/product-photo/65f1b16617a64db2321a2fcb"
        )

        for (imageUrl in imageURLs) {
            val imageView = ImageView(this)
            Picasso.get().load(imageUrl).into(imageView)
            viewFlipper.addView(imageView)
        }

        viewFlipper.startFlipping()



        productRecyclerviewAdapter!!.onItemClick = {  item ->
            val intent = Intent(this, ViewProduct::class.java)
            intent.putExtra("product", item)
            startActivity(intent)
        }
        prepareProductListData()
    }


    private fun prepareProductListData() {
        var shirt = DataClassDashboard("RSO","https://kind-lime-slug-tux.cyclic.app/api/v1/product/product-photo/65f8071fcc972dd5a2b7f0b3",200, "PHP")
        productList.add(shirt)
        shirt = DataClassDashboard("PE","https://kind-lime-slug-tux.cyclic.app/api/v1/product/product-photo/65f7e366d2e39c555b2dba62",150, "PHP")
        productList.add(shirt)
        shirt = DataClassDashboard("Two Piece Swimsuit","https://kind-lime-slug-tux.cyclic.app/api/v1/product/product-photo/65f1b16617a64db2321a2fcb",79, "PHP")
        productList.add(shirt)
        shirt = DataClassDashboard("Jacket","https://kind-lime-slug-tux.cyclic.app/api/v1/product/product-photo/65f1b01e17a64db2321a2fc3",50, "PHP")
        productList.add(shirt)
        shirt = DataClassDashboard("Women's Short","https://kind-lime-slug-tux.cyclic.app/api/v1/product/product-photo/65e16a56e44249c1e7bf84f3",45, "PHP")
        productList.add(shirt)
        shirt = DataClassDashboard("Women's Short","https://kind-lime-slug-tux.cyclic.app/api/v1/product/product-photo/65e16a35e44249c1e7bf84ea",19,"PHP")
        productList.add(shirt)


        productRecyclerviewAdapter!!.notifyDataSetChanged()
    }
}