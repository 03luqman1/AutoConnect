package com.example.autoconnect.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.autoconnect.R

class RatingsAdapter : RecyclerView.Adapter<RatingsAdapter.ViewHolder>() {

    private var ratingsList: List<RatingItem> = ArrayList()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRating: TextView = itemView.findViewById(R.id.tvRating)
        val tvComments: TextView = itemView.findViewById(R.id.tvComments)
        val tvUserId: TextView = itemView.findViewById(R.id.tvUserId)
    }

    fun setRatings(ratings: List<RatingItem>) {
        ratingsList = ratings
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_rating, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rating = ratingsList[position]
        holder.tvRating.text = "Rating: ${rating.rating}"
        holder.tvComments.text = "Comments: ${rating.comments}"
        holder.tvUserId.text = "User ID: ${rating.userId}"
    }

    override fun getItemCount(): Int {
        return ratingsList.size
    }
}