package com.example.autoconnect

import android.os.Bundle
import android.view.View
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

        val buttonConfirmVehicle = findViewById<Button>(R.id.buttonConfirmVehicle)

        buttonSearchVehicle.setOnClickListener {
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
                vehicleInfo = gson.fromJson(responseBody, VehicleInfo::class.java) // Assign to the class property

                // Construct formatted text to display in textViewVehicleDetails
                val formattedText = StringBuilder().apply {
                    append("Make: ${vehicleInfo.make}\n")
                    append("Year of Manufacture: ${vehicleInfo.yearOfManufacture}\n")
                    append("Engine Capacity: ${vehicleInfo.engineCapacity}\n")
                    append("CO2 Emissions: ${vehicleInfo.co2Emissions}\n")
                    append("Fuel Type: ${vehicleInfo.fuelType}\n")
                    append("Tax Status: ${vehicleInfo.taxStatus}\n")
                    append("Tax Due Date: ${vehicleInfo.taxDueDate}\n")
                    append("MOT Status: ${vehicleInfo.motStatus}\n")
                    append("Marked for Export: ${if (vehicleInfo.markedForExport) "Yes" else "No"}\n")
                    append("Colour: ${vehicleInfo.colour}\n")
                    append("Type Approval: ${vehicleInfo.typeApproval}\n")
                    append("Date of Last V5C Issued: ${vehicleInfo.dateOfLastV5CIssued}\n")
                    append("MOT Expiry Date: ${vehicleInfo.motExpiryDate}\n")
                    append("Wheelplan: ${vehicleInfo.wheelplan}\n")
                    append("Month of First Registration: ${vehicleInfo.monthOfFirstRegistration}\n")
                }.toString()


                runOnUiThread {
                    // Update UI with formatted text
                    textViewVehicleDetails.text = formattedText
                    buttonConfirmVehicle.visibility = View.VISIBLE
                }
            }
        }
        buttonConfirmVehicle.setOnClickListener {
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
                    }
                    .addOnFailureListener { e ->
                        // Failed to add vehicle details
                        //Toast.makeText(this, "Failed to add vehicle details: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}



