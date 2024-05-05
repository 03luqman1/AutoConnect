package com.example.autoconnect.ui.settings

import android.app.AlarmManager
import android.app.PendingIntent
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
import android.widget.Switch
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.autoconnect.MainActivity
import com.example.autoconnect.NotificationBroadcastReceiver
import com.example.autoconnect.NotificationHelper
import com.example.autoconnect.R
import com.example.autoconnect.VehicleInfo
import com.example.autoconnect.databinding.FragmentGarageBinding
import com.example.autoconnect.databinding.FragmentHomeBinding
import java.util.Calendar
import kotlin.random.Random

class SettingsFragment : Fragment() {

    private val NOTIFICATION_CHANNEL_ID = "my_channel"

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(com.example.autoconnect.R.layout.fragment_settings, container, false)

        val buttonBack: Button = view.findViewById(R.id.buttonBack2)
        val notif: Button = view.findViewById(R.id.testNotificationButton)
        val delayedNotif: Button = view.findViewById(R.id.testDelayedNotificationButton)

        val toggleNotificationText = view.findViewById<TextView>(R.id.toggleNotificationText)
        val notificationSwitch = view.findViewById<Switch>(R.id.notificationSwitch)

        // Check the current notification state and update the switch accordingly
        val areNotificationsEnabled = getNotificationState()
        notificationSwitch.isChecked = areNotificationsEnabled

        // Set a listener to handle switch state change
        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Update notification state
            setNotificationState(isChecked)

            // Update the UI
            if (isChecked) {
                toggleNotificationText.text = "Notifications Enabled"
            } else {
                toggleNotificationText.text = "Notifications Disabled"
                val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val sharedPreferences = requireContext().getSharedPreferences("notification_request_codes", Context.MODE_PRIVATE)
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
        }

        buttonBack.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.navigation_home)
        }
        notif.setOnClickListener {
            // Check if notifications are enabled
            if (getNotificationState()) {
                // Send immediate notification
                NotificationHelper.sendNotification(
                    requireContext(),
                    "My Notification",
                    "This is a notification from my app"
                )
            }
        }

        delayedNotif.setOnClickListener {
            if (getNotificationState()) {
                scheduleNotification(requireContext())
            }

        }

        return view
    }

    private fun scheduleNotification(context: Context) {

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val notificationIntent = Intent(context, NotificationBroadcastReceiver::class.java)
        notificationIntent.putExtra("title", "Scheduled Notification")
        notificationIntent.putExtra("message", "The is is a scheduled notification from my app\n:)")

        val requestCode = Random.nextInt() // Generate a random request code
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,//or set fixed number: 0 ??
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE // Use FLAG_IMMUTABLE here
        )

        // Schedule the alarm to trigger after an hour (3600 * 1000 milliseconds)
        val triggerTime = Calendar.getInstance().timeInMillis + 10000
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )


    }


    // Function to get the current notification state from MainActivity
    private fun getNotificationState(): Boolean {
        return (requireActivity() as MainActivity).getNotificationState()
    }

    // Function to update the notification state in MainActivity
    private fun setNotificationState(enabled: Boolean) {
        (requireActivity() as MainActivity).setNotificationState(enabled)
    }



}