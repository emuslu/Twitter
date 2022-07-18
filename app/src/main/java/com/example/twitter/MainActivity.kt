package com.example.twitter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.view.Menu
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.apphosting.datastore.testing.DatastoreTestTrace
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {
    @SuppressLint("ResourceAsColor")

    val executor = Executors.newSingleThreadExecutor()
    val handler = Handler(Looper.getMainLooper())
    var image: Bitmap? = null

    lateinit var db: FirebaseFirestore
    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var dbref : CollectionReference
    private lateinit var tweetRecyclerview : RecyclerView
    private lateinit var tweetArrayList : ArrayList<Tweet>
    lateinit var userJson:java.io.Serializable
    lateinit var user:User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userJson = intent.getSerializableExtra("user")!!
        user = Json.decodeFromString<User>(userJson.toString())

        db = FirebaseFirestore.getInstance()
        var auth = Firebase.auth
        var currentUser: FirebaseUser? = auth.currentUser

        var toolbar: Toolbar = findViewById(R.id.include)
        var drawerLayout: DrawerLayout = findViewById(R.id.my_drawer_layout)
        var navigationView: NavigationView = findViewById(R.id.navigation)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24)
        supportActionBar?.title = ""

        val createTweetFragment: CreateTweetFragment
        intent.putExtra("user", userJson)
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.show()
        fab.setOnClickListener { view ->
            view.requestFocus()
            val imgr = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imgr.showSoftInput(view, InputMethodManager.SHOW_FORCED)
            val mFragmentManager = supportFragmentManager
            val mFragmentTransaction = mFragmentManager.beginTransaction()
            val mFragment = CreateTweetFragment()
            mFragmentTransaction.add(R.id.fragment_container, mFragment).commit()
        }

        navigationView.setNavigationItemSelectedListener{
            when(it.itemId)
            {
                R.id.nav_profile -> {
                    val i = Intent(this, ProfileActivity::class.java).putExtra("user", userJson)
                    startActivity(i)
                }
            }
            true
        }
        tweetRecyclerview = findViewById(R.id.recyclerView)
        tweetRecyclerview.layoutManager = LinearLayoutManager(this)
        tweetRecyclerview.setHasFixedSize(true)

        tweetArrayList = arrayListOf<Tweet>()
        getTweetData()
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val image:ImageView = findViewById(R.id.drawer_header_profile_pic)
        val header_name: TextView = findViewById(R.id.header_name)
        val header_username: TextView = findViewById(R.id.header_username)
        var header_following_count: TextView = findViewById(R.id.header_following_count)
        var header_follower_count: TextView = findViewById(R.id.header_follower_count)
        DownloadImageFromURL(user.profilePictureURL, image)
        header_name.text = user.name
        header_username.text = user.username
        header_following_count.text = user.following.toString()
        header_follower_count.text = user.followers.toString()
        if (toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }

    private fun getTweetData() {

        dbref = FirebaseFirestore.getInstance().collection("tweets")

        dbref.addSnapshotListener { snapshot, error ->
            if (snapshot != null) {
                tweetArrayList.clear()
                for (tweetSnapshot in snapshot) {

                    val tweet = tweetSnapshot.toObject(Tweet::class.java)
                    tweetArrayList.add(tweet!!)

                }

                tweetRecyclerview.adapter = RecyclerAdapter(tweetArrayList)
            }
        }

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