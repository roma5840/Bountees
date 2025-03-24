package com.example.Bountees.Dashboard

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.example.Bountees.R
import com.squareup.picasso.Picasso

class ViewProduct : AppCompatActivity() {


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_product)


        val product = intent.getParcelableExtra<DataClassDashboard>("product")
        if (product !=null){
            val textView : TextView = findViewById(R.id.tv_viewtitle)
            val textView2 : TextView = findViewById(R.id.tv_viewprice)
            val imageView : ImageView = findViewById(R.id.iv_viewproduct)

            textView.text = product.title
            Picasso.get().load(product.image).into(imageView)
            textView2.text = product.price.toString()
            //text currency

        }else{
            Log.e("ViewProduct", "Product object is null")
        }
    }
}