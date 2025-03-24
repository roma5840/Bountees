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

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPageLoginBinding
    private lateinit var userDataManager: UserDataManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPageLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        userDataManager = UserDataManager(this)

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
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, Dashboard::class.java)
                        clearDatabase()
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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
}
