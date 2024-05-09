package com.example.autoconnect.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.autoconnect.R
import com.example.autoconnect.UserDetails

class UsersAdapter : RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    private var usersList: List<UserDetails> = ArrayList()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
        val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
        val tvPhoneNumber: TextView = itemView.findViewById(R.id.tvPhoneNumber)
    }

    fun setUsers(users: List<UserDetails>) {
        usersList = users
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = usersList[position]
        holder.tvName.text = "Name: ${user.fullName}"
        holder.tvUserName.text = "Username: ${user.userName}"
        holder.tvEmail.text = "Email: ${user.email}"
        holder.tvPhoneNumber.text = "Phone Number: ${user.phoneNumber}"

    }

    override fun getItemCount(): Int {
        return usersList.size
    }
}