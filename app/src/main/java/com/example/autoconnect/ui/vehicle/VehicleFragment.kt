package com.example.autoconnect.ui.vehicle

// VehicleFragment.kt

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.autoconnect.R
import com.example.autoconnect.VehicleInfo
import com.example.autoconnect.databinding.FragmentGarageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit


class VehicleFragment : Fragment() {

    private var _binding: FragmentGarageBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        val vehicleInfo = requireArguments().getSerializable("vehicleInfo") as VehicleInfo

        // Inflate the layout for this fragment
        val view = inflater.inflate(com.example.autoconnect.R.layout.fragment_vehicle, container, false)

        // Populate the UI components with the vehicle details
        val textViewRegistrationNumber = view.findViewById<TextView>(R.id.textViewRegistrationNumber)
        textViewRegistrationNumber.text = "${vehicleInfo.registrationNumber}"

        val taxStatus = view.findViewById<ImageView>(R.id.taxImageView)
        // Check if the tax status is "Valid" and set the text accordingly
        if (vehicleInfo.taxStatus == "Taxed") {
            taxStatus.setImageResource(R.drawable.green_tick)
            val textViewTaxDueDate = view.findViewById<TextView>(R.id.textViewTaxDueDate)
            textViewTaxDueDate.text = "Tax Due Date: ${vehicleInfo.taxDueDate}"
        } else if (vehicleInfo.taxStatus == "Untaxed" || vehicleInfo.taxStatus == "SORN"){
            taxStatus.setImageResource(R.drawable.red_cross)
            val textViewTaxDueDate = view.findViewById<TextView>(R.id.textViewTaxDueDate)
            textViewTaxDueDate.text = "Tax Status: ${vehicleInfo.taxStatus}"
        }

        val motStatus = view.findViewById<ImageView>(R.id.motImageView)
        // Check if the tax status is "Valid" and set the text accordingly
        if (vehicleInfo.motStatus == "Valid") {
            motStatus.setImageResource(R.drawable.green_tick)
            val textViewMOTExpiryDate = view.findViewById<TextView>(R.id.textViewMOTExpiryDate)
            textViewMOTExpiryDate.text = "MOT Expiry Date: ${vehicleInfo.motExpiryDate}"
        } else if (vehicleInfo.motStatus == "No details held by DVLA"){
            motStatus.setImageResource(R.drawable.green_tick)
            val textViewMOTExpiryDate = view.findViewById<TextView>(R.id.textViewMOTExpiryDate)
            val formatter = SimpleDateFormat("yyyy-MM", Locale.getDefault())
            val firstRegDate = Calendar.getInstance()
            firstRegDate.time = formatter.parse(vehicleInfo.monthOfFirstRegistration)
            firstRegDate.add(Calendar.YEAR, 3)
            val formattedExpiryDate = formatter.format(firstRegDate.time)
            textViewMOTExpiryDate.text = "MOT Due Date: $formattedExpiryDate"
        }else if (vehicleInfo.motStatus == "Not valid"){
            motStatus.setImageResource(R.drawable.red_cross)
            val textViewMOTExpiryDate = view.findViewById<TextView>(R.id.textViewMOTExpiryDate)
            textViewMOTExpiryDate.text = "MOT Status: ${vehicleInfo.motStatus}"
        }


        val textViewMake = view.findViewById<TextView>(R.id.textViewMake)
        textViewMake.text = "Make: ${vehicleInfo.make}"

        val textViewYearOfManufacture = view.findViewById<TextView>(R.id.textViewYearOfManufacture)
        textViewYearOfManufacture.text = "Year of Manufacture: ${vehicleInfo.yearOfManufacture}"

        val textViewEngineCapacity = view.findViewById<TextView>(R.id.textViewEngineCapacity)
        textViewEngineCapacity.text = "Engine Capacity: ${vehicleInfo.engineCapacity}"

        val textViewCO2Emissions = view.findViewById<TextView>(R.id.textViewCO2Emissions)
        textViewCO2Emissions.text = "CO2 Emissions: ${vehicleInfo.co2Emissions}"

        val textViewFuelType = view.findViewById<TextView>(R.id.textViewFuelType)
        textViewFuelType.text = "Fuel Type: ${vehicleInfo.fuelType}"

