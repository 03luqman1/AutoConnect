package com.example.autoconnect.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.autoconnect.R
import com.example.autoconnect.StartActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_panel)

        val buttonViewFeedback = findViewById<Button>(R.id.buttonViewFeedback)
        buttonViewFeedback.setOnClickListener {
            // Handle the button click to go to the Login activity
            val intent = Intent(this, FeedbackActivity::class.java)
            startActivity(intent)
        }

        val buttonAddAdmin = findViewById<Button>(R.id.buttonAddAdmin)
        buttonAddAdmin.setOnClickListener {
            // Handle the button click to go to the Login activity
            val intent = Intent(this, AddAdminActivity::class.java)
            startActivity(intent)
        }

        val buttonManageSocialHub = findViewById<Button>(R.id.buttonManageSocialHub)
        buttonManageSocialHub.setOnClickListener {
            // Handle the button click to go to the Login activity
            val intent = Intent(this, ManageSocialActivity::class.java)
            startActivity(intent)
        }

        val buttonManageUsers = findViewById<Button>(R.id.buttonManageUsers)
        buttonManageUsers.setOnClickListener {
            // Handle the button click to go to the Login activity
            val intent = Intent(this, ManageAccountsActivity::class.java)
            startActivity(intent)
        }

        val buttonSignOutAdmin = findViewById<Button>(R.id.buttonSignOutAdmin)
        buttonSignOutAdmin.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(Intent(this, StartActivity::class.java))
            this.finish()
        }

    }

}