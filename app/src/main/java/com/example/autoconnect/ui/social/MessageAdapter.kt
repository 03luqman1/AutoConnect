package com.example.autoconnect.ui.social

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.autoconnect.R

class MessageAdapter(val messages: MutableList<Pair<String, String>>) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val senderTextView: TextView = itemView.findViewById(R.id.textViewUsername)
        val messageTextView: TextView = itemView.findViewById(R.id.textViewMessage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val (senderId, message) = messages[position]
        holder.senderTextView.text = senderId
        holder.messageTextView.text = message
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    fun addMessage(senderId: String, message: String) {
        messages.add(Pair(senderId, message))
        notifyItemInserted(messages.size - 1)
    }
}

