package com.example.twitter

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.w3c.dom.Text
import java.util.concurrent.Executors

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
val executor = Executors.newSingleThreadExecutor()
val handler = Handler(Looper.getMainLooper())
var image: Bitmap? = null

/**
 * A simple [Fragment] subclass.
 * Use the [EditProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditProfileFragment : Fragment() {
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val i: Intent = activity!!.intent
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)
        userJson = i.getSerializableExtra("user")!!
        user = Json.decodeFromString<User>(userJson.toString())

        val editProfileBackground: ImageView = view.findViewById(R.id.edit_profile_background)
        val editProfilePicture = view.findViewById<ImageView>(R.id.edit_profile_picture)
        val editProfileName = view.findViewById<EditText>(R.id.edit_profile_name)
        val editProfileBio = view.findViewById<EditText>(R.id.edit_profile_description)
        val editProfileLocation = view.findViewById<EditText>(R.id.edit_profile_location)

        DownloadImageFromURL(user.backgroundPictureURL, editProfileBackground)
        DownloadImageFromURL(user.profilePictureURL, editProfilePicture)
        editProfileName.setText(user.name)
        editProfileBio.setText(user.description)
        editProfileLocation.setText(user.location)
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
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