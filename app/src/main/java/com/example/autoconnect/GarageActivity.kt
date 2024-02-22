package com.example.autoconnect

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class GarageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_garage)

        val buttonAddNewVehicle: Button = findViewById(R.id.buttonAddNewVehicle)

        buttonAddNewVehicle.setOnClickListener {
            //startActivity(Intent(this, MenuActivity::class.java))
            startActivity(Intent(this, AddVehicleActivity::class.java))
        }
    }
}