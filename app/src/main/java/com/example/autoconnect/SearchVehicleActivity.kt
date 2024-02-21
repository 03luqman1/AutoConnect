package com.example.autoconnect

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
            val body = "{\"registrationNumber\": \"$vrn\"}".toRequestBody(mediaType) // Use the VRN in the request body
            val request = Request.Builder()
                .url("https://driver-vehicle-licensing.api.gov.uk/vehicle-enquiry/v1/vehicles")
                .post(body)
                .addHeader("x-api-key", "c5jFj6j13r4eNwFSMo706bniWL02zjqaKllvaqA6")
                .addHeader("Content-Type", "application/json")
                .build()

            GlobalScope.launch(Dispatchers.IO) {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                runOnUiThread {
                    // Update UI with response data
                    textViewVehicleDetails.text = responseBody
                }
            }
        }
    }
}
