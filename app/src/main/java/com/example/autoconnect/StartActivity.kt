package com.example.autoconnect

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val buttonGoToLogin = findViewById<Button>(R.id.buttonLogin)
        buttonGoToLogin.setOnClickListener {
            // Handle the button click to go to the Login activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val buttonGoToSignup = findViewById<Button>(R.id.buttonSignUp)
        buttonGoToSignup.setOnClickListener {
            // Handle the button click to go to the Register activity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
    @Suppress("MissingSuperCall")
    override fun onBackPressed() {
        val intent = Intent(this, StartActivity::class.java)
        startActivity(intent)
        finish()
    }
}
