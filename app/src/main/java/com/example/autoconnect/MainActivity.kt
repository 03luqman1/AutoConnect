package com.example.autoconnect

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonGoToMenu: Button = findViewById(R.id.buttonVehicleSearch)
        val buttonLeaveReview: Button = findViewById(R.id.buttonLeaveReview)
        val buttonViewOrders: Button = findViewById(R.id.buttonViewOrders)
        val buttonSignOut: Button = findViewById(R.id.buttonSignOut)

        buttonGoToMenu.setOnClickListener {
            //startActivity(Intent(this, MenuActivity::class.java))
        }

        buttonLeaveReview.setOnClickListener {
            //startActivity(Intent(this, ReviewActivity::class.java))
        }

        buttonViewOrders.setOnClickListener {
            //startActivity(Intent(this, OrdersActivity::class.java))
        }

        buttonSignOut.setOnClickListener {
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
