package com.example.autoconnect.ui.social

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.autoconnect.Message
import com.example.autoconnect.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class SocialFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var editTextMessage: EditText
    private lateinit var buttonSend: ImageButton
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var messageListener: ValueEventListener
    private lateinit var currentUserID: String
    private var currentUserUsername: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_social, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewMessages)
        editTextMessage = view.findViewById(R.id.editTextMessage)
        buttonSend = view.findViewById(R.id.buttonSend)

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

        val usersRef = FirebaseDatabase.getInstance().reference.child("Users")
        val currentUserID = firebaseAuth.currentUser?.uid
        if (currentUserID != null) {
            usersRef.child(currentUserID).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    currentUserUsername = snapshot.child("userName").getValue(String::class.java) ?: ""
                    // Initialize the adapter with an empty list and the current user ID
                    messageAdapter = MessageAdapter(mutableListOf(), currentUserUsername, adminUsernames,
                        { senderUsername, message, isCurrentUser ,messageId->

                        },
                        { senderUsername, message, isCurrentUser, messageId ->
                            // Handle the click event here
                            if (isCurrentUser) {
                                if (message != "-") {
                                    context?.let {
                                        AlertDialog.Builder(it)
                                            .setTitle("Message from $senderUsername")
                                            .setMessage(message)
                                            .setPositiveButton("Edit") { _, _ ->
                                                // Handle edit
                                                editMessage(messageId, message)
                                            }
                                            .setNegativeButton("Delete") { dialog, _ ->
                                                deleteMessage(messageId)
                                                dialog.dismiss()
                                            }
                                            .setNeutralButton("Cancel") { dialog, _ ->
                                                dialog.dismiss()
                                            }
                                            .show()
                                    }
                                }
                            }
                        }
                    )

                    recyclerView.apply {
                        layoutManager = LinearLayoutManager(requireContext())
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
                            val context = context ?: return // Store context in a local variable
                            val messages = mutableListOf<Message>()
                            for (messageSnapshot in snapshot.children) {
                                val content = messageSnapshot.child("content").getValue(String::class.java)
                                val senderId = messageSnapshot.child("userName").getValue(String::class.java)
                                val addOn = messageSnapshot.child("addOn").getValue(String::class.java)
                                val messageId = messageSnapshot.key
                                if (content != null && senderId != null) {
                                    messages.add(Message(senderId, content, addOn, messageId))
                                }
                            }
                            messageAdapter.messages.clear()
                            messages.forEach { (senderId, content, addOn, messageId) ->
                                if (messageId != null) {
                                    messageAdapter.addMessage(senderId, content, addOn.toString(), messageId)
                                }
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

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Remove the listener when the view is destroyed
        if (::messageListener.isInitialized) {
            database.removeEventListener(messageListener)
        }
    }

    private fun addMessage(messageContent: String) {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users")
        usersRef.child(currentUserID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userName = snapshot.child("userName").getValue(String::class.java)
                // Now you have the username, you can use it as needed
                val message = mapOf("content" to messageContent, "userName" to userName)
                database.push().setValue(message)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Message Sent", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Failed to send message", Toast.LENGTH_SHORT).show()
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun editMessage(messageId: String, message: String) {

        val editDialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_message, null)
        val editTextMessage = editDialogView.findViewById<EditText>(R.id.editTextNewMessage)
        editTextMessage.setText(message)

        context?.let {
            AlertDialog.Builder(it)
                .setTitle("Edit Message")
                .setView(editDialogView)
                .setPositiveButton("Save") { _, _ ->
                    val newMessage = editTextMessage.text.toString().trim()
                    if (newMessage.isNotEmpty()) {
                        updateMessage(messageId, newMessage)
                    } else {
                        Toast.makeText(context, "Message cannot be empty", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun updateMessage(messageId: String, newMessage: String) {
        val editedMessage = "$newMessage"
        val addOn = "$newMessage (Edited)"
        val messageRef = database.child(messageId)
        messageRef.child("content").setValue(editedMessage)
        messageRef.child("addOn").setValue(addOn)
    }

    private fun deleteMessage(messageId: String) {
        val addOn = "(Deleted By User)"
        val messageRef = database.child(messageId)
        messageRef.child("content").setValue("-")
        messageRef.child("addOn").setValue(addOn)
    }
}

