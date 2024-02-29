package com.example.autoconnect

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.autoconnect.dataclasses.VehicleInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class AddVehicleActivity : AppCompatActivity() {
    private lateinit var editTextVRN: EditText
    private lateinit var buttonSearchVehicle: Button
    private lateinit var textViewVehicleDetails: TextView
    private lateinit var vehicleInfo: VehicleInfo // Declare vehicleInfo here

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_vehicle)

        editTextVRN = findViewById(R.id.editTextVRN2)
        buttonSearchVehicle = findViewById(R.id.buttonSearchVehicle)
        textViewVehicleDetails = findViewById(R.id.textViewVehicleDetails)


        editTextVRN.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called before the text is changed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called when the text is changed

                // If the text is empty or null, hide textViewVehicleDetails and buttonConfirmVehicle
                textViewVehicleDetails.visibility = View.GONE
                buttonSearchVehicle.text = "SEARCH VEHICLE"
            }

            override fun afterTextChanged(s: Editable?) {
                // This method is called after the text is changed
            }
        })


        buttonSearchVehicle.setOnClickListener {
            if (buttonSearchVehicle.text == "SEARCH VEHICLE") {




                val client = OkHttpClient()
                val mediaType = "application/json".toMediaType()
                val vrn = editTextVRN.text.toString() // Get the VRN from editTextVRN
                val body = "{\"registrationNumber\": \"$vrn\"}".toRequestBody(mediaType)
                val request = Request.Builder()
                    .url("https://driver-vehicle-licensing.api.gov.uk/vehicle-enquiry/v1/vehicles")
                    .post(body)
                    .addHeader("x-api-key", "c5jFj6j13r4eNwFSMo706bniWL02zjqaKllvaqA6")
                    .addHeader("Content-Type", "application/json")
                    .build()

                GlobalScope.launch(Dispatchers.IO) {
                    val response = client.newCall(request).execute()
                    val responseBody = response.body?.string()

                    // Parse JSON response using Gson
                    val gson = Gson()
                    vehicleInfo = gson.fromJson(
                        responseBody,
                        VehicleInfo::class.java
                    ) // Assign to the class property


                    runOnUiThread {
                        if (!vehicleInfo.make.isNullOrEmpty()) {
                            // Get the InputMethodManager
                            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

                            // Hide the keyboard
                            imm.hideSoftInputFromWindow(editTextVRN.windowToken, 0)

                            textViewVehicleDetails.text =
                                "${vehicleInfo.yearOfManufacture} ${vehicleInfo.colour} ${vehicleInfo.make}"
                            textViewVehicleDetails.visibility = View.VISIBLE
                            buttonSearchVehicle.text = "CONFIRM VEHICLE"
                            //I NEED A EDIT TEXT LISTENER HEAR FOR editTextVRN. If the edit text is changed then textViewVehicleDetails and buttonConfirmVehicle SHOULD have visibility set to INVISIBLE
                        } else {
                            textViewVehicleDetails.visibility = View.GONE
                            buttonSearchVehicle.text = "SEARCH VEHICLE"
                        }
                    }
                }
            } else if (buttonSearchVehicle.text == "CONFIRM VEHICLE"){


            // Get the VRN
            val vrn = editTextVRN.text.toString()

            // Get the UID of the currently authenticated user
            val uid = FirebaseAuth.getInstance().currentUser?.uid

            // Ensure the user is authenticated
            if (uid != null) {
                // Get a reference to your Firebase database
                val database = FirebaseDatabase.getInstance()

                // Reference to the "vehicles" node under the user's UID
                val vehiclesRef = database.getReference("Users").child(uid).child("Vehicles")

                // Push the vehicle details to the database under the VRN key
                vehiclesRef.child(vrn).setValue(vehicleInfo)
                    .addOnSuccessListener {
                        // Vehicle details added successfully
                        //Toast.makeText(this, "Vehicle details added to database", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, GarageActivity::class.java))
                    }
                    .addOnFailureListener { e ->
                        // Failed to add vehicle details
                        //Toast.makeText(this, "Failed to add vehicle details: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
    }
}



