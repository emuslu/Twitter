package com.example.twitter

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class WelcomeActivity : AppCompatActivity() {
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.welcome_page)

        var toolbar:androidx.appcompat.widget.Toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.default_toolbar)
        toolbar.setTitle("")
        setSupportActionBar(toolbar)

        val login_with_google_button = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.continue_with_google)
        val create_account_button = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.create_an_account)
        create_account_button.setOnClickListener{
            goToCreateAccount()
        }
        login_with_google_button.setOnClickListener{
            var i = Intent(this, MainActivity::class.java)
            startActivity(i)
            finish()
        }

    }

    private fun goToCreateAccount() {
        var i = Intent(this, CreateAccountActivity::class.java)
        startActivity(i)
        finish()
    }

}