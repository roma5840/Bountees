package com.example.Bountees.SplashScreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.Bountees.Comments
import com.example.Bountees.Dashboard.Dashboard
import com.example.Bountees.LoginActivity
import com.example.Bountees.MyApi
import com.example.Bountees.R
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SplashScreen : AppCompatActivity() {
    private val SPLASHTIME: Long = 3000
    private val BASE_URL = "https://com-example-bountees-default-rtdb.asia-southeast1.firebasedatabase.app/"
    private val TAG: String = "CHECK_RESPONSE"
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page_splash_screen)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        Handler(Looper.getMainLooper()).postDelayed({
            // Check if user is signed in
            val currentUser = auth.currentUser
            if (currentUser != null) {
                // User is already signed in, go to dashboard
                startActivity(Intent(this, Dashboard::class.java))
                Log.d("Firebase", "User already logged in: ${currentUser.email}")
            } else {
                // No user is signed in, go to login page
                startActivity(Intent(this, LoginActivity::class.java))
                Log.d("Firebase", "No user logged in, redirecting to login")
            }
            finish()
        }, SPLASHTIME)

        // Your commented out API call
        // getAllComments()
    }

    // Keep any other methods you had in your original SplashScreen class
}