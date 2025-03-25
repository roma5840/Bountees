package com.example.Bountees

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.Bountees.AddToCart.DatabaseHelper
import com.example.Bountees.Dashboard.Dashboard
import com.example.Bountees.database.UserDataManager
import com.example.Bountees.databinding.ActivityPageLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.rxbinding2.widget.RxTextView

import android.content.Context
import android.content.SharedPreferences
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.core.view.isVisible
import com.twilio.Twilio
import com.twilio.rest.api.v2010.account.Message
import com.twilio.type.PhoneNumber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import okhttp3.Credentials
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request

import android.view.View

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPageLoginBinding
    private lateinit var userDataManager: UserDataManager

    private lateinit var etPhoneNumber: EditText
    private lateinit var etOTP: EditText
    private lateinit var btnSendOTP: Button
    private lateinit var btnVerifyOTP: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        private const val ACCOUNT_SID = "AC73286e55ef5aed9be22292b420912e26"
        private const val AUTH_TOKEN = "438abb3b080edf73ba6f55bd0791070d"
        private const val TWILIO_PHONE = "+19207813596" // Your Twilio number
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPageLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        userDataManager = UserDataManager(this)



        // Initialize views
        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        etOTP = findViewById(R.id.etOTP)
        btnSendOTP = findViewById(R.id.btnSendOTP)
        btnVerifyOTP = findViewById(R.id.btnVerifyOTP)
        progressBar = findViewById(R.id.progressBar)
        sharedPreferences = getSharedPreferences("OTP_Prefs", Context.MODE_PRIVATE)

        // Send OTP Button Click
        btnSendOTP.setOnClickListener {
            val phoneNumber = etPhoneNumber.text.toString().trim()
            if (phoneNumber.isEmpty()) {
                Toast.makeText(this, "Please enter phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val otp = generateOTP()
            saveOTPToPrefs(otp)
            sendOTPToPhone(phoneNumber, otp)
        }

        // Verify OTP Button Click
        btnVerifyOTP.setOnClickListener {
            val enteredOTP = etOTP.text.toString().trim()
            if (verifyOTP(enteredOTP)) {
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                clearDatabase()
                startActivity(Intent(this, Dashboard::class.java))
                finish()
            } else {
                Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show()
            }
        }









        //  username validation
        val usernameStream = RxTextView.textChanges(binding.loginUsername)
            .skipInitialValue()
            .map { username ->
                username.isEmpty()
            }
        usernameStream.subscribe{
            showTextMinimalAlert(it, "Email")
        }

        //  password validation
        val passwordStream = RxTextView.textChanges(binding.loginPassword)
            .skipInitialValue()
            .map { password ->
                password.isEmpty()
            }
        passwordStream.subscribe{
            showTextMinimalAlert(it, "Password")
        }

        // BUTTON ENABLE TRUE OR FALSE
        val invalidFieldsStream = io.reactivex.Observable.combineLatest(
            usernameStream,
            passwordStream
        ) { usernameInvalid: Boolean, passwordInvalid: Boolean ->
            !usernameInvalid && !passwordInvalid
        }

        invalidFieldsStream.subscribe { isValid ->
            if(isValid) {
                binding.btConfirmLogin.isEnabled = true
                binding.btConfirmLogin.backgroundTintList = ContextCompat.getColorStateList(this, R.color.black)
            } else {
                binding.btConfirmLogin.isEnabled = false
                binding.btConfirmLogin.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.darker_gray)
            }
        }

        binding.btConfirmLogin.setOnClickListener {
            val userName = binding.loginUsername.text.toString()
            val passWord = binding.loginPassword.text.toString()

            if (userName.isNotEmpty() && passWord.isNotEmpty()) {
                userDataManager.loginUser(userName, passWord) { success, message ->
                    if (success) {
                        runOnUiThread {
                            // Show phone number and OTP controls
                            etPhoneNumber.visibility = View.VISIBLE
                            btnSendOTP.visibility = View.VISIBLE

                            // Disable credential fields
                            binding.loginUsername.isEnabled = false
                            binding.loginPassword.isEnabled = false
                            binding.btConfirmLogin.isEnabled = false

                            Toast.makeText(
                                this@LoginActivity,
                                "Enter phone number for OTP verification",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                this@LoginActivity,
                                message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }



        binding.createAccount.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
            finish()
        }
    }

    private fun showTextMinimalAlert(isNotValid: Boolean, text: String) {
        if(text == "Email")
            binding.loginUsername.error = if (isNotValid) "$text required" else null
        else if (text == "Password")
            binding.loginPassword.error = if(isNotValid) "$text required" else null
    }


    private fun clearDatabase() {
        val dbHelper = DatabaseHelper(this)
        val dbTwo = dbHelper.writableDatabase
        dbTwo.execSQL("DELETE FROM cart")
        dbTwo.close()
    }

    private fun generateOTP(length: Int = 6): String {
        val allowedChars = ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }

    private fun saveOTPToPrefs(otp: String) {
        sharedPreferences.edit().putString("stored_otp", otp).apply()
    }

    private fun verifyOTP(inputOTP: String): Boolean {
        val storedOTP = sharedPreferences.getString("stored_otp", "")
        return inputOTP == storedOTP
    }

    private fun sendOTPToPhone(phoneNumber: String, otp: String) {
        progressBar.isVisible = true
        btnSendOTP.isEnabled = false

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = OkHttpClient()

                val formBody = FormBody.Builder()
                    .add("To", phoneNumber)
                    .add("From", TWILIO_PHONE)
                    .add("Body", "Your OTP is: $otp")
                    .build()

                val request = Request.Builder()
                    .url("https://api.twilio.com/2010-04-01/Accounts/$ACCOUNT_SID/Messages.json")
                    .post(formBody)
                    .addHeader("Authorization", Credentials.basic(ACCOUNT_SID, AUTH_TOKEN))
                    .build()

                val response = client.newCall(request).execute()

                runOnUiThread {
                    progressBar.isVisible = false
                    btnSendOTP.isEnabled = true

                    if (response.isSuccessful) {
                        // Show OTP verification controls
                        etOTP.visibility = View.VISIBLE
                        btnVerifyOTP.visibility = View.VISIBLE
                        btnSendOTP.text = "Resend OTP"
                        Toast.makeText(
                            this@LoginActivity,
                            "OTP sent successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }


            } catch (e: Exception) {
                runOnUiThread {
                    progressBar.isVisible = false
                    btnSendOTP.isEnabled = true
                    Toast.makeText(
                        this@LoginActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
