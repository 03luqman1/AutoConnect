package com.example.autoconnect.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import com.example.autoconnect.NotificationBroadcastReceiver
import com.example.autoconnect.NotificationHelper
import com.example.autoconnect.R
import com.example.autoconnect.StartActivity
import com.example.autoconnect.databinding.FragmentHomeBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.Calendar
import kotlin.random.Random


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        // Request notification policy access permission
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_NOTIFICATION_POLICY),
            0
        )

        NotificationHelper.createNotificationChannel(requireContext())


        binding.buttonSignOut.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(Intent(requireContext(), StartActivity::class.java))
            requireActivity().finish()
        }

        binding.buttonSettings.setOnClickListener {
            findNavController().navigate(R.id.navigation_settings)
        }

        binding.buttonNotifications.setOnClickListener {
            // Send immediate notification
            NotificationHelper.sendNotification(
                requireContext(),
                "My Notification",
                "This is a notification from my app"
            )

            // Schedule notification for an hour later
            scheduleNotification(requireContext())
        }


        binding.buttonAbout.setOnClickListener {
            findNavController().navigate(R.id.navigation_about)
        }

        binding.buttonLeaveReview.setOnClickListener {
            findNavController().navigate(R.id.navigation_review)
            //startActivity(Intent(requireContext(), ReviewFragment::class.java))
           // requireActivity()
        }
        // Uncomment the following lines if you need to observe ViewModel data
        /*
        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        */

        return root
    }


    private fun scheduleNotification(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val notificationIntent = Intent(context, NotificationBroadcastReceiver::class.java)
        notificationIntent.putExtra("title", "Scheduled Notification")
        notificationIntent.putExtra("message", "The is is a scheduled notification from my app. Enjoy.")

        val requestCode = Random.nextInt() // Generate a random request code
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,//or set fixed number: 0 ??
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
