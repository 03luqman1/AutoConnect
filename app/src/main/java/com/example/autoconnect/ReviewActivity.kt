package com.example.autoconnect

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.autoconnect.dataclasses.Review
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ReviewActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var currentToast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()



        // Get references to the views
        val ratingBar: RatingBar = findViewById(R.id.ratingBar)
        val editTextComments: EditText = findViewById(R.id.editTextComments)
        val buttonSubmitReview: Button = findViewById(R.id.buttonSubmitReview)

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

                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    // Display a toast indicating that a rating must be selected
                    showToast("Please select a star rating before submitting your review.")
                }
            }
        }

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
        currentToast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        currentToast?.show()
    }
}