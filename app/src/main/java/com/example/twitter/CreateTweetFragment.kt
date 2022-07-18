package com.example.twitter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
lateinit var userJson: java.io.Serializable
lateinit var user: User
/**
 * A simple [Fragment] subclass.
 * Use the [CreateTweetFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateTweetFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val i: Intent = activity!!.intent
        userJson = i.getSerializableExtra("user")!!
        user = Json.decodeFromString<User>(userJson.toString())

        val view: View =  inflater.inflate(R.layout.fragment_create_tweet, container, false)
        val returnButton: Button = view.findViewById(R.id.return_button)
        val tweetButton: Button =  view.findViewById(R.id.tweet_button)
        var tweetEditText: EditText = view.findViewById(R.id.create_tweet_text)
        tweetButton.setOnClickListener {
            var text = tweetEditText.text.toString()
            CreateTweet(text)
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        }

        returnButton.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        }
        return view
    }

    private fun CreateTweet(text1: String) {
        var db = FirebaseFirestore.getInstance()
        var auth = Firebase.auth
        val sdf = SimpleDateFormat("dd/M/yyyy")
        val currentDate = sdf.format(Date())
        var tweet: Tweet
        var tweets = listOf<String>()
        tweet = Tweet(username = user.username, name = user.name, profilePictureURL = user.profilePictureURL, text = text1,
        datetime = currentDate)
        db.collection("tweets").add(tweet).addOnSuccessListener { documentReference ->
            db.collection("users").document(auth.currentUser!!.uid).get().addOnSuccessListener { value ->
                val user: User? = value.toObject(User::class.java)
                tweets = value.get("tweets") as List<String>
                var temp = tweets.toMutableList()
                temp.add(documentReference.id)
                user!!.tweets = temp.toList()
                db.collection("users").document(auth.currentUser!!.uid).set(user)
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CreateTweetFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic fun newInstance(param1: String, param2: String) =
                CreateTweetFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}