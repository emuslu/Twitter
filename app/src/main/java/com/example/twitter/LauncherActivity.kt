package com.example.twitter

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class LauncherActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        auth = Firebase.auth
    }
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        var db = FirebaseFirestore.getInstance()
        var auth = Firebase.auth
        var currentUser = auth.currentUser

        if (currentUser == null) {
                val i = Intent(this, WelcomeActivity::class.java)
                startActivity(i)
                finish()
        }
        else
        {
            db.collection("users").document(currentUser.uid).get().addOnSuccessListener { result ->
                var user = loadUser(result)
                var userJson = Json.encodeToString(user)
                val i = Intent(this, MainActivity::class.java).putExtra("user", userJson)
                startActivity(i)
                finish()
            }
        }
    }
    public fun loadUser(snapshot: DocumentSnapshot): User
    {
        var user: User = User(username = snapshot.get("username") as String, name = snapshot.get("name") as String,
            email = snapshot.get("email") as String, following = (snapshot.get("following") as Number).toInt(), followers = (snapshot.get("followers") as Number).toInt(),
            description = snapshot.get("description") as String, profilePictureURL = snapshot.get("profilePictureURL") as String,
            backgroundPictureURL = snapshot.get("backgroundPictureURL") as String, location = snapshot.get("location") as String,
            joinDate = snapshot.get("joinDate") as String, birthDate = snapshot.get("birthDate") as String, tweets = listOf<String>()
        )
        return user
    }
}