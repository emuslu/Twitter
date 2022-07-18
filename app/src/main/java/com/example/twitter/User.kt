package com.example.twitter

import android.location.Location
import android.net.Uri
import com.google.firebase.firestore.DocumentReference
import java.io.Serializable
import java.util.*

@kotlinx.serialization.Serializable
data class User(
    var username: String = "",
    var name: String = "",
    val email: String = "",
    var following: Int = 0,
    var followers: Int = 0,
    var description: String = "",
    var profilePictureURL: String = "",
    var backgroundPictureURL: String = "",
    var location: String = "",
    var joinDate: String = "",
    var birthDate: String = "",
    var tweets: List<String> = listOf<String>()
): Serializable
