package com.example.ead.adapters


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.ead.GlobalVariable
import com.example.ead.R
import com.example.ead.activities.CartActivity
import com.example.ead.models.CartItem
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request


class CartAdapter(
    private var cartItems: MutableList<CartItem>,
    private val context: Context,
    private val activity: CartActivity // Add this line
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    val baseUrl = GlobalVariable.BASE_URL


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]
        holder.itemNameTextView.text = item.productName
        holder.itemPriceTextView.text = String.format("Price: $%.2f", item.price)
        holder.itemCountTextView.text = item.quantity.toString()
        holder.itemCountChangeTextView.text = item.quantity.toString()

        // Load image using Picasso
        Picasso.get()
            .load(item.imagePath)
            .placeholder(R.drawable.logo_dark)
            .error(R.drawable.logo_dark)
            .into(holder.itemImageView)

        // Set click listeners for the + and - buttons
        holder.plusButton.setOnClickListener {
            item.quantity += 1
            holder.itemCountChangeTextView.text = item.quantity.toString()

        }

        holder.minusButton.setOnClickListener {
            if (item.quantity > 0) {
                item.quantity -= 1
                holder.itemCountChangeTextView.text = item.quantity.toString()
            }
        }

        holder.editButton.setOnClickListener {
            val updatedQuantity = holder.itemCountChangeTextView.text.toString().toInt()
            val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val userId = sharedPreferences.getString("customer_id", null)

            if (userId != null) {
                updateCartItemQuantity(userId, item.id, updatedQuantity)
            } else {
                Toast.makeText(context, "User ID not found", Toast.LENGTH_SHORT).show()
            }
        }


        // Set up delete button
        holder.deleteButton.setOnClickListener {
            // Retrieve userId from SharedPreferences
            val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val userId = sharedPreferences.getString("customer_id", null)

            // Only proceed if userId is available
            if (userId != null) {
                deleteCartItem(userId, item.id, position)
            } else {
                Toast.makeText(context, "User ID not found", Toast.LENGTH_SHORT).show()
            }
        }

      
    }

    private fun updateCartItemQuantity(userId: String, itemId: String?, quantity: Int) {
        if (itemId == null || quantity <= 0) {
            Toast.makeText(context, "Invalid item ID or quantity", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            val url = "$baseUrl/cart/$userId/items/$itemId"
            val client = OkHttpClient()
            val requestBody = okhttp3.RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                quantity.toString() // Sending quantity as plain text in the request body
            )
            val request = Request.Builder()
                .url(url)
                .put(requestBody)
                .build()

            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        // Find the updated item and refresh it in the RecyclerView
                        val position = cartItems.indexOfFirst { it.id == itemId }
                        if (position != -1) {
                            cartItems[position].quantity = quantity
                            notifyItemChanged(position)

                        }
                        activity.updateTotalAmount()
                        Toast.makeText(context, "Item quantity updated successfully", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Failed to update item: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("CartAdapter", "Error updating item: ${e.message}")
                    Toast.makeText(context, "Error updating item", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    override fun getItemCount(): Int {
        return cartItems.size
    }

    // Method to delete an item from the cart
    private fun deleteCartItem(userId: String, itemId: String?, position: Int) {
        if (itemId == null) {
            Toast.makeText(context, "Invalid item ID", Toast.LENGTH_SHORT).show()
            return
        }

        // Coroutine for performing the network request on a background thread
        CoroutineScope(Dispatchers.IO).launch {
            val url = "$baseUrl/cart/$userId/items/$itemId"
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .delete() // HTTP DELETE request
                .build()

            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    // Update the UI on the main thread after successful deletion
                    withContext(Dispatchers.Main) {
                        // Remove the item from the cart list and notify the adapter
                        cartItems.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, cartItems.size)

                        activity.updateTotalAmount()


                        Toast.makeText(context, "Item deleted successfully", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Failed to delete item: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("CartAdapter", "Error deleting item: ${e.message}")
                    Toast.makeText(context, "Error deleting item", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    



    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemNameTextView: TextView = itemView.findViewById(R.id.itemNameTextView)
        val itemPriceTextView: TextView = itemView.findViewById(R.id.itemPriceTextView)
        val itemCountTextView: TextView = itemView.findViewById(R.id.itemCountTextView)
        val itemCountChangeTextView: TextView = itemView.findViewById(R.id.itemCountChangeTextView)
        val itemImageView: ImageView = itemView.findViewById(R.id.itemImageView)
        val plusButton: Button = itemView.findViewById(R.id.plusButton)
        val minusButton: Button = itemView.findViewById(R.id.minusButton)
        val itemLayout: LinearLayout = itemView.findViewById(R.id.itemLayout)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteIcon)
        val editButton: ImageView = itemView.findViewById(R.id.editButton)
    }


}
