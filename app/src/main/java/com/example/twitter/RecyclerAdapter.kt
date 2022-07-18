package com.example.twitter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.type.DateTime
import java.text.FieldPosition
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.Executors
import kotlin.collections.ArrayList

class RecyclerAdapter(private val tweetArrayList: ArrayList<Tweet>): RecyclerView.Adapter<RecyclerAdapter.ViewHolder>()
{

    val executor = Executors.newSingleThreadExecutor()
    val handler = Handler(Looper.getMainLooper())
    var image: Bitmap? = null
    val id = Firebase.auth.currentUser?.uid

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder{
        val v = LayoutInflater.from(parent.context).inflate(R.layout.tweet, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return tweetArrayList.size
    }

    override fun onBindViewHolder(holder: RecyclerAdapter.ViewHolder, position: Int){

        var tweet: Tweet = tweetArrayList.get(position)

        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())

        DownloadImageFromURL(tweet.profilePictureURL, holder.tweetProfilePic)
        holder.tweetName.text = tweet.name
        holder.tweetUsername.text = tweet.username
        holder.tweetPassedTime.text = tweet.datetime.toString()
        holder.tweetText.text = tweet.text
        holder.tweetNumOfReplies.text = tweet.num_of_replies
        holder.tweetNumOfRetweets.text = tweet.num_of_retweets
        holder.tweetNumOfLikes.text = tweet.num_of_likes
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var tweetProfilePic: ImageView
        var tweetName: TextView
        var tweetUsername: TextView
        var tweetPassedTime: TextView
        var tweetText: TextView
        var tweetNumOfReplies: TextView
        var tweetNumOfRetweets: TextView
        var tweetNumOfLikes: TextView

        init {

            tweetProfilePic = itemView.findViewById(R.id.tweet_profile_pic)
            tweetName = itemView.findViewById(R.id.tweet_name)
            tweetUsername = itemView.findViewById(R.id.tweet_username)
            tweetPassedTime = itemView.findViewById(R.id.tweet_passed_time)
            tweetText = itemView.findViewById(R.id.tweet_text)
            tweetNumOfReplies = itemView.findViewById(R.id.tweet_num_of_replies)
            tweetNumOfRetweets = itemView.findViewById(R.id.tweet_num_of_retweets)
            tweetNumOfLikes = itemView.findViewById(R.id.tweet_num_of_likes)

            itemView.setOnClickListener {
                val position: Int = absoluteAdapterPosition
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