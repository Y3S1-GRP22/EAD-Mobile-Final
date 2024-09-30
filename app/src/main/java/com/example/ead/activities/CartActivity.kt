package com.example.ead.activities


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ead.GlobalVariable
import com.example.ead.R
import com.example.ead.adapters.CartAdapter
import com.example.ead.models.CartItem
import com.example.ead.models.CartResponse // Create this model class
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class CartActivity : AppCompatActivity() {
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var totalAmountTextView: TextView
    private lateinit var clearCart: TextView
    private lateinit var checkoutButton: Button
    private lateinit var buttonBack: ImageButton
    private lateinit var cartItems: MutableList<CartItem>
    val baseUrl = GlobalVariable.BASE_URL


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        // Initialize views
        cartRecyclerView = findViewById(R.id.cartRecyclerView)
        totalAmountTextView = findViewById(R.id.totalAmountTextView)
        checkoutButton = findViewById(R.id.checkoutButton)
        clearCart = findViewById(R.id.clearCart)
        buttonBack = findViewById(R.id.buttonBack)

        // Set up RecyclerView
        cartItems = mutableListOf()
        cartAdapter = CartAdapter(cartItems,this,this)
        cartRecyclerView.layoutManager = LinearLayoutManager(this)
        cartRecyclerView.adapter = cartAdapter

        updateTotalAmount()


        // Set click listeners
        buttonBack.setOnClickListener { onBackPressed() }
        clearCart.setOnClickListener {
            // Call the method to clear the cart
            clearCartItemsFromServer()
        }
        checkoutButton.setOnClickListener {
            val intent = Intent(this, CheckoutActivity::class.java)
            val totalAmount = calculateTotal() // Get the total amount
            intent.putExtra("total_amount", totalAmount) // Pass total amount to the next activity
            startActivity(intent)
        }


        // Fetch cart details
        fetchCartDetails()
    }

    // Method to clear cart from server
    private fun clearCartItemsFromServer() {
        CoroutineScope(Dispatchers.IO).launch {
            val userId = getCustomerId() // Get the userId from SharedPreferences
            if (userId == null) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CartActivity, "User ID not found", Toast.LENGTH_SHORT).show()
                }
                return@launch
            }

            val url = "$baseUrl/cart/$userId/clear"
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .delete() // HTTP DELETE request
                .build()

            try {
                val response: Response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        // Clear the UI after successful deletion
                        clearCartItems()
                        Toast.makeText(this@CartActivity, "Cart cleared successfully", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@CartActivity, "Failed to clear cart", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("CartActivity", "Error clearing cart: ${e.message}")
                    Toast.makeText(this@CartActivity, "Error clearing cart", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Method to clear cart items in the UI
    private fun clearCartItems() {
        cartItems.clear()
        cartAdapter.notifyDataSetChanged()
        updateTotalAmount()
//        updateButton.visibility = View.GONE
    }

    private fun fetchCartDetails() {
        CoroutineScope(Dispatchers.IO).launch {
            val userId = getCustomerId() // Replace with the actual cart ID
            val url = "$baseUrl/cart/$userId"
            println("url is" + url)
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            updateTotalAmount()

            try {
                val response: Response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    responseData?.let {
                        val cartResponse = Gson().fromJson(it, CartResponse::class.java)
                        withContext(Dispatchers.Main) {
                            // Update cartItems and notify the adapter
                            cartItems.clear()
                            cartItems.addAll(cartResponse.items)
                            cartAdapter.notifyDataSetChanged()

                            // Update the total amount after cart details are fetched
                            updateTotalAmount()
                        }
                        println("cart activity success")
                    }
                } else {
                    Log.e("CartActivity", "Error fetching cart details: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("CartActivity", "Exception: ${e.message}")
            }
        }
    }






     fun updateTotalAmount() {
        val total = calculateTotal()
        totalAmountTextView.text = String.format("Total: $%.2f", total)
    }

    private fun calculateTotal(): Double {
        var total = 0.0
        for (item in cartItems) {
            total += item.price * item.quantity
        }
        return total
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack() // Go back to previous fragment
        } else {
            finish() // Close activity if no fragments are in the stack
        }
    }

    private fun getCustomerId(): String? {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("customer_id", null)
    }

    override fun onResume() {
        super.onResume()
        // Recalculate total when the activity is resumed (e.g., after a refresh)
        updateTotalAmount()
    }



}
