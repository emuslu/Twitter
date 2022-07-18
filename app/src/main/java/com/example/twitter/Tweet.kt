package com.example.twitter

import com.google.firebase.Timestamp
import com.google.type.DateTime
import java.time.LocalDateTime
import java.util.*

class Tweet (
    val username: String = "",
    val name: String = "",
    val profilePictureURL: String = "",
    val text: String = "",
    val datetime: String = "",
    val num_of_retweets: String = "0",
    val num_of_replies: String = "0",
    val num_of_likes: String = "0"
)