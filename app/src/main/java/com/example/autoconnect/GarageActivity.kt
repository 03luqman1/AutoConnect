package com.example.autoconnect

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.autoconnect.dataclasses.VehicleInfo
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

        listViewVehicles.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                // Get the selected vehicle info
                val selectedVehicle = listViewVehicles.adapter.getItem(position) as VehicleInfo

                // Start DisplayVehicleActivity and pass selected vehicle info
                val intent = Intent(this, DisplayVehicleActivity::class.java).apply {
                    putExtra("vehicle_info", selectedVehicle)
                }
                startActivity(intent)
            }
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
                    val vehicleList = mutableListOf<VehicleInfo>()
                    for (vehicleSnapshot in snapshot.children) {
                        val vehicle = vehicleSnapshot.getValue(VehicleInfo::class.java)
                        vehicle?.let {
                            vehicleList.add(it)
                        }
                    }

                    // Create a custom adapter to populate the ListView
                    val adapter = VehicleAdapter(this@GarageActivity, vehicleList)

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

