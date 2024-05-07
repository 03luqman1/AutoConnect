package com.example.autoconnect

// Message.kt
data class Message(
    val content: String,
    val senderUsername: String,
    val timestamp: String?,
    val messageId: String?
)
