package com.example.autoconnect

import android.app.Service
import android.util.Log
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Handle FCM messages here
        Log.d(TAG, "From: ${remoteMessage.from}")
        println("CLICCKKKKKKKKKKKKKKKKEEEEEEEEEEEEEEEEEEEEEEEEEEDDDDDDDDDDDDDDDDDD")
        Toast.makeText(this, "NOTIFICATION CLICKED!!", Toast.LENGTH_SHORT).show()
        // Check if message contains a notification payload
        remoteMessage.notification?.let {
            Log.d(TAG, "Notification Message Body: ${it.body}")
            // You can handle the notification here or pass it to a helper method
        }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
        // Implement this method to send token to your app server
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}

