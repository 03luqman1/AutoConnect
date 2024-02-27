package com.example.autoconnect

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.autoconnect.dataclasses.VehicleInfo

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
        } else if (vehicleInfo.taxStatus == "Not Taxed" || vehicleInfo.taxStatus == "SORN"){
            taxStatus.setImageResource(R.drawable.red_cross)
        }

        val textViewTaxDueDate = findViewById<TextView>(R.id.textViewTaxDueDate)
        textViewTaxDueDate.text = "Tax Due Date: ${vehicleInfo.taxDueDate}"

        val textViewMOTStatus = findViewById<TextView>(R.id.textViewMOTStatus)
        textViewMOTStatus.text = "MOT:"

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

        val textViewMOTExpiryDate = findViewById<TextView>(R.id.textViewMOTExpiryDate)
        textViewMOTExpiryDate.text = "MOT Expiry Date: ${vehicleInfo.motExpiryDate}"

        val textViewWheelplan = findViewById<TextView>(R.id.textViewWheelplan)
        textViewWheelplan.text = "Wheelplan: ${vehicleInfo.wheelplan}"

        val textViewMonthOfFirstRegistration = findViewById<TextView>(R.id.textViewMonthOfFirstRegistration)
        textViewMonthOfFirstRegistration.text = "Month of First Registration: ${vehicleInfo.monthOfFirstRegistration}"

        // Set the vehicle make logo
        setVehicleMakeLogo(vehicleInfo.make)
        // Add more TextViews for other vehicle details as needed
    }

    private fun setVehicleMakeLogo(make: String) {
        val logoImageView = findViewById<ImageView>(R.id.logoImageView)
        val logoResourceId = getLogoResourceForMake(make)
        logoImageView.setImageResource(logoResourceId)
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

            // Add more cases for other vehicle makes as needed
            else -> R.drawable.default_logo // Default logo if the make is not found
        }
    }
}

