package com.example.autoconnect

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.autoconnect.databinding.ActivityRegisterBinding
import com.example.autoconnect.UserDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.textViewSignIn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.buttonRegister.setOnClickListener {
            val fullName = binding.editTextFullName.text.toString().trim()
            val userName = binding.editTextUsername.text.toString().trim()
            val email = binding.editTextEmail.text.toString().trim()
            val phoneNumber = binding.editTextPhoneNumber.text.toString().trim()
            val password = binding.editTextNewPassword.text.toString().trim()

            if (validateInputs(fullName, userName, email, phoneNumber, password)) {
                // Inputs are valid, register the user in Firebase Authentication
                registerUser(email, password, fullName, userName, phoneNumber)
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

    private fun registerUser(
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
                    addUserDetailsToDatabase(user?.uid, fullName, userName, email, phoneNumber)
                    showToast("Registration successful.")
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    // Registration failed
                    showToast("Registration failed. ${task.exception?.message}")
                }
            }
    }

    private fun addUserDetailsToDatabase(
        userId: String?,
        fullName: String,
        userName: String,
        email: String,
        phoneNumber: String
    ) {
        userId?.let {
            val database = FirebaseDatabase.getInstance()
            val usersRef = database.getReference("Users")
            val userDetails = UserDetails("",fullName, userName, email, phoneNumber)
            usersRef.child(userId).setValue(userDetails)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
