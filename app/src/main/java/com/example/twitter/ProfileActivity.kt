package com.example.twitter

import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.parseAsHtml
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.net.URL
import java.util.concurrent.Executors

class ProfileActivity : AppCompatActivity() {

    val executor = Executors.newSingleThreadExecutor()
    val handler = Handler(Looper.getMainLooper())
    var image: Bitmap? = null
    lateinit var userJson:java.io.Serializable
    lateinit var user:User
    private lateinit var tweetRecyclerview : RecyclerView
    private lateinit var dbref : CollectionReference
    lateinit var db: FirebaseFirestore
    private lateinit var tweetArrayList : ArrayList<Tweet>
    lateinit var currentUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        db = FirebaseFirestore.getInstance()
        var auth = Firebase.auth
        currentUser = auth.currentUser!!
        userJson = intent.getSerializableExtra("user")!!
        user = Json.decodeFromString<User>(userJson.toString())

        var editProfileButton: Button = findViewById(R.id.tweet_edit_profile)
        var backgroundPic: ImageView = findViewById(R.id.profile_background_pic)
        var profilePic: de.hdodenhof.circleimageview.CircleImageView =
            findViewById(R.id.profile_pic)
        var profileName: TextView = findViewById(R.id.profile_name)
        var profileUsername: TextView = findViewById(R.id.profile_username)
        var description: TextView = findViewById(R.id.profile_description)
        var location: TextView = findViewById(R.id.profile_location)
        var birthdate: TextView = findViewById(R.id.profile_birthdate)
        var joindate: TextView = findViewById(R.id.profile_join_date)
        var following_follower_count: TextView = findViewById(R.id.profile_following_follower_count)

        DownloadImageFromURL(user.backgroundPictureURL, backgroundPic)
        DownloadImageFromURL(user.profilePictureURL, profilePic)
        profileName.text = user.name
        profileUsername.text = user.username
        description.text = user.description
        location.text = user.location
        birthdate.text = user.birthDate
        joindate.text = user.joinDate
        following_follower_count.text = user.following.toString() + " following " +user.followers.toString() + " followers"

        editProfileButton.setOnClickListener{ view ->
            val mFragmentManager = supportFragmentManager
            val mFragmentTransaction = mFragmentManager.beginTransaction()
            val mFragment = EditProfileFragment()
            mFragmentTransaction.add(R.id.edit_profile_fragment_container, mFragment).commit()
        }
        tweetRecyclerview = findViewById(R.id.profileRecyclerView)
        tweetRecyclerview.layoutManager = LinearLayoutManager(this)
        tweetRecyclerview.setHasFixedSize(true)
        tweetArrayList = arrayListOf<Tweet>()

        getTweetData()
    }
    private fun getTweetData() {
        var tweets_of_user = arrayListOf<String>()
        var tweets = arrayListOf<String>()
        dbref = db.collection("users")
        dbref.document(currentUser.uid).get().addOnSuccessListener { doc ->
            tweets_of_user = doc.get("tweets") as ArrayList<String>
            for ((index, tweet) in tweets_of_user.withIndex())
            {
                tweets.add(tweet.replace("\\s".toRegex(), ""))
            }
            tweetArrayList.clear()
            var dbref2 = db.collection("tweets").get().addOnSuccessListener { result ->
                for (document in result) {
                    if (document.id in tweets)
                    {
                        val tweet = document.toObject(Tweet::class.java)
                        tweetArrayList.add(tweet!!)
                    }
                }
                tweetRecyclerview.adapter = RecyclerAdapter(tweetArrayList)
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
    fun DownloadImageFromURL(url: String, imageView: ImageView)
    {
        executor.execute {

            try {
                val `in` = java.net.URL(url).openStream()
                image = BitmapFactory.decodeStream(`in`)

                // Only for making changes in UI
                handler.post {
                    imageView.setImageBitmap(image)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}