package com.example.autoconnect

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.autoconnect.admin.AdminActivity
import com.example.autoconnect.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        firebaseAuth = FirebaseAuth.getInstance()
        binding.textViewSignUp.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.buttonSignIn.setOnClickListener {
            val email = binding.editTextEmailSignIn.text.toString()
            val pass = binding.editTextPassword.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {

                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        if (user != null) {
                            val userId = user.uid

                            // Check the user type (Admin or Customer) based on the user ID
                            checkUserType(userId)
                        }

                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()

                    }
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()

            }
        }
    }


    private fun checkUserType(userId: String) {
        val adminRef = FirebaseDatabase.getInstance().getReference("Admin").child(userId)
        val customerRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)

        adminRef.get().addOnCompleteListener { adminTask ->
            if (adminTask.isSuccessful) {
                if (adminTask.result != null && adminTask.result.exists()) {
                    Toast.makeText(this, "ADMIN SIGNED IN", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, AdminActivity::class.java)
                    startActivity(intent)
                } else {
                    // user is a customer
                    customerRef.get().addOnCompleteListener { customerTask ->
                        if (customerTask.isSuccessful) {
                            if (customerTask.result != null && customerTask.result.exists()) {
                                Toast.makeText(this, "User Signed In", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                            } else {
                                showToast("User not found in database.")
                            }
                        } else {
                            showToast("Error checking customer user type.")
                        }
                    }
                }
            } else {
                showToast("Error checking admin user type.")
            }
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT)
    }

}