package com.example.autoconnect.ui.garage

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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
import com.example.autoconnect.NotificationBroadcastReceiver
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
import org.json.JSONArray
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.random.Random

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

                    val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

                    // Cancel all existing notifications
                    cancelNotifications(requireContext(), alarmManager)

                    val requestCodes = mutableListOf<Int>()

                    for (vehicleSnapshot in snapshot.children) {
                        val vehicle = vehicleSnapshot.getValue(VehicleInfo::class.java)
                        vehicle?.let {
                            vehicleList.add(it)




                        }
                        vehicle?.let { fetchVehicleDataAndUpdateDatabase(it) }

                        val taxRequestCode = Random.nextInt()
                        val motRequestCode = Random.nextInt()
                        val insuranceRequestCode = Random.nextInt()
                        val serviceRequestCode = Random.nextInt()

                        scheduleNotification(requireContext(), vehicle?.taxDueDate.toString(), vehicle?.registrationNumber.toString(), "Tax", taxRequestCode)
                        scheduleNotification(requireContext(), vehicle?.motExpiryDate.toString(), vehicle?.registrationNumber.toString(), "MOT", motRequestCode)
                        scheduleNotification(requireContext(), vehicle?.insuranceExpiry.toString(), vehicle?.registrationNumber.toString(), "Insurance", insuranceRequestCode)
                        scheduleNotification(requireContext(), vehicle?.serviceDue.toString(), vehicle?.registrationNumber.toString(), "Service", serviceRequestCode)

                        requestCodes.add(taxRequestCode)
                        requestCodes.add(motRequestCode)
                        requestCodes.add(insuranceRequestCode)
                        requestCodes.add(serviceRequestCode)

                    }

                    // Store request codes in SharedPreferences
                    saveRequestCodes(requireContext(), requestCodes)

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

    private fun saveRequestCodes(context: Context, requestCodes: List<Int>) {
        val sharedPreferences = context.getSharedPreferences("notification_request_codes", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("request_codes", Gson().toJson(requestCodes))
        editor.apply()
    }

    private fun cancelNotifications(context: Context, alarmManager: AlarmManager) {
        val sharedPreferences = context.getSharedPreferences("notification_request_codes", Context.MODE_PRIVATE)
        val requestCodesJson = sharedPreferences.getString("request_codes", null)
        val requestCodes = Gson().fromJson(requestCodesJson, Array<Int>::class.java)

        requestCodes?.forEach { requestCode ->
            val notificationIntent = Intent(context, NotificationBroadcastReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
            )

            pendingIntent?.let {
                alarmManager.cancel(it)
                it.cancel()
            }
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

    private fun isValidDateFormat(date: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val parsedDate = sdf.parse(date)
            val currentDate = Calendar.getInstance().time

            parsedDate?.after(currentDate) ?: false
        } catch (e: ParseException) {
            false
        }
    }


    private fun scheduleNotification(context: Context, dueDate: String, vrn: String, type: String, reqCode: Int) {

if (isValidDateFormat(dueDate)) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val notificationIntent = Intent(context, NotificationBroadcastReceiver::class.java)
    notificationIntent.putExtra("title", "Vehicle $type Reminder")
    notificationIntent.putExtra("message", "Vehicle: $vrn \nDue/Expires: $dueDate")

    //val requestCode = Random.nextInt() // Generate a random request code

    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = sdf.parse(dueDate)
    val calendar = Calendar.getInstance()
    calendar.time = date!!

    // Set the time to 9 am
    calendar.set(Calendar.HOUR_OF_DAY, 9)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)


    val pendingIntent = PendingIntent.getBroadcast(
        context,
        reqCode,//requestCode,//or set fixed number: 0 ??
        notificationIntent,
        PendingIntent.FLAG_IMMUTABLE // Use FLAG_IMMUTABLE here
    )


    // Schedule the alarm to trigger after an hour (3600 * 1000 milliseconds)
    //val triggerTime = Calendar.getInstance().timeInMillis + 30000
    alarmManager.setExact(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        pendingIntent
    )
} else if (dueDate == "No details held by DVLA"){
    println(dueDate)
    Toast.makeText(requireContext(), dueDate, Toast.LENGTH_SHORT).show()
}
    }
}