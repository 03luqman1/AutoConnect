package com.example.autoconnect.ui.review

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.autoconnect.MainActivity
import com.example.autoconnect.R
import com.example.autoconnect.databinding.FragmentGarageBinding
import com.example.autoconnect.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ReviewFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private var currentToast: Toast? = null

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(com.example.autoconnect.R.layout.fragment_review, container, false)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()



        // Get references to the views
        val ratingBar: RatingBar = view.findViewById(R.id.ratingBar)
        val editTextComments: EditText = view.findViewById(R.id.editTextComments)
        val buttonSubmitReview: Button = view.findViewById(R.id.buttonSubmitReview)

        buttonSubmitReview.setOnClickListener {
            // Get the selected rating and comments
            val rating: Float = ratingBar.rating
            val comments: String = editTextComments.text.toString()

            if (comments.length > 500) {
                showToast("Comments cannot exceed 500 characters.")
            } else {
                // Check if a rating has been selected
                if (rating > 0) {
                    // Add review details to the Realtime Database
                    addReviewToDatabase(rating, comments)

                    val navController = findNavController()
                    navController.navigate(R.id.navigation_home)
                } else {
                    // Display a toast indicating that a rating must be selected
                    showToast("Please select a star rating before submitting your review.")
                }
            }
        }

        return view
    }

    private fun addReviewToDatabase(rating: Float, comments: String) {
        val user = auth.currentUser
        val userId = user?.uid

        userId?.let {
            val database = Firebase.database
            val reviewsRef = database.getReference("Reviews")

            // Create a unique key for each review
            val reviewId = reviewsRef.push().key

            // Create a Review object
            val review = Review(userId, rating, comments)

            // Push the review to the "reviews" node using the unique key as the identifier
            reviewId?.let {
                reviewsRef.child(it).setValue(review)
            }
        }
        showToast("Review Submitted - Thanks for the review!")
    }

    private fun showToast(message: String) {
        currentToast?.cancel()
        currentToast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        currentToast?.show()
    }
}