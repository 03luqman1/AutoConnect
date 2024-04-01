package com.example.autoconnect.ui.add

import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.autoconnect.R
import com.example.autoconnect.VehicleInfo
import com.example.autoconnect.databinding.FragmentGarageBinding

class AddFragment : Fragment() {


    private lateinit var editTextVRN: EditText
    private lateinit var buttonSearchVehicle: Button
    private lateinit var textViewVehicleDetails: TextView
    private lateinit var vehicleInfo: VehicleInfo


    private var _binding: FragmentGarageBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(com.example.autoconnect.R.layout.fragment_add, container, false)


        editTextVRN = view.findViewById(R.id.editTextVRN2)
        buttonSearchVehicle = view.findViewById(R.id.buttonSearchVehicle)
        textViewVehicleDetails = view.findViewById(R.id.textViewVehicleDetails)


        editTextVRN.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called before the text is changed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called when the text is changed

                // If the text is empty or null, hide textViewVehicleDetails and buttonConfirmVehicle
                textViewVehicleDetails.visibility = View.GONE
                buttonSearchVehicle.text = "SEARCH VEHICLE"
            }

            override fun afterTextChanged(s: Editable?) {
                // This method is called after the text is changed
            }
        })


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
                vehicleInfo = gson.fromJson(
                    responseBody,
                    VehicleInfo::class.java
                ) // Assign to the class property


                requireActivity().runOnUiThread {
                    if (!vehicleInfo.make.isNullOrEmpty()) {
                        // Get the InputMethodManager
                        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager//WHY IS this UNDERLINED IN RED?

                        // Hide the keyboard
                        imm.hideSoftInputFromWindow(editTextVRN.windowToken, 0)

                        // Inflate the confirmation pop-up layout
                        val inflater = LayoutInflater.from(requireContext())//WHY IS this UNDERLINED IN RED?
                        val popupView = inflater.inflate(R.layout.confirm_add_dialog, null)



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
                        val btnCancel = popupView.findViewById<Button>(R.id.btnCancelAdd)
                        val btnConfirmAdd = popupView.findViewById<Button>(R.id.btnConfirmAdd)
                        val vehicleAddLogo = popupView.findViewById<ImageView>(R.id.imageViewConfirmVehicleLogo)


                        var txtVehicleDetail = popupView.findViewById<TextView>(R.id.textViewConfirmVehicleDetails)
                        txtVehicleDetail.text = "${vehicleInfo.yearOfManufacture} ${vehicleInfo.colour} ${vehicleInfo.make}"//LINE 132


                        val logoResourceId = getLogoResourceForMake(vehicleInfo.make)
                        vehicleAddLogo.setImageResource(logoResourceId)





                        // Set click listener for the cancel button
                        btnCancel.setOnClickListener {
                            // Dismiss the pop-up
                            popupWindow.dismiss()
                        }

                        // Set click listener for the confirm delete button
                        btnConfirmAdd.setOnClickListener {
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
                                        Toast.makeText(context, "Vehicle details added to database", Toast.LENGTH_SHORT).show()
                                        val navController = findNavController()
                                        navController.navigate(R.id.navigation_garage)
                                        //startActivity(Intent(this@AddVehicleActivity, GarageActivity::class.java))
                                        //finish()
                                    }
                                    .addOnFailureListener { e ->
                                        // Failed to add vehicle details
                                        Toast.makeText(context, "Failed to add vehicle details: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }

                            // Dismiss the pop-up after confirming deletion
                            popupWindow.dismiss()
                        }

                    }
                }
            }
        }


        return view
    }

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