package com.example.autoconnect

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.autoconnect.R
import com.example.autoconnect.dataclasses.VehicleInfo

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
        val motStatusTextView = listItemView?.findViewById<TextView>(R.id.motStatusTextView)
        val taxStatusTextView = listItemView?.findViewById<TextView>(R.id.taxStatusTextView)
        val insuranceStatusTextView = listItemView?.findViewById<TextView>(R.id.insuranceStatusTextView)

        currentVehicle?.let {
            // Load the vehicle logo from the drawable folder
            logoImageView?.setImageResource(getLogoResource(currentVehicle.make))
            vrnTextView?.text = it.registrationNumber
            motStatusTextView?.text = "MOT Status: ${it.motStatus} (Expiry: ${it.motExpiryDate})"
            taxStatusTextView?.text = "Tax Status: ${it.taxStatus} (Expiry: ${it.taxDueDate})"
            insuranceStatusTextView?.text = "Insurance Status: UNKNOWN (Expiry: UNKNOWN)"
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

