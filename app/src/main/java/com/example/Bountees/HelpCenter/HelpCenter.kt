package com.example.Bountees.HelpCenter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.Bountees.R

class HelpCenter : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page_help_center)

        val recyclerView = findViewById<RecyclerView>(R.id.rv_helpCenter)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)





        val titles = arrayListOf(
            "How do I create an account on the application?",
            "How do I search for products in the app?",
            "How do I add products to my shopping cart in the app?",
            "How do I update my account information, such as my email address",
            "Can I change or cancel my order after it's been placed in the app?"
        )

        val description = arrayListOf(
            "To create an account, open the app and click on the \"Sign Up\" or \"Create Account\" button. Follow the on-screen prompts to enter your information, including your email and password. After verifying your email, your account will be set up and ready for use.",
            "To search for products, simply open the app and use the search bar at the top of the screen. Enter keywords, product names and the app will display relevant search results.",
            "To add products to your shopping cart, simply browse the app, and when you find a product you'd like to purchase, click the \"Add to Cart\" button or icon. The item will be added to your cart, and you can continue shopping or proceed to checkout.",
            "You can update your account information by navigating to the \"Profile\" or \"Account Settings\" section within the app. From there, you can edit your personal information, including your email address.",
            "You can typically change or cancel an order, depending on its status."
        )

        val itemsList = ArrayList<DataClassHelpCenter>()
        for (i in titles.indices){
            val item = DataClassHelpCenter(titles[i], description[i])
            itemsList.add(item)
        }

        val myAdapter = HelpCenterAdapter(itemsList)
        recyclerView.adapter = myAdapter
    }
}