        val textViewMarkedForExport = view.findViewById<TextView>(R.id.textViewMarkedForExport)
        textViewMarkedForExport.text = "Marked for Export: ${vehicleInfo.markedForExport}"

        val textViewColour = view.findViewById<TextView>(R.id.textViewColour)
        textViewColour.text = "Colour: ${vehicleInfo.colour}"

        val textViewTypeApproval = view.findViewById<TextView>(R.id.textViewTypeApproval)
        textViewTypeApproval.text = "Type Approval: ${vehicleInfo.typeApproval}"

        val textViewDateOfLastV5CIssued = view.findViewById<TextView>(R.id.textViewDateOfLastV5CIssued)
        textViewDateOfLastV5CIssued.text = "Date of Last V5C Issued: ${vehicleInfo.dateOfLastV5CIssued}"

        val textViewWheelplan = view.findViewById<TextView>(R.id.textViewWheelplan)
        textViewWheelplan.text = "Wheelplan: ${vehicleInfo.wheelplan}"

        val textViewMonthOfFirstRegistration = view.findViewById<TextView>(R.id.textViewMonthOfFirstRegistration)
        textViewMonthOfFirstRegistration.text = "Month Of First Registration: ${vehicleInfo.monthOfFirstRegistration}"

        // Set the vehicle make logo
        setVehicleMakeLogo(vehicleInfo.make, view)


        val garageButton = view.findViewById<Button>(R.id.buttonGarage)
        garageButton.setOnClickListener {
            val navController = Navigation.findNavController(view)
            navController.navigate(R.id.navigation_garage)

            // Remove the previous fragment from the back stack
            //navController.popBackStack()

        }

        val removeVehicleButton = view.findViewById<Button>(R.id.buttonRemoveVehicle)
        removeVehicleButton.setOnClickListener {
            // Inflate the confirmation pop-up layout
            val inflater = LayoutInflater.from(context)
            val popupView = inflater.inflate(R.layout.confirm_delete_dialog, null)

            // Create and configure the PopupWindow
            val popupWindow = PopupWindow(
                popupView,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                true
            )

            // Show the pop-up at the center of the screen
            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)

            // Find cancel and confirm delete buttons in the pop-up layout
            val btnCancel = popupView.findViewById<Button>(R.id.btnCancel)
            val btnConfirmDelete = popupView.findViewById<Button>(R.id.btnConfirmDelete)

            // Set click listener for the cancel button
            btnCancel.setOnClickListener {
                // Dismiss the pop-up
                popupWindow.dismiss()
            }

