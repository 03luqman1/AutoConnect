package com.example.autoconnect.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.widget.ImageButton
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.autoconnect.R
import com.example.autoconnect.ui.social.MessageAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase


class ManageSocialActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var editTextMessage: EditText
    private lateinit var buttonSend: ImageButton
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var messageListener: ValueEventListener
    private lateinit var currentUserID: String
    private var currentUserUsername: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_social)


       /* val buttonBack = findViewById<Button>(R.id.buttonBackFromViewFeedback)
        buttonBack.setOnClickListener {
            // Handle the button click to go to the Login activity
            val intent = Intent(this, AdminActivity::class.java)
            startActivity(intent)
        }*/
        recyclerView = findViewById(R.id.recyclerViewMessages)
        editTextMessage = findViewById(R.id.editTextMessage)
        buttonSend = findViewById(R.id.buttonSend)

        firebaseAuth = Firebase.auth
        currentUserID = firebaseAuth.currentUser?.uid ?: ""
        database = FirebaseDatabase.getInstance().reference.child("Social")


        val adminUsernames = mutableListOf<String>()





        val databaseReference = FirebaseDatabase.getInstance().getReference("Admin")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (snapshot in dataSnapshot.children) {
                    val username = snapshot.child("userName").getValue(String::class.java)
                    if (username != null) {
                        adminUsernames.add(username)
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                //Log.e(TAG, "Failed to read admin usernames.", databaseError.toException())
            }
        })

        val adminRef = FirebaseDatabase.getInstance().reference.child("Admin")
        val currentUserID = firebaseAuth.currentUser?.uid
        if (currentUserID != null) {
            adminRef.child(currentUserID).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    currentUserUsername = snapshot.child("userName").getValue(String::class.java) ?: ""
                    // Initialize the adapter with an empty list and the current user ID
                    messageAdapter = MessageAdapter(mutableListOf(), currentUserUsername, adminUsernames,
                        { senderUsername, message ->
                            // Handle the click event here
                            AlertDialog.Builder(this@ManageSocialActivity)
                                .setTitle("Message from $senderUsername")
                                .setMessage(message)
                                .setPositiveButton("Delete") { dialog, _ ->
                                    deleteMessage(message)
                                    dialog.dismiss()
                                }
                                .setNegativeButton("Cancel") { dialog, _ ->
                                    dialog.dismiss()
                                }
                                .show()
                        }
                    ) { message ->
                        // Handle the delete click event here
                        deleteMessage(message)
                    }

                    recyclerView.apply {
                        layoutManager = LinearLayoutManager(context)
                        adapter = messageAdapter
                    }

                    buttonSend.setOnClickListener {
                        val messageContent = editTextMessage.text.toString()
                        if (messageContent.isNotBlank()) {
                            // Call the addMessage method to add the message to the adapter and Firebase
                            addMessage(messageContent)
                            // Clear the EditText
                            editTextMessage.text.clear()
                        }
                    }

                    // Set up listener to fetch messages from Firebase and update UI
                    messageListener = object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val messages = mutableListOf<Pair<String, String>>()
                            for (messageSnapshot in snapshot.children) {
                                val content = messageSnapshot.child("content").getValue(String::class.java)
                                val senderId = messageSnapshot.child("userName").getValue(String::class.java)
                                if (content != null && senderId != null) {
                                    messages.add(Pair(senderId, content))
                                }
                            }
                            messageAdapter.messages.clear()
                            messages.forEach { (senderId, content) ->
                                messageAdapter.addMessage(senderId, content)
                            }
                            messageAdapter.notifyDataSetChanged()
                            recyclerView.scrollToPosition(messageAdapter.itemCount - 1)
                        }


                        override fun onCancelled(error: DatabaseError) {
                            // Handle error
                        }
                    }

                    // Start listening for changes in the database
                    database.addValueEventListener(messageListener)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }

    }

    // Method to add a new message to the adapter and Firebase
    private fun addMessage(messageContent: String) {
        val adminRef = FirebaseDatabase.getInstance().reference.child("Admin")
        adminRef.child(currentUserID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userName = snapshot.child("userName").getValue(String::class.java)
                // Now you have the username, you can use it as needed
                val message = mapOf("content" to messageContent, "userName" to userName)
                database.push().setValue(message)
                    .addOnSuccessListener {
                        //Toast.makeText(this, "Message Sent", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        //Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show()
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun deleteMessage(message: String) {
        database.orderByChild("content").equalTo(message).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    childSnapshot.ref.removeValue()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }


}
