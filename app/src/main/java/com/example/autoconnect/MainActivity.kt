package com.example.autoconnect

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.autoconnect.databinding.ActivityMainBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val NOTIFICATION_CHANNEL_ID = "my_channel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.bottomNavigationView



        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        /*// Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                //R.id.navigation_home, R.id.navigation_garage, R.id.navigation_search
            )
        )
        //setupActionBarWithNavController(navController, appBarConfiguration)*/
        navView.setupWithNavController(navController)





        // Get the NavController
        //val navController1 = findNavController()

        var previousDestinationId = -1 // Initialize with an invalid ID
        // Add destination change listener
        navController.addOnDestinationChangedListener { _, destination, _ ->




            if (destination.id == R.id.navigation_add) {
                if (previousDestinationId != R.id.navigation_garage) {
                    navController.navigate(R.id.navigation_garage)
                    previousDestinationId = R.id.navigation_garage
                }
            } else if (destination.id == R.id.navigation_vehicle) {
                    if (previousDestinationId != R.id.navigation_garage) {
                        navController.navigate(R.id.navigation_garage)
                        previousDestinationId = R.id.navigation_garage
                    }
            } else if (destination.id == R.id.navigation_about) {
                if (previousDestinationId != R.id.navigation_home) {
                    navController.navigate(R.id.navigation_home)
                    previousDestinationId = R.id.navigation_home
                }
            } else if (destination.id == R.id.navigation_settings) {
                if (previousDestinationId != R.id.navigation_home) {
                    navController.navigate(R.id.navigation_home)
                    previousDestinationId = R.id.navigation_home
                }
            }
            else {
                previousDestinationId = destination.id
            }
        }

    }
    // Function to get the current notification state from SharedPreferences
    fun getNotificationState(): Boolean {
        val sharedPreferences = getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("notifications_enabled", true) // Default to true if not found
    }

    // Function to update the notification state in SharedPreferences
    fun setNotificationState(enabled: Boolean) {
        val sharedPreferences = getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("notifications_enabled", enabled).apply()

        // Cancel pending notifications if notifications are disabled
        if (!enabled) {
           // cancelNotifications()
        }
    }

    // Function to cancel all pending notifications





}