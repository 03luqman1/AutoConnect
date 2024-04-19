package com.example.autoconnect.ui.garage

import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import android.widget.PopupWindow
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.autoconnect.R
import com.example.autoconnect.VehicleInfo
import com.example.autoconnect.databinding.FragmentGarageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

class GarageFragment : Fragment() {

    private lateinit var listViewVehicles: ListView
    private lateinit var buttonAddNewVehicle: Button
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private var _binding: FragmentGarageBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(com.example.autoconnect.R.layout.fragment_garage, container, false)

        listViewVehicles = view.findViewById(R.id.listViewVehicles)
        buttonAddNewVehicle = view.findViewById(R.id.buttonAddNewVehicle)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance().reference

        buttonAddNewVehicle.setOnClickListener {
            findNavController(view).navigate(R.id.navigation_add)
        }





        // Display the user's vehicles in the ListView
        displayUserVehicles()
// Inside your Fragment or Activity where you want to navigate
        val navController = findNavController()
        listViewVehicles.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            // Get the selected vehicle info
            val selectedVehicle = listViewVehicles.adapter.getItem(position) as VehicleInfo

            // Navigate to the vehicle detail fragment and pass the selected vehicle's information
            //findNavController().navigate(R.id.navigation_vehicle)
            val bundle = Bundle()
            bundle.putSerializable("vehicleInfo", selectedVehicle)
            findNavController(view).navigate(R.id.navigation_vehicle, bundle)


            //navController.navigate(R.id.navigation_vehicle)




        }








        return view
    }






    private fun displayUserVehicles() {
        // Get the UID of the currently authenticated user
        val uid = auth.currentUser?.uid

        // Ensure the user is authenticated
        if (uid != null) {
            // Reference to the "vehicles" node under the user's UID
            val vehiclesRef = database.child("Users").child(uid).child("Vehicles")

            // Listen for changes in the vehicles node
            vehiclesRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val vehicleList = mutableListOf<VehicleInfo>()
                    for (vehicleSnapshot in snapshot.children) {
                        val vehicle = vehicleSnapshot.getValue(VehicleInfo::class.java)
                        vehicle?.let {
                            vehicleList.add(it)
                        }
                        vehicle?.let { fetchVehicleDataAndUpdateDatabase(it) }
                    }

                    // Create a custom adapter to populate the ListView
                    val adapter = VehicleAdapter(requireContext(), vehicleList)

                    // Set the adapter to the ListView
                    listViewVehicles.adapter = adapter
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }
    }

    private fun fetchVehicleDataAndUpdateDatabase(vehicle: VehicleInfo) {
// Create OkHttpClient instance
        val client = OkHttpClient()

        // Define the request body
        val mediaType = "application/json".toMediaType()
        val body = "{\"registrationNumber\": \"${vehicle.registrationNumber}\"}".toRequestBody(mediaType)

        // Create the request
        val request = Request.Builder()
            .url("https://driver-vehicle-licensing.api.gov.uk/vehicle-enquiry/v1/vehicles")
            .post(body)
            .addHeader("x-api-key", "c5jFj6j13r4eNwFSMo706bniWL02zjqaKllvaqA6")
            .addHeader("Content-Type", "application/json")
            .build()

        // Make the API call asynchronously
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Handle failure
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                val gson = Gson()
                val updatedVehicleInfo = gson.fromJson(responseBody, VehicleInfo::class.java)

                updatedVehicleInfo.insuranceExpiry = vehicle.insuranceExpiry
                updatedVehicleInfo.serviceDue = vehicle.serviceDue

                // Update the database with the returned vehicle data
                database.child("Users").child(auth.currentUser!!.uid)
                    .child("Vehicles").child(vehicle.registrationNumber)
                    .setValue(updatedVehicleInfo)
            }
        })
    }
}