            // Set click listener for the confirm delete button
            btnConfirmDelete.setOnClickListener {
                // Get a reference to your Firebase database
                val database = FirebaseDatabase.getInstance()

                // Get the UID of the currently authenticated user
                val uid = FirebaseAuth.getInstance().currentUser?.uid

                // Ensure the user is authenticated
                if (uid != null) {
                    // Reference to the "vehicles" node under the user's UID
                    val vehiclesRef = database.getReference("Users").child(uid).child("Vehicles")

                    // Remove the vehicle from the database
                    vehiclesRef.child(vehicleInfo.registrationNumber).removeValue()
                        .addOnSuccessListener {
                            // Vehicle removed successfully
                            Toast.makeText(context, "Vehicle removed from database", Toast.LENGTH_SHORT).show()
                            //startActivity(Intent(this, GarageActivity::class.java))
                            //finish()
                            val navController = Navigation.findNavController(view)
                            navController.navigate(R.id.navigation_garage)
                        }
                        .addOnFailureListener { e ->
                            // Failed to remove vehicle
                            Toast.makeText(context, "Failed to remove vehicle: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }

                // Dismiss the pop-up after confirming deletion
                popupWindow.dismiss()
            }
        }






        updateInsuraceStatus(vehicleInfo, view)
        updateServiceStatus(vehicleInfo, view)




        val taxRelativeLayout = view.findViewById<RelativeLayout>(R.id.taxRelativeLayout)
        taxRelativeLayout.setOnClickListener {
            // Inflate the custom popup layout
            val inflater = LayoutInflater.from(context)
            val popupView = inflater.inflate(R.layout.vehicle_tax_dialog, null)

            // Find views in the popup layout
            val textViewMessage = popupView.findViewById<TextView>(R.id.textViewMessageTax)
            val buttonAction = popupView.findViewById<Button>(R.id.buttonActionTax)

            // Create and configure the popup window
            val popupWindow = PopupWindow(
                popupView,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                true
            )

            textViewMessage.text = "VEHICLE TAX DETAILS\n\nTax status : ${vehicleInfo.taxStatus}\nTax due date : ${vehicleInfo.taxDueDate}"
            buttonAction.setOnClickListener {
                // Handle button click
                // For example, dismiss the popup
                popupWindow.dismiss()
            }
            // Show the popup at the center of the screen
            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)

        }



        val motRelativeLayout = view.findViewById<RelativeLayout>(R.id.motRelativeLayout)
        motRelativeLayout.setOnClickListener {
            // Inflate the custom popup layout
            val inflater = LayoutInflater.from(context)
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
                .url("https://beta.check-mot.service.gov.uk/trade/vehicles/mot-tests?registration=${vehicleInfo.registrationNumber}")
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



        val insuranceRelativeLayout = view.findViewById<RelativeLayout>(R.id.insuranceRelativeLayout)
        insuranceRelativeLayout.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = Dialog(requireContext())
            datePickerDialog.setContentView(R.layout.custom_date_picker_dialog)

            val datePicker = datePickerDialog.findViewById<DatePicker>(R.id.datePicker)
            val cancelButton = datePickerDialog.findViewById<Button>(R.id.btnCancel)
            val okButton = datePickerDialog.findViewById<Button>(R.id.btnOk)
            val expiredButton = datePickerDialog.findViewById<Button>(R.id.btnExpired)
            val addYearButton = datePickerDialog.findViewById<Button>(R.id.btnAddYear)

            datePicker.minDate = System.currentTimeMillis() - 1000

            cancelButton.setOnClickListener {
                datePickerDialog.dismiss()
                updateInsuraceStatus(vehicleInfo, view)
            }

            okButton.setOnClickListener {
                val year = datePicker.year
                val month = datePicker.month + 1
                val dayOfMonth = datePicker.dayOfMonth
                val insuranceExpiryDate = view.findViewById<TextView>(R.id.textViewInsuranceExpiry)
                insuranceExpiryDate.text = "Expires: $year-$month-$dayOfMonth"
                datePickerDialog.dismiss()
                vehicleInfo.insuranceExpiry = "$year-$month-$dayOfMonth"
                updateInsuraceStatus(vehicleInfo, view)
                saveChanges(vehicleInfo)
            }

            expiredButton.setOnClickListener {
                val insuranceExpiryDate = view.findViewById<TextView>(R.id.textViewInsuranceExpiry)
                insuranceExpiryDate.text = "No Insurance"
                datePickerDialog.dismiss()
                vehicleInfo.insuranceExpiry = "No Insurance"
                updateInsuraceStatus(vehicleInfo, view)
                saveChanges(vehicleInfo)
            }

            addYearButton.setOnClickListener {
                val year = datePicker.year
                val month = datePicker.month
                val dayOfMonth = datePicker.dayOfMonth

                // Create a Calendar instance and set it to the selected date
                val calendar = Calendar.getInstance()
                calendar.set(year, month, dayOfMonth)

                // Add 1 year to the selected date
                calendar.add(Calendar.YEAR, 1)

                // Update the DatePicker with the new date
                datePicker.updateDate(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                val year1 = datePicker.year
                val month1 = datePicker.month + 1
                val dayOfMonth1 = datePicker.dayOfMonth
                val insuranceExpiryDate = view.findViewById<TextView>(R.id.textViewInsuranceExpiry)
                insuranceExpiryDate.text = "Expires: $year1-$month1-$dayOfMonth1"
                datePickerDialog.dismiss()
                vehicleInfo.insuranceExpiry = "$year1-$month1-$dayOfMonth1"
                updateInsuraceStatus(vehicleInfo, view)
                saveChanges(vehicleInfo)
            }


            datePickerDialog.show()
        }






        val serviceRelativeLayout = view.findViewById<RelativeLayout>(R.id.serviceRelativeLayout)
        serviceRelativeLayout.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = Dialog(requireContext())
            datePickerDialog.setContentView(R.layout.custom_date_picker_dialog)

            val datePicker = datePickerDialog.findViewById<DatePicker>(R.id.datePicker)
            val cancelButton = datePickerDialog.findViewById<Button>(R.id.btnCancel)
            val okButton = datePickerDialog.findViewById<Button>(R.id.btnOk)
            val expiredButton = datePickerDialog.findViewById<Button>(R.id.btnExpired)
            val addYearButton = datePickerDialog.findViewById<Button>(R.id.btnAddYear)

            datePicker.minDate = System.currentTimeMillis() - 1000

            cancelButton.setOnClickListener {
                datePickerDialog.dismiss()
                updateServiceStatus(vehicleInfo, view)
            }

            okButton.setOnClickListener {
                val year = datePicker.year
                val month = datePicker.month + 1
                val dayOfMonth = datePicker.dayOfMonth
                val serviceDue = view.findViewById<TextView>(R.id.textViewServiceDueDate)
                serviceDue.text = "Due: $year-$month-$dayOfMonth"
                datePickerDialog.dismiss()
                vehicleInfo.serviceDue = "$year-$month-$dayOfMonth"
                updateServiceStatus(vehicleInfo, view)
                saveChanges(vehicleInfo)
            }

            expiredButton.setOnClickListener {
                val serviceDue = view.findViewById<TextView>(R.id.textViewServiceDueDate)
                serviceDue.text = "Needs Service"
                datePickerDialog.dismiss()
                vehicleInfo.serviceDue = "Needs Service"
                updateServiceStatus(vehicleInfo, view)
                saveChanges(vehicleInfo)
            }

            addYearButton.setOnClickListener {
                val year = datePicker.year
                val month = datePicker.month
                val dayOfMonth = datePicker.dayOfMonth

                // Create a Calendar instance and set it to the selected date
                val calendar = Calendar.getInstance()
                calendar.set(year, month, dayOfMonth)

                // Add 1 year to the selected date
                calendar.add(Calendar.YEAR, 1)

                // Update the DatePicker with the new date
                datePicker.updateDate(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                val year1 = datePicker.year
                val month1 = datePicker.month + 1
                val dayOfMonth1 = datePicker.dayOfMonth
                val serviceDue = view.findViewById<TextView>(R.id.textViewServiceDueDate)
                serviceDue.text = "Due: $year1-$month1-$dayOfMonth1"
                datePickerDialog.dismiss()
                vehicleInfo.serviceDue = "$year1-$month1-$dayOfMonth1"
                updateServiceStatus(vehicleInfo, view)
                saveChanges(vehicleInfo)
            }


            datePickerDialog.show()
        }



        return view
    }

    private fun updateInsuraceStatus(vehicleInfo: VehicleInfo, view: View) {
        val insuranceStatus = view.findViewById<ImageView>(R.id.insuranceImageView)

        if (vehicleInfo.insuranceExpiry == "No Insurance") {

            insuranceStatus.setImageResource(R.drawable.red_cross)
            val textViewInsuranceExpiry = view.findViewById<TextView>(R.id.textViewInsuranceExpiry)
            textViewInsuranceExpiry.text = "${vehicleInfo.insuranceExpiry}"
        }else if(vehicleInfo.insuranceExpiry == "") {

            insuranceStatus.setImageResource(R.drawable.orange_questionmark)
            val textViewInsuranceExpiry = view.findViewById<TextView>(R.id.textViewInsuranceExpiry)
            textViewInsuranceExpiry.text = "${vehicleInfo.insuranceExpiry}"

        } else {
            // Get the current date
            val currentDate = Calendar.getInstance()

            // Parse the insurance expiry date into a Calendar instance
            val insuranceExpiryDate = Calendar.getInstance()
            insuranceExpiryDate.time = SimpleDateFormat("yyyy-MM-dd").parse(vehicleInfo.insuranceExpiry)

            // Compare the insurance expiry date with the current date
            if (insuranceExpiryDate.after(currentDate)) {
                // Insurance expiry date is in the future
                insuranceStatus.setImageResource(R.drawable.green_tick)
                val textViewInsuranceExpiry = view.findViewById<TextView>(R.id.textViewInsuranceExpiry)
                textViewInsuranceExpiry.text = "Expires: ${vehicleInfo.insuranceExpiry}"
            } else if (insuranceExpiryDate.before(currentDate)) {
                // Insurance expiry date is in the past
                insuranceStatus.setImageResource(R.drawable.red_cross)
                val textViewInsuranceExpiry = view.findViewById<TextView>(R.id.textViewInsuranceExpiry)
                textViewInsuranceExpiry.text = "Expired: ${vehicleInfo.insuranceExpiry}"
            } else {
                // Insurance expiry date is today
                insuranceStatus.setImageResource(R.drawable.orange_questionmark)
                val textViewInsuranceExpiry = view.findViewById<TextView>(R.id.textViewInsuranceExpiry)
                textViewInsuranceExpiry.text = "Expires: ${vehicleInfo.insuranceExpiry}"
            }
        }
    }

    private fun updateServiceStatus(vehicleInfo: VehicleInfo, view: View) {
        val serviceStatus = view.findViewById<ImageView>(R.id.serviceImageView)

        if (vehicleInfo.serviceDue == "Needs Service") {

            serviceStatus.setImageResource(R.drawable.red_cross)
            val textViewServiceDue = view.findViewById<TextView>(R.id.textViewServiceDueDate)
            textViewServiceDue.text = "${vehicleInfo.serviceDue}"
        }else if(vehicleInfo.serviceDue == "") {

            serviceStatus.setImageResource(R.drawable.orange_questionmark)
            val textViewServiceDue = view.findViewById<TextView>(R.id.textViewServiceDueDate)
            textViewServiceDue.text = "${vehicleInfo.serviceDue}"

        } else {
            // Get the current date
            val currentDate = Calendar.getInstance()

            // Parse the insurance expiry date into a Calendar instance
            val serviceDue = Calendar.getInstance()
            serviceDue.time = SimpleDateFormat("yyyy-MM-dd").parse(vehicleInfo.serviceDue)

            // Compare the insurance expiry date with the current date
            if (serviceDue.after(currentDate)) {
                // Insurance expiry date is in the future
                serviceStatus.setImageResource(R.drawable.green_tick)
                val textViewServiceDue = view.findViewById<TextView>(R.id.textViewServiceDueDate)
                textViewServiceDue.text = "Due: ${vehicleInfo.serviceDue}"
            } else if (serviceDue.before(currentDate)) {
                // Insurance expiry date is in the past
                serviceStatus.setImageResource(R.drawable.red_cross)
                val textViewServiceDue = view.findViewById<TextView>(R.id.textViewServiceDueDate)
                textViewServiceDue.text = "Due Since: ${vehicleInfo.serviceDue}"
            } else {
                // Insurance expiry date is today
                serviceStatus.setImageResource(R.drawable.orange_questionmark)
                val textViewServiceDue = view.findViewById<TextView>(R.id.textViewServiceDueDate)
                textViewServiceDue.text = "Due: ${vehicleInfo.serviceDue}"
            }
        }
    }



    private fun setVehicleMakeLogo(make: String, view: View) {
        val logoImageView = view.findViewById<ImageView>(R.id.logoImageView)
        val logoResourceId = getLogoResourceForMake(make)
        logoImageView.setImageResource(logoResourceId)
    }


    private fun saveChanges(vehicleInfo: VehicleInfo){
        // Get the VRN
        val vrn = vehicleInfo.registrationNumber

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
                    Toast.makeText(context, "Vehicle details added to database", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    // Failed to add vehicle details
                    Toast.makeText(context, "Failed to add vehicle details: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
    // Function to get the resource ID of the vehicle logo based on the vehicle make
    private fun getLogoResourceForMake(make: String): Int {
        return when (make) {
            "BMW" -> R.drawable.bmw_logo
            "NISSAN" -> R.drawable.nissan_logo
            "SEAT" -> R.drawable.seat_logo
            "TOYOTA" -> R.drawable.toyota_logo
            "SUZUKI" -> R.drawable.suzuki_logo
            "MERCEDES-BENZ" -> R.drawable.mercedes_logo
            "VAUXHALL" -> R.drawable.vauxhall_logo
            "AUDI" -> R.drawable.audi_logo
            "VOLKSWAGEN" -> R.drawable.volkswagen_logo
            "CITROEN" -> R.drawable.citroen_logo
            "HYUNDAI" -> R.drawable.hyundai_logo

            // Add more cases for other vehicle makes as needed
            else -> R.drawable.default_logo // Default logo if the make is not found
        }
    }
}




