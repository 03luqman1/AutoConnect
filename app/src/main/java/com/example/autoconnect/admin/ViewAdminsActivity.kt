package com.example.autoconnect.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.autoconnect.R
import com.example.autoconnect.UserDetails
import com.google.firebase.database.*

class ViewAdminsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UsersAdapter
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_accounts)

        recyclerView = findViewById(R.id.recyclerViewAccounts)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = UsersAdapter()
        recyclerView.adapter = adapter

        databaseReference = FirebaseDatabase.getInstance().reference.child("Admin")

        loadUsers()

        val textViewSubTitle = findViewById<TextView>(R.id.textViewAccountsTitle)
        textViewSubTitle.text = "View Admin Accounts"

        val buttonBack = findViewById<Button>(R.id.buttonBackFromViewAccounts)
        buttonBack.setOnClickListener {
            // Handle the button click to go to the Login activity
            val intent = Intent(this, AdminActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadUsers() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val usersList = mutableListOf<UserDetails>()
                for (userSnapshot in dataSnapshot.children) {
                    val userId = userSnapshot.key.toString()
                    val userName = userSnapshot.child("userName").getValue(String::class.java)
                    val fullName = userSnapshot.child("fullName").getValue(String::class.java)
                    val email = userSnapshot.child("email").getValue(String::class.java)
                    val phoneNumber = userSnapshot.child("phoneNumber").getValue(String::class.java)

                    if (userName != null && userId != null) {
                        val userDetails = UserDetails(userId, fullName ?: "", userName ?: "", email ?: "", phoneNumber ?: "")
                        usersList.add(userDetails)
                    }
                }
                adapter.setUsers(usersList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })
    }
}