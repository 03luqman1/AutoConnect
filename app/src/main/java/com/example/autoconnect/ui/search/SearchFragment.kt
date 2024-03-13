package com.example.autoconnect.ui.search

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.autoconnect.R
import com.example.autoconnect.VehicleInfo
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import java.io.IOException
import java.util.concurrent.TimeUnit

class SearchFragment : Fragment() {

    private lateinit var editTextVRN: EditText
    private lateinit var buttonSearchVehicle: Button
    private lateinit var textViewVehicleDetails: TextView
    private lateinit var scrollViewVehicleDetails: ScrollView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)



        editTextVRN = view.findViewById(R.id.editTextVRN)
        buttonSearchVehicle = view.findViewById(R.id.buttonSearchVehicle)
        textViewVehicleDetails = view.findViewById(R.id.textViewVehicleDetails)
        scrollViewVehicleDetails = view.findViewById(R.id.scrollViewVehicleDetails)


        editTextVRN.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                scrollViewVehicleDetails.visibility = View.GONE
                buttonSearchVehicle.text = "SEARCH VEHICLE"
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        buttonSearchVehicle.setOnClickListener {
            if (buttonSearchVehicle.text == "SEARCH VEHICLE") {



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


                    requireActivity().runOnUiThread(Runnable {
                        if (!vehicleInfo.make.isNullOrEmpty()) {
                            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.hideSoftInputFromWindow(editTextVRN.windowToken, 0)
                            textViewVehicleDetails.text = getFormattedVehicleInfo(vehicleInfo)
                            scrollViewVehicleDetails.visibility = View.VISIBLE
                            buttonSearchVehicle.text = "VIEW MOT DETAILS"
                        } else {
                            scrollViewVehicleDetails.visibility = View.GONE
                            buttonSearchVehicle.text = "SEARCH VEHICLE"
                        }
                    })

                }
            }else if (buttonSearchVehicle.text == "VIEW MOT DETAILS") {



                // Inflate the custom popup layout
                val inflater = LayoutInflater.from(requireContext())

                val popupView = inflater.inflate(R.layout.vehicle_mot_dialog, null)

                // Find views in the popup layout
                val textViewMessage = popupView.findViewById<TextView>(R.id.textViewMessage)
                val buttonAction = popupView.findViewById<Button>(R.id.buttonAction)






// Create OkHttpClient instance
                val client = OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS) // Set timeout if needed
                    .readTimeout(30, TimeUnit.SECONDS)    // Set timeout if needed
                    .build()

// Define the request
                val request = Request.Builder()
                    .url("https://beta.check-mot.service.gov.uk/trade/vehicles/mot-tests?registration=${editTextVRN.text}")
                    .addHeader("Accept", "application/json+v6")
                    .addHeader("x-api-key", "Inj3f7O42k59a35bVEk915UiGuwEEjwu4N5dinQL")
                    .addHeader("Cookie", "incap_ses_1184_1151098=itS6XtMCMUaXg5VCHmpuELwx32UAAAAAKyIy9B1xRP4sWGzRiHnDrA==; nlbi_1151098=SOXfUQF6iRyqGoTVsRy5CgAAAADFLSJLw2S1NE+mACTWYWfR; visid_incap_1151098=CnGlY4ygRb6EhBBj29MO5sgw32UAAAAAQUIPAAAAAAD1F/Q1H/OQRROuDR9AzOfw")
                    .build()

