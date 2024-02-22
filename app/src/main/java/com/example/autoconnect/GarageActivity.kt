package com.example.autoconnect

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.autoconnect.dataclasses.VehicleInfo2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class GarageActivity : AppCompatActivity() {
    private lateinit var listViewVehicles: ListView
    private lateinit var buttonAddNewVehicle: Button
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_garage)

        listViewVehicles = findViewById(R.id.listViewVehicles)
        buttonAddNewVehicle = findViewById(R.id.buttonAddNewVehicle)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().reference

        buttonAddNewVehicle.setOnClickListener {
            startActivity(Intent(this, AddVehicleActivity::class.java))
        }

        // Display the user's vehicles in the ListView
        displayUserVehicles()
    }

    private fun displayUserVehicles() {
        // Get the UID of the currently authenticated user
        val uid = auth.currentUser?.uid

        // Ensure the user is authenticated
        if (uid != null) {
            // Reference to the "vehicles" node under the user's UID
            val vehiclesRef = database.child("Users").child(uid).child("Vehicles")

            // Listen for changes in the vehicles node
            vehiclesRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val vehicleList = mutableListOf<String>()
                    for (vehicleSnapshot in snapshot.children) {
                        val vehicle = vehicleSnapshot.getValue(VehicleInfo2::class.java)
                        vehicle?.let {
                            // Add the registration number of each vehicle to the list
                            vehicleList.add(it.registrationNumber)
                        }
                    }

                    // Create an ArrayAdapter to populate the ListView
                    val adapter = ArrayAdapter(
                        this@GarageActivity,
                        android.R.layout.simple_list_item_1,
                        vehicleList
                    )

                    // Set the adapter to the ListView
                    listViewVehicles.adapter = adapter
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }
    }
}

