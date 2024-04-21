package com.example.autoconnect



// NotificationBroadcastReceiver.kt

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.autoconnect.NotificationHelper

class NotificationBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title")
        val message = intent.getStringExtra("message")

        NotificationHelper.sendNotification(context, title ?: "", message ?: "")
    }
}
