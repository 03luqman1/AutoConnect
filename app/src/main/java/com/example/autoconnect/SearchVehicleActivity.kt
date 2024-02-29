package com.example.autoconnect

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
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


        editTextVRN.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called before the text is changed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called when the text is changed

                // If the text is empty or null, hide textViewVehicleDetails and buttonConfirmVehicle
                textViewVehicleDetails.visibility = View.INVISIBLE
            }

            override fun afterTextChanged(s: Editable?) {
                // This method is called after the text is changed
            }
        })




        buttonSearchVehicle.setOnClickListener {

            val client = OkHttpClient()
            val mediaType = "application/json".toMediaType()
            val vrn = editTextVRN.text.toString()
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
                    if (!vehicleInfo.make.isNullOrEmpty()){
                        textViewVehicleDetails.text = formattedText
                        textViewVehicleDetails.visibility = View.VISIBLE

                        //I NEED A EDIT TEXT LISTENER HEAR FOR editTextVRN. If the edit text is changed then textViewVehicleDetails and buttonConfirmVehicle SHOULD have visibility set to INVISIBLE
                    }else{
                        textViewVehicleDetails.visibility = View.INVISIBLE

                    }

                }
            }
        }
    }
}



