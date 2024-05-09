package com.example.autoconnect

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.autoconnect.admin.AdminActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoadingActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        firebaseAuth = FirebaseAuth.getInstance()

    }
    @Suppress("MissingSuperCall")
    override fun onBackPressed() {
        val intent = Intent(this, LoadingActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onStart() {
        super.onStart()

        if(firebaseAuth.currentUser != null){
            val user = firebaseAuth.currentUser
            if (user != null) {
                val userId = user.uid

                // Check the user type (Admin or Customer) based on the user ID
                checkUserType(userId)
            }
        }else{
            val intent = Intent(this, StartActivity::class.java)
            startActivity(intent)
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
                                val intent = Intent(this, StartActivity::class.java)
                                startActivity(intent)
                            }
                        } else {
                            showToast("Error checking customer user type.")
                            val intent = Intent(this, StartActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }
            } else {
                showToast("Error checking admin user type.")
                val intent = Intent(this, StartActivity::class.java)
                startActivity(intent)
            }
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT)
    }
}