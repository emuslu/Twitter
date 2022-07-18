package com.example.twitter

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [CreateAccountFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateAccountActivity : AppCompatActivity() {
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        val sdf = SimpleDateFormat("dd/M/yyyy")
        val currentDate = sdf.format(Date())

        var auth = Firebase.auth
        var db = FirebaseFirestore.getInstance()
        var createButton = findViewById<Button>(R.id.btn_register)
        createButton.setOnClickListener()
        {
            var et_name = findViewById<EditText>(R.id.et_name)
            var name = et_name.text.toString()
            val et_username = findViewById<EditText>(R.id.et_username)
            var username = et_username.text.toString()
            val et_email = findViewById<EditText>(R.id.et_email)
            var email = et_email.text.toString()
            val et_password = findViewById<EditText>(R.id.et_password)
            var password = et_password.text.toString()

            val user: User = User(username, name, email, profilePictureURL = "https://abs.twimg.com/sticky/default_profile_images/default_profile_400x400.png",
                backgroundPictureURL = "https://browsecat.net/sites/default/files/twitter-background-129345-835071-2349106.png", joinDate = currentDate)
            val userJson = Json.encodeToString(user)
            auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener { res ->
                db.collection("users").document(res.user!!.uid)
                    .set(user)
                    .addOnSuccessListener { documentReference ->
                        Log.d(
                            TAG,
                            "DocumentSnapshot added with ID: " + res.user!!.uid
                        )
                        var i = Intent(this, MainActivity::class.java).putExtra("user", userJson)
                        startActivity(i)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                    }
            }
        }
    }
}
