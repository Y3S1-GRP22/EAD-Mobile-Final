package com.example.ead.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ead.R
import com.example.ead.models.Comment
import com.squareup.picasso.Picasso

class CommentAdapter(
    private val context: Context,
    private val comments: List<Comment>
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]

        holder.itemNameTextView.text = comment.productName ?: "Unknown Product"
        holder.itemVendorTextView.text = comment.vendorId
        holder.itemCommentTextView.text = comment.comments
        holder.itemRatingBar.rating = comment.rating.toFloat() // Set the rating value in the RatingBar


        Picasso.get()
            .load(comment.productImageUrl)
            .placeholder(R.drawable.logo_dark)
            .error(R.drawable.logo_dark)
            .into(holder.itemImageView)
    }


    override fun getItemCount(): Int {
        return comments.size
    }

    // ViewHolder class to hold references to each view in the layout
    // ViewHolder class to hold references to each view in the layout
    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImageView: ImageView = itemView.findViewById(R.id.itemImageView)
        val itemNameTextView: TextView = itemView.findViewById(R.id.itemNameTextView)
        val itemVendorTextView: TextView = itemView.findViewById(R.id.itemVendorTextView)
        val itemCommentTextView: TextView = itemView.findViewById(R.id.itemCommentTextView)
        val itemRatingBar: RatingBar = itemView.findViewById(R.id.ratingBar) // Add this line
    }

}
