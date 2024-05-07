package com.example.autoconnect.ui.social

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.autoconnect.Message
import com.example.autoconnect.R

class MessageAdapter(
    val messages: MutableList<Message>,
    private val currentUserUsername: String,
    private val adminUsernames: List<String>,
    private val onItemClick: (senderUsername: String, message: String, isCurrentUser: Boolean, messageId: String) -> Unit,
    private val onItemHold: (senderUsername: String, message: String, isCurrentUser: Boolean, messageId: String) -> Unit
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val senderTextView: TextView = itemView.findViewById(R.id.textViewUsername)
        val messageTextView: TextView = itemView.findViewById(R.id.textViewMessage)
        val addOnTextView: TextView = itemView.findViewById(R.id.textViewAddOn)
        val messageIdTextView: TextView = itemView.findViewById(R.id.textViewMessageId)
        val messageBubble: LinearLayout = itemView.findViewById(R.id.MessageBubble)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val (senderUsername, message) = messages[position]
                    val isCurrentUser = senderUsername == currentUserUsername
                    onItemClick(senderUsername, message, isCurrentUser, messageIdTextView.text.toString())
                }
            }
            itemView.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val (senderUsername, message) = messages[position]
                    val isCurrentUser = senderUsername == currentUserUsername
                    onItemHold(senderUsername, message, isCurrentUser, messageIdTextView.text.toString())
                }
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val (senderUsername, message, addOn, messageId) = messages[position]
        holder.senderTextView.text = senderUsername
        holder.messageTextView.text = message
        holder.addOnTextView.text = addOn
        holder.messageIdTextView.text = messageId

        // Check if the sender is the current user
        if (adminUsernames.contains(senderUsername)) {
            // Check if sender is one of the admin accounts
            holder.messageBubble.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.admin_rounded_background)
            holder.messageTextView.backgroundTintList = ContextCompat.getColorStateList(holder.itemView.context, R.color.transparent)
            (holder.messageTextView.layoutParams as? LinearLayout.LayoutParams)?.gravity = Gravity.CENTER
            (holder.messageTextView.layoutParams as? LinearLayout.LayoutParams)?.width = LinearLayout.LayoutParams.MATCH_PARENT
            (holder.senderTextView.layoutParams as? LinearLayout.LayoutParams)?.gravity = Gravity.START
            holder.addOnTextView.backgroundTintList = ContextCompat.getColorStateList(holder.itemView.context, R.color.transparent)
            (holder.addOnTextView.layoutParams as? LinearLayout.LayoutParams)?.gravity = Gravity.CENTER
            (holder.addOnTextView.layoutParams as? LinearLayout.LayoutParams)?.width = LinearLayout.LayoutParams.MATCH_PARENT

            // Set visibility based on addOnTextView's text
            if ((!addOn.isNullOrBlank())&&(addOn != "null")) {
                holder.messageTextView.visibility = View.GONE
                holder.addOnTextView.visibility = View.VISIBLE
            } else {
                holder.messageTextView.visibility = View.VISIBLE
                holder.addOnTextView.visibility = View.GONE
            }
        } else if (senderUsername == currentUserUsername) {
            // Set a different text color for the current user's message
            holder.messageBubble.background = null
            holder.messageTextView.backgroundTintList = ContextCompat.getColorStateList(holder.itemView.context, R.color.teal_200)
            (holder.messageTextView.layoutParams as? LinearLayout.LayoutParams)?.gravity = Gravity.END
            (holder.messageTextView.layoutParams as? LinearLayout.LayoutParams)?.width = LinearLayout.LayoutParams.WRAP_CONTENT
            (holder.senderTextView.layoutParams as? LinearLayout.LayoutParams)?.gravity = Gravity.END
            holder.addOnTextView.backgroundTintList = ContextCompat.getColorStateList(holder.itemView.context, R.color.teal_200)
            (holder.addOnTextView.layoutParams as? LinearLayout.LayoutParams)?.gravity = Gravity.END
            (holder.addOnTextView.layoutParams as? LinearLayout.LayoutParams)?.width = LinearLayout.LayoutParams.WRAP_CONTENT

            // Set visibility based on addOnTextView's text
            if ((!addOn.isNullOrBlank())&&(addOn != "null")) {
                holder.messageTextView.visibility = View.GONE
                holder.addOnTextView.visibility = View.VISIBLE
            } else {
                holder.messageTextView.visibility = View.VISIBLE
                holder.addOnTextView.visibility = View.GONE
            }
        } else {
            holder.messageBubble.background = null
            holder.messageTextView.backgroundTintList = ContextCompat.getColorStateList(holder.itemView.context, R.color.teal_700)
            (holder.messageTextView.layoutParams as? LinearLayout.LayoutParams)?.gravity = Gravity.START
            (holder.messageTextView.layoutParams as? LinearLayout.LayoutParams)?.width = LinearLayout.LayoutParams.WRAP_CONTENT
            (holder.senderTextView.layoutParams as? LinearLayout.LayoutParams)?.gravity = Gravity.START
            (holder.addOnTextView.layoutParams as? LinearLayout.LayoutParams)?.gravity = Gravity.START
            holder.addOnTextView.backgroundTintList = ContextCompat.getColorStateList(holder.itemView.context, R.color.teal_700)
            (holder.addOnTextView.layoutParams as? LinearLayout.LayoutParams)?.gravity = Gravity.START
            (holder.addOnTextView.layoutParams as? LinearLayout.LayoutParams)?.width = LinearLayout.LayoutParams.WRAP_CONTENT

            // Set visibility based on addOnTextView's text

            if ((!addOn.isNullOrBlank())&&(addOn != "null")) {
                    holder.messageTextView.visibility = View.GONE
                    holder.addOnTextView.visibility = View.VISIBLE
                } else {
                    holder.messageTextView.visibility = View.VISIBLE
                    holder.addOnTextView.visibility = View.GONE
                }

        }
    }


    override fun getItemCount(): Int {
        return messages.size
    }

    fun addMessage(senderUsername: String, message: String, addOn: String, messageId: String) {
        messages.add(Message(senderUsername, message, addOn, messageId))
        notifyItemInserted(messages.size - 1)
    }
}



