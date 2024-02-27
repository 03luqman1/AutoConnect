package com.example.autoconnect

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.autoconnect.dataclasses.VehicleInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DisplayVehicleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_vehicle)

        // Retrieve the vehicle info from the intent
        val vehicleInfo = intent.getSerializableExtra("vehicle_info") as VehicleInfo

        // Populate the UI components with the vehicle details
        val textViewRegistrationNumber = findViewById<TextView>(R.id.textViewRegistrationNumber)
        textViewRegistrationNumber.text = "${vehicleInfo.registrationNumber}"


        val taxStatus = findViewById<ImageView>(R.id.taxImageView)
        // Check if the tax status is "Valid" and set the text accordingly
        if (vehicleInfo.taxStatus == "Taxed") {
            taxStatus.setImageResource(R.drawable.green_tick)
            val textViewTaxDueDate = findViewById<TextView>(R.id.textViewTaxDueDate)
            textViewTaxDueDate.text = "Tax Due Date: ${vehicleInfo.taxDueDate}"
        } else if (vehicleInfo.taxStatus == "Untaxed" || vehicleInfo.taxStatus == "SORN"){
            taxStatus.setImageResource(R.drawable.red_cross)
            val textViewTaxDueDate = findViewById<TextView>(R.id.textViewTaxDueDate)
            textViewTaxDueDate.text = "Tax Due Date: ${vehicleInfo.taxDueDate}"
        }



        val motStatus = findViewById<ImageView>(R.id.motImageView)
        // Check if the tax status is "Valid" and set the text accordingly
        if (vehicleInfo.motStatus == "Valid") {
            motStatus.setImageResource(R.drawable.green_tick)
            val textViewMOTExpiryDate = findViewById<TextView>(R.id.textViewMOTExpiryDate)
            textViewMOTExpiryDate.text = "MOT Expiry Date: ${vehicleInfo.motExpiryDate}"
        } else if (vehicleInfo.motStatus == "No details held by DVLA"){
            motStatus.setImageResource(R.drawable.green_tick)
            val textViewMOTExpiryDate = findViewById<TextView>(R.id.textViewMOTExpiryDate)
            val formatter = SimpleDateFormat("yyyy-MM", Locale.getDefault())
            val firstRegDate = Calendar.getInstance()
            firstRegDate.time = formatter.parse(vehicleInfo.monthOfFirstRegistration)
            firstRegDate.add(Calendar.YEAR, 3)
            val formattedExpiryDate = formatter.format(firstRegDate.time)
            textViewMOTExpiryDate.text = "MOT Expiry Date: $formattedExpiryDate"
        }else if (vehicleInfo.motStatus == "Not Valid"){
            motStatus.setImageResource(R.drawable.red_cross)
        }




        val textViewMake = findViewById<TextView>(R.id.textViewMake)
        textViewMake.text = "Make: ${vehicleInfo.make}"

        val textViewYearOfManufacture = findViewById<TextView>(R.id.textViewYearOfManufacture)
        textViewYearOfManufacture.text = "Year of Manufacture: ${vehicleInfo.yearOfManufacture}"

        val textViewEngineCapacity = findViewById<TextView>(R.id.textViewEngineCapacity)
        textViewEngineCapacity.text = "Engine Capacity: ${vehicleInfo.engineCapacity}"

        val textViewCO2Emissions = findViewById<TextView>(R.id.textViewCO2Emissions)
        textViewCO2Emissions.text = "CO2 Emissions: ${vehicleInfo.co2Emissions}"

        val textViewFuelType = findViewById<TextView>(R.id.textViewFuelType)
        textViewFuelType.text = "Fuel Type: ${vehicleInfo.fuelType}"

        val textViewMarkedForExport = findViewById<TextView>(R.id.textViewMarkedForExport)
        textViewMarkedForExport.text = "Marked for Export: ${vehicleInfo.markedForExport}"

        val textViewColour = findViewById<TextView>(R.id.textViewColour)
        textViewColour.text = "Colour: ${vehicleInfo.colour}"

        val textViewTypeApproval = findViewById<TextView>(R.id.textViewTypeApproval)
        textViewTypeApproval.text = "Type Approval: ${vehicleInfo.typeApproval}"

        val textViewDateOfLastV5CIssued = findViewById<TextView>(R.id.textViewDateOfLastV5CIssued)
        textViewDateOfLastV5CIssued.text = "Date of Last V5C Issued: ${vehicleInfo.dateOfLastV5CIssued}"

        val textViewWheelplan = findViewById<TextView>(R.id.textViewWheelplan)
        textViewWheelplan.text = "Wheelplan: ${vehicleInfo.wheelplan}"

        val textViewMonthOfFirstRegistration = findViewById<TextView>(R.id.textViewMonthOfFirstRegistration)
        textViewMonthOfFirstRegistration.text = "Expires: ${vehicleInfo.insuranceExpiry}"

        // Set the vehicle make logo
        setVehicleMakeLogo(vehicleInfo.make)
        // Add more TextViews for other vehicle details as needed




        updateInsuraceStatus(vehicleInfo)
        updateServiceStatus(vehicleInfo)


        val insuranceRelativeLayout = findViewById<RelativeLayout>(R.id.insuranceRelativeLayout)
        insuranceRelativeLayout.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = Dialog(this)
            datePickerDialog.setContentView(R.layout.custom_date_picker_dialog)

            val datePicker = datePickerDialog.findViewById<DatePicker>(R.id.datePicker)
            val cancelButton = datePickerDialog.findViewById<Button>(R.id.btnCancel)
            val okButton = datePickerDialog.findViewById<Button>(R.id.btnOk)
            val expiredButton = datePickerDialog.findViewById<Button>(R.id.btnExpired)
            val addYearButton = datePickerDialog.findViewById<Button>(R.id.btnAddYear)

            datePicker.minDate = System.currentTimeMillis() - 1000

            cancelButton.setOnClickListener {
                datePickerDialog.dismiss()
                updateInsuraceStatus(vehicleInfo)
            }

            okButton.setOnClickListener {
                val year = datePicker.year
                val month = datePicker.month + 1
                val dayOfMonth = datePicker.dayOfMonth
                val insuranceExpiryDate = findViewById<TextView>(R.id.textViewInsuranceExpiry)
                insuranceExpiryDate.text = "Expires: $year-$month-$dayOfMonth"
                datePickerDialog.dismiss()
                vehicleInfo.insuranceExpiry = "$year-$month-$dayOfMonth"
                updateInsuraceStatus(vehicleInfo)
                saveChanges(vehicleInfo)
            }

            expiredButton.setOnClickListener {
                val insuranceExpiryDate = findViewById<TextView>(R.id.textViewInsuranceExpiry)
                insuranceExpiryDate.text = "No Insurance"
                datePickerDialog.dismiss()
                vehicleInfo.insuranceExpiry = "No Insurance"
                updateInsuraceStatus(vehicleInfo)
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
                val insuranceExpiryDate = findViewById<TextView>(R.id.textViewInsuranceExpiry)
                insuranceExpiryDate.text = "Expires: $year1-$month1-$dayOfMonth1"
                datePickerDialog.dismiss()
                vehicleInfo.insuranceExpiry = "$year1-$month1-$dayOfMonth1"
                updateInsuraceStatus(vehicleInfo)
                saveChanges(vehicleInfo)
            }


            datePickerDialog.show()
        }






        val serviceRelativeLayout = findViewById<RelativeLayout>(R.id.serviceRelativeLayout)
        serviceRelativeLayout.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = Dialog(this)
            datePickerDialog.setContentView(R.layout.custom_date_picker_dialog)

            val datePicker = datePickerDialog.findViewById<DatePicker>(R.id.datePicker)
            val cancelButton = datePickerDialog.findViewById<Button>(R.id.btnCancel)
            val okButton = datePickerDialog.findViewById<Button>(R.id.btnOk)
            val expiredButton = datePickerDialog.findViewById<Button>(R.id.btnExpired)
            val addYearButton = datePickerDialog.findViewById<Button>(R.id.btnAddYear)

            datePicker.minDate = System.currentTimeMillis() - 1000

            cancelButton.setOnClickListener {
                datePickerDialog.dismiss()
                updateServiceStatus(vehicleInfo)
            }

            okButton.setOnClickListener {
                val year = datePicker.year
                val month = datePicker.month + 1
                val dayOfMonth = datePicker.dayOfMonth
                val serviceDue = findViewById<TextView>(R.id.textViewServiceDueDate)
                serviceDue.text = "Due: $year-$month-$dayOfMonth"
                datePickerDialog.dismiss()
                vehicleInfo.serviceDue = "$year-$month-$dayOfMonth"
                updateServiceStatus(vehicleInfo)
                saveChanges(vehicleInfo)
            }

            expiredButton.setOnClickListener {
                val serviceDue = findViewById<TextView>(R.id.textViewServiceDueDate)
                serviceDue.text = "Needs Service"
                datePickerDialog.dismiss()
                vehicleInfo.serviceDue = "Needs Service"
                updateServiceStatus(vehicleInfo)
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
                val serviceDue = findViewById<TextView>(R.id.textViewServiceDueDate)
                serviceDue.text = "Due: $year1-$month1-$dayOfMonth1"
                datePickerDialog.dismiss()
                vehicleInfo.serviceDue = "$year1-$month1-$dayOfMonth1"
                updateServiceStatus(vehicleInfo)
                saveChanges(vehicleInfo)
            }


            datePickerDialog.show()
        }


































    }

    private fun updateInsuraceStatus(vehicleInfo: VehicleInfo) {
        val insuranceStatus = findViewById<ImageView>(R.id.insuranceImageView)

        if (vehicleInfo.insuranceExpiry == "No Insurance") {

            insuranceStatus.setImageResource(R.drawable.red_cross)
            val textViewInsuranceExpiry = findViewById<TextView>(R.id.textViewInsuranceExpiry)
            textViewInsuranceExpiry.text = "${vehicleInfo.insuranceExpiry}"
        }else if(vehicleInfo.insuranceExpiry == "") {

            insuranceStatus.setImageResource(R.drawable.orange_questionmark)
            val textViewInsuranceExpiry = findViewById<TextView>(R.id.textViewInsuranceExpiry)
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
                val textViewInsuranceExpiry = findViewById<TextView>(R.id.textViewInsuranceExpiry)
                textViewInsuranceExpiry.text = "Expires: ${vehicleInfo.insuranceExpiry}"
            } else if (insuranceExpiryDate.before(currentDate)) {
                // Insurance expiry date is in the past
                insuranceStatus.setImageResource(R.drawable.red_cross)
                val textViewInsuranceExpiry = findViewById<TextView>(R.id.textViewInsuranceExpiry)
                textViewInsuranceExpiry.text = "Expired: ${vehicleInfo.insuranceExpiry}"
            } else {
                // Insurance expiry date is today
                insuranceStatus.setImageResource(R.drawable.orange_questionmark)
                val textViewInsuranceExpiry = findViewById<TextView>(R.id.textViewInsuranceExpiry)
                textViewInsuranceExpiry.text = "Expires: ${vehicleInfo.insuranceExpiry}"
            }
        }
    }

    private fun updateServiceStatus(vehicleInfo: VehicleInfo) {
        val serviceStatus = findViewById<ImageView>(R.id.serviceImageView)

        if (vehicleInfo.serviceDue == "Needs Service") {

            serviceStatus.setImageResource(R.drawable.red_cross)
            val textViewServiceDue = findViewById<TextView>(R.id.textViewServiceDueDate)
            textViewServiceDue.text = "${vehicleInfo.serviceDue}"
        }else if(vehicleInfo.serviceDue == "") {

            serviceStatus.setImageResource(R.drawable.orange_questionmark)
            val textViewServiceDue = findViewById<TextView>(R.id.textViewServiceDueDate)
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
                val textViewServiceDue = findViewById<TextView>(R.id.textViewServiceDueDate)
                textViewServiceDue.text = "Due: ${vehicleInfo.serviceDue}"
            } else if (serviceDue.before(currentDate)) {
                // Insurance expiry date is in the past
                serviceStatus.setImageResource(R.drawable.red_cross)
                val textViewServiceDue = findViewById<TextView>(R.id.textViewServiceDueDate)
                textViewServiceDue.text = "Due Since: ${vehicleInfo.serviceDue}"
            } else {
                // Insurance expiry date is today
                serviceStatus.setImageResource(R.drawable.orange_questionmark)
                val textViewServiceDue = findViewById<TextView>(R.id.textViewServiceDueDate)
                textViewServiceDue.text = "Due: ${vehicleInfo.serviceDue}"
            }
        }
    }



    private fun setVehicleMakeLogo(make: String) {
        val logoImageView = findViewById<ImageView>(R.id.logoImageView)
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
                    Toast.makeText(this, "Vehicle details added to database", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    // Failed to add vehicle details
                    Toast.makeText(this, "Failed to add vehicle details: ${e.message}", Toast.LENGTH_SHORT).show()
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

            // Add more cases for other vehicle makes as needed
            else -> R.drawable.default_logo // Default logo if the make is not found
        }
    }
}

