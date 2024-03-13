package com.example.autoconnect

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonGoToSearch: Button = findViewById(R.id.buttonVehicleSearch)
        val buttonLeaveReview: Button = findViewById(R.id.buttonLeaveReview)
        val buttonSignOut: Button = findViewById(R.id.buttonSignOut)
        val buttonSocial: Button = findViewById(R.id.buttonSocial)
        val buttonGarage: Button = findViewById(R.id.buttonGarage)

        buttonGoToSearch.setOnClickListener {
            //startActivity(Intent(this, MenuActivity::class.java))
            startActivity(Intent(this, SearchVehicleActivity::class.java))
        }

        buttonSocial.setOnClickListener {
            startActivity(Intent(this, SocialActivity::class.java))
        }

        buttonGarage.setOnClickListener {
            startActivity(Intent(this, GarageActivity::class.java))
        }

        buttonLeaveReview.setOnClickListener {
            startActivity(Intent(this, ReviewActivity::class.java))
        }


        buttonSignOut.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(Intent(this, StartActivity::class.java))
            finish()
        }
    }

    @Suppress("MissingSuperCall")
    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
