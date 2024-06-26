package com.example.autoconnect.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.autoconnect.admin.AdminActivity
import com.example.autoconnect.MainActivity
import com.example.autoconnect.R
import com.example.autoconnect.UserDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class AddAdminActivity : AppCompatActivity() {

    private lateinit var editTextEmail: EditText
    private lateinit var editTextFullName: EditText
    private lateinit var editTextPhoneNumber: EditText
    private lateinit var editTextUserName: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var usernamesRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_admin)

        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        usernamesRef = database.getReference("Usernames")

        editTextFullName = findViewById(R.id.editTextFullName)
        editTextUserName = findViewById(R.id.editTextUserName)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber)
        editTextPassword = findViewById(R.id.editTextPassword)

        val backButton = findViewById<Button>(R.id.buttonBackFromAddAdmin)
        backButton.setOnClickListener {
            // Handle the button click to go to the AdminActivity
            val intent = Intent(this, AdminActivity::class.java)
            startActivity(intent)
        }

        val addAdmin = findViewById<Button>(R.id.buttonAddAdmin)
        addAdmin.setOnClickListener {

            val fullName = editTextFullName.text.toString().trim()
            val userName = editTextUserName.text.toString().trim()
            val email = editTextEmail.text.toString().trim()
            val phoneNumber = editTextPhoneNumber.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (validateInputs(fullName, userName, email, phoneNumber, password)) {
                // Inputs are valid, check if the username is available
                checkUsernameAvailability(email, password, fullName, userName, phoneNumber)
            }
        }
    }

    private fun validateInputs(
        fullName: String,
        userName: String,
        email: String,
        phoneNumber: String,
        password: String
    ): Boolean {
        // Validate each input field and show error messages if needed
        if (fullName.isEmpty() || fullName.length < 1 || fullName.length > 50) {
            showToast("Please enter a valid full name (1-50 characters).")
            return false
        }

        if (userName.isEmpty() || userName.length < 1 || userName.length > 50) {
            showToast("Please enter a valid username (1-50 characters).")
            return false
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Please enter a valid email address.")
            return false
        }

        if (phoneNumber.isEmpty() || !android.util.Patterns.PHONE.matcher(phoneNumber).matches() || phoneNumber.length > 15) {
            showToast("Please enter a valid phone number.")
            return false
        }

        if (password.length < 8 || password.length > 20 || password.contains(" ")) {
            showToast("Please enter a password between 8 and 20 characters without spaces.")
            return false
        }

        // All inputs are valid
        return true
    }

    private fun checkUsernameAvailability(
        email: String,
        password: String,
        fullName: String,
        userName: String,
        phoneNumber: String
    ) {
        usernamesRef.child(userName).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Username already exists
                    showToast("Username is already taken.")
                } else {
                    // Username is available, register the admin
                    registerAdmin(email, password, fullName, userName, phoneNumber)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Error checking username availability.")
            }
        })
    }

    private fun registerAdmin(
        email: String,
        password: String,
        fullName: String,
        userName: String,
        phoneNumber: String
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registration success
                    val user = firebaseAuth.currentUser
                    addAdminDetailsToDatabase(user, fullName, userName, email, phoneNumber)
                    showToast("Admin Added")
                    startActivity(Intent(this, AdminActivity::class.java))
                    finish()
                } else {
                    // Registration failed
                    showToast("Registration failed. ${task.exception?.message}")
                }
            }
    }

    private fun addAdminDetailsToDatabase(
        user: FirebaseUser?,
        fullName: String,
        userName: String,
        email: String,
        phoneNumber: String
    ) {
        user?.let {
            val adminRef = database.getReference("Admin")
            val userDetails = UserDetails(user.uid, fullName, userName, email, phoneNumber)
            adminRef.child(user.uid).setValue(userDetails)
            addUserToUsernamesNode(userName)
        }
    }

    private fun addUserToUsernamesNode(userName: String) {
        usernamesRef.child(userName).setValue(true)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
