package com.example.autoconnect.ui.social

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.autoconnect.R

class MessageAdapter(
    val messages: MutableList<Pair<String, String>>,
    private val currentUserUsername: String,
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

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
        val (senderUsername, message) = messages[position]
        holder.senderTextView.text = senderUsername
        holder.messageTextView.text = message

        // Check if the sender is the current user
        if (senderUsername == currentUserUsername) {
            // Set a different text color for the current user's message
            holder.messageTextView.backgroundTintList = ContextCompat.getColorStateList(holder.itemView.context, R.color.teal_200)
            (holder.messageTextView.layoutParams as? LinearLayout.LayoutParams)?.gravity = Gravity.END
            (holder.senderTextView.layoutParams as? LinearLayout.LayoutParams)?.gravity = Gravity.END

            //holder.messageTextView.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
        } else {

            holder.messageTextView.backgroundTintList = ContextCompat.getColorStateList(holder.itemView.context, R.color.teal_700)
            (holder.messageTextView.layoutParams as? LinearLayout.LayoutParams)?.gravity = Gravity.START
            (holder.senderTextView.layoutParams as? LinearLayout.LayoutParams)?.gravity = Gravity.START
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    fun addMessage(senderUsername: String, message: String) {
        messages.add(Pair(senderUsername, message))
        notifyItemInserted(messages.size - 1)
    }
}



