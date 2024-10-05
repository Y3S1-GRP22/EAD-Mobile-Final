package com.example.ead.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ead.activities.ProductDetailActivity
import com.example.ead.R
import com.example.ead.models.CartItem
import com.squareup.picasso.Picasso

class CheckoutAdapter(private val cartItems: List<CartItem>) :
    RecyclerView.Adapter<CheckoutAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.checkout_item, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]
        holder.itemNameTextView.text = item.productName
        holder.itemPriceTextView.text = String.format("Price: $%.2f", item.price)
        holder.itemCountTextView.text = "Count: ${item.quantity}"

        // Load image using Picasso
        Picasso.get()
            .load(item.imagePath)
            .placeholder(R.drawable.logo_dark)
            .error(R.drawable.logo_dark)
            .into(holder.itemImageView)

        // Handle item click to open ProductDetailActivity
        holder.itemLayout.setOnClickListener {
            Log.d("Checkout Adapter", "Item clicked")

            // Create an Intent to start ProductDetailActivity
            val intent =
                Intent(holder.itemLayout.context, ProductDetailActivity::class.java).apply {
                    putExtra("productName", item.productName)
                    putExtra("productPrice", item.price)
                    putExtra("productImageUrl", item.imagePath)
                    putExtra("productCategory", "Default")
                    putExtra("productCount", item.quantity)

                }

            // Start the ProductDetailActivity
            holder.itemLayout.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemNameTextView: TextView = itemView.findViewById(R.id.itemNameTextView)
        val itemPriceTextView: TextView = itemView.findViewById(R.id.itemPriceTextView)
        val itemCountTextView: TextView = itemView.findViewById(R.id.itemCountTextView)
        val itemImageView: ImageView = itemView.findViewById(R.id.itemImageView)
        val itemLayout: LinearLayout = itemView.findViewById(R.id.itemLayout)
    }
}
