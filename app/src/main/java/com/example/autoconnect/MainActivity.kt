package com.example.autoconnect

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
            } else if (destination.id == R.id.navigation_review) {
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
}