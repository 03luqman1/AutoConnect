package com.example.autoconnect.ui.garage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.autoconnect.R
import com.example.autoconnect.VehicleInfo
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class VehicleAdapter(context: Context, vehicles: List<VehicleInfo>) :
    ArrayAdapter<VehicleInfo>(context, 0, vehicles) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItemView = convertView
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(
                R.layout.item_vehicle, parent, false
            )
        }

        val currentVehicle = getItem(position)

        val logoImageView = listItemView?.findViewById<ImageView>(R.id.logoImageView)
        val vrnTextView = listItemView?.findViewById<TextView>(R.id.registrationNumberTextView)



        val motStatus = listItemView?.findViewById<ImageView>(R.id.motStatusImageView)
        val motStatusTextView = listItemView?.findViewById<TextView>(R.id.motStatusTextView)


        val taxStatus = listItemView?.findViewById<ImageView>(R.id.taxStatusImageView)
        val taxStatusTextView = listItemView?.findViewById<TextView>(R.id.taxStatusTextView)

        val insuranceStatus = listItemView?.findViewById<ImageView>(R.id.insuranceStatusImageView)
        val insuranceStatusTextView = listItemView?.findViewById<TextView>(R.id.insuranceStatusTextView)

        val serviceStatus = listItemView?.findViewById<ImageView>(R.id.serviceStatusImageView)
        val serviceStatusTextView = listItemView?.findViewById<TextView>(R.id.serviceStatusTextView)


        currentVehicle?.let {
            // Load the vehicle logo from the drawable folder
            logoImageView?.setImageResource(getLogoResource(currentVehicle.make))
            vrnTextView?.text = it.registrationNumber

            // Check if the tax status is "Valid" and set the text accordingly
            if (it.taxStatus == "Taxed") {
                taxStatus?.setImageResource(R.drawable.green_tick)
                taxStatusTextView?.text = " TAX (${it.taxDueDate})"
            } else if (it.taxStatus == "Untaxed" || it.taxStatus == "SORN"){
                taxStatus?.setImageResource(R.drawable.red_cross)
                taxStatusTextView?.text = " TAX (${it.taxStatus})"
            }

            if ((it.motStatus == "Valid")||(it.motStatus == "No details held by DVLA")) {
                motStatus?.setImageResource(R.drawable.green_tick)
                motStatusTextView?.text = " MOT (${it.motExpiryDate})"
            }
            else if (it.motStatus == "Not valid"){
                motStatus?.setImageResource(R.drawable.red_cross)
                motStatusTextView?.text = " MOT (${it.motStatus})"
            }



            if (it.insuranceExpiry == "No Insurance") {
                insuranceStatus?.setImageResource(R.drawable.red_cross)
                insuranceStatusTextView?.text = " INSURANCE (${it.insuranceExpiry})"
            }else if(it.insuranceExpiry == "") {
                insuranceStatus?.setImageResource(R.drawable.orange_questionmark)
                insuranceStatusTextView?.text = " INSURANCE"

            } else {
                // Get the current date
                val currentDate = Calendar.getInstance()

                // Parse the insurance expiry date into a Calendar instance
                val insuranceExpiryDate = Calendar.getInstance()
                insuranceExpiryDate.time = SimpleDateFormat("yyyy-MM-dd").parse(it.insuranceExpiry)

                // Compare the insurance expiry date with the current date
                if (insuranceExpiryDate.after(currentDate)) {
                    // Insurance expiry date is in the future
                    insuranceStatus?.setImageResource(R.drawable.green_tick)
                    insuranceStatusTextView?.text = " INSURANCE (${it.insuranceExpiry})"
                } else if (insuranceExpiryDate.before(currentDate)) {
                    // Insurance expiry date is in the past
                    insuranceStatus?.setImageResource(R.drawable.red_cross)
                    insuranceStatusTextView?.text = " INSURANCE (${it.insuranceExpiry})"
                } else {
                    // Insurance expiry date is today
                    insuranceStatus?.setImageResource(R.drawable.orange_questionmark)
                    insuranceStatusTextView?.text = " INSURANCE (${it.insuranceExpiry})"
                }
            }


            if (it.serviceDue == "Needs Service") {

                serviceStatus?.setImageResource(R.drawable.red_cross)
                serviceStatusTextView?.text = " SERVICE (${it.serviceDue})"
            }else if(it.serviceDue == "") {

                serviceStatus?.setImageResource(R.drawable.orange_questionmark)
                serviceStatusTextView?.text = " SERVICE"

            } else {
                // Get the current date
                val currentDate = Calendar.getInstance()

                // Parse the insurance expiry date into a Calendar instance
                val serviceDue = Calendar.getInstance()
                serviceDue.time = SimpleDateFormat("yyyy-MM-dd").parse(it.serviceDue)

                // Compare the insurance expiry date with the current date
                if (serviceDue.after(currentDate)) {
                    // Insurance expiry date is in the future
                    serviceStatus?.setImageResource(R.drawable.green_tick)
                    serviceStatusTextView?.text = " SERVICE (${it.serviceDue})"
                } else if (serviceDue.before(currentDate)) {
                    // Insurance expiry date is in the past
                    serviceStatus?.setImageResource(R.drawable.red_cross)
                    serviceStatusTextView?.text = " SERVICE (${it.serviceDue})"
                } else {
                    // Insurance expiry date is today
                    serviceStatus?.setImageResource(R.drawable.orange_questionmark)
                    serviceStatusTextView?.text = "SERVICE (${it.serviceDue})"
                }
            }



        }

        return listItemView!!
    }

    // Function to get the resource ID of the vehicle logo based on the vehicle make
    private fun getLogoResource(make: String): Int {
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

