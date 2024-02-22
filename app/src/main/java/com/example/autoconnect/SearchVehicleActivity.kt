package com.example.autoconnect

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.autoconnect.dataclasses.VehicleInfo
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class SearchVehicleActivity : AppCompatActivity() {
    private lateinit var editTextVRN: EditText
    private lateinit var buttonSearchVehicle: Button
    private lateinit var textViewVehicleDetails: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_search)

        editTextVRN = findViewById(R.id.editTextVRN)
        buttonSearchVehicle = findViewById(R.id.buttonSearchVehicle)
        textViewVehicleDetails = findViewById(R.id.textViewVehicleDetails)

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
                val vehicleInfo = gson.fromJson(responseBody, VehicleInfo::class.java)

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
                }
            }
        }
    }
}