// Make the API call asynchronously
                client.newCall(request).enqueue(object : okhttp3.Callback {
                    override fun onFailure(call: okhttp3.Call, e: IOException) {
                        // Handle failure, e.g., show error message
                        e.printStackTrace()
                    }

                    override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                        // Check if the response is successful
                        if (!response.isSuccessful) {
                            // Handle unsuccessful response, e.g., show error message
                            println("Error: ${response.code}")
                            return
                        }

                        // Get the response body as a string
                        val responseBody = response.body?.string()

                        // Update UI on the main thread
                        requireActivity().runOnUiThread {
                            if (!responseBody.isNullOrEmpty()) {
                                // Parse the JSON response
                                val jsonArray = JSONArray(responseBody)

                                if (jsonArray.length() > 0) {
                                    val jsonObject = jsonArray.getJSONObject(0)
                                    if (!jsonObject.has("motTestDueDate")) {
                                        val registration = jsonObject.getString("registration")
                                        val make = jsonObject.getString("make")
                                        val model = jsonObject.getString("model")
                                        val firstUsedDate = jsonObject.getString("firstUsedDate")
                                        val fuelType = jsonObject.getString("fuelType")
                                        val primaryColour = jsonObject.getString("primaryColour")
                                        val vehicleId = jsonObject.getString("vehicleId")
                                        val registrationDate = jsonObject.getString("registrationDate")
                                        val manufactureDate = jsonObject.getString("manufactureDate")
                                        val engineSize = jsonObject.getString("engineSize")


                                        val motTests = jsonObject.getJSONArray("motTests")

                                        val formattedText = StringBuilder()
                                        formattedText.append("Registration: $registration\n")
                                        formattedText.append("Make: $make\n")
                                        formattedText.append("Model: $model\n")
                                        formattedText.append("First Used Date: $firstUsedDate\n")
                                        formattedText.append("Fuel Type: $fuelType\n")
                                        formattedText.append("Primary Colour: $primaryColour\n")
                                        formattedText.append("Vehicle Id : $vehicleId\n")
                                        formattedText.append("Registration Date : $registrationDate\n")
                                        formattedText.append("Manufacture Date : $manufactureDate\n")
                                        formattedText.append("Engine Size : $engineSize\n\n")

                                        for (i in 0 until motTests.length()) {
                                            val testObj = motTests.getJSONObject(i)
                                            val testResult = testObj.getString("testResult")
                                            val completedDate = testObj.getString("completedDate")
                                            val odometerValue = testObj.getString("odometerValue")
                                            val odometerUnit = testObj.getString("odometerUnit")
                                            val odometerResultType =
                                                testObj.getString("odometerResultType").lowercase()
                                            val motTestNumber = testObj.getString("motTestNumber")
                                            val rfrAndComments = testObj.getJSONArray("rfrAndComments")

                                            if (testResult.equals("PASSED")) {
                                                val expiryDate = testObj.getString("expiryDate")
                                                formattedText.append("\nMOT Test " + (motTests.length() - i) + ":\n")
                                                formattedText.append("Completed Date: $completedDate\n")
                                                formattedText.append("Test Result: $testResult\n")
                                                formattedText.append("Expiry Date: $expiryDate\n")
                                                formattedText.append("Odometer Value: $odometerValue $odometerUnit ($odometerResultType)\n")
                                                formattedText.append("MOT Test Number: $motTestNumber\n")
                                            } else if (testResult.equals("FAILED")) {
                                                formattedText.append("\nMOT Test " + (motTests.length() - i) + ":\n")
                                                formattedText.append("Completed Date: $completedDate\n")
                                                formattedText.append("Test Result: $testResult\n")
                                                formattedText.append("Odometer Value: $odometerValue $odometerUnit ($odometerResultType)\n")
                                                formattedText.append("MOT Test Number: $motTestNumber\n")
                                            }
                                            if (rfrAndComments.length() > 0) {
                                                formattedText.append("RFR and Comments:\n")
                                                for (j in 0 until rfrAndComments.length()) {
                                                    val rfrObj = rfrAndComments.getJSONObject(j)
                                                    val text = rfrObj.getString("text")
                                                    formattedText.append("- $text\n")
                                                }
                                            }

                                            formattedText.append("\n")
                                        }

                                        // Update the TextView with the formatted text
                                        textViewMessage.text = formattedText.toString()


                                    } else {
                                        val registration = jsonObject.getString("registration")
                                        val make = jsonObject.getString("make")
                                        val model = jsonObject.getString("model")
                                        val manufactureYear = jsonObject.getString("manufactureYear")
                                        val fuelType = jsonObject.getString("fuelType")
                                        val primaryColour = jsonObject.getString("primaryColour")
                                        val dvlaId = jsonObject.getString("dvlaId")
                                        val registrationDate = jsonObject.getString("registrationDate")
                                        val manufactureDate = jsonObject.getString("manufactureDate")
                                        val engineSize = jsonObject.getString("engineSize")
                                        val motTestDueDate = jsonObject.getString("motTestDueDate")

                                        val formattedText = StringBuilder()
                                        formattedText.append("Registration: $registration\n")
                                        formattedText.append("Make: $make\n")
                                        formattedText.append("Model: $model\n")
                                        formattedText.append("First Used Date: $manufactureYear\n")
                                        formattedText.append("Fuel Type: $fuelType\n")
                                        formattedText.append("Primary Colour: $primaryColour\n")
                                        formattedText.append("Vehicle Id : $dvlaId\n")
                                        formattedText.append("Registration Date : $registrationDate\n")
                                        formattedText.append("Manufacture Date : $manufactureDate\n")
                                        formattedText.append("Engine Size : $engineSize\n")
                                        formattedText.append("MOT Test Due Date : $motTestDueDate\n\n")

                                        textViewMessage.text = formattedText.toString()
                                    }
                                } else {
                                    textViewMessage.text = "No data available"
                                }
                            } else {
                                textViewMessage.text = "No data available"
                            }
                        }
                    }
                })































                // Set text and click listener for the button
                //textViewMessage.text = "Your custom message here"


                // Create and configure the popup window
                val popupWindow = PopupWindow(
                    popupView,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    true
                )
                buttonAction.setOnClickListener {
                    // Handle button click
                    // For example, dismiss the popup
                    popupWindow.dismiss()
                }
                // Show the popup at the center of the screen
                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)




            }
        }

        return view
    }

    private fun searchVehicle() {
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

            requireActivity().runOnUiThread {
                if (!vehicleInfo.make.isNullOrEmpty()) {
                    val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(editTextVRN.windowToken, 0)
                    textViewVehicleDetails.text = getFormattedVehicleInfo(vehicleInfo)
                    scrollViewVehicleDetails.visibility = View.VISIBLE
                    buttonSearchVehicle.text = "VIEW MOT DETAILS"
                } else {
                    scrollViewVehicleDetails.visibility = View.GONE
                    buttonSearchVehicle.text = "SEARCH VEHICLE"
                }
            }
        }
    }

    private fun getFormattedVehicleInfo(vehicleInfo: VehicleInfo): String {
        return StringBuilder().apply {
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
    }

    private fun showMOTDetailsPopup() {
        // Your MOT details popup logic here
    }
}
