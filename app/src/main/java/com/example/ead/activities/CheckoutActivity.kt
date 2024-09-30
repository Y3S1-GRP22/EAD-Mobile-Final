package com.example.ead.activities

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.example.ead.GlobalVariable
import com.example.ead.R
import com.example.ead.adapters.CheckoutAdapter
import com.example.ead.models.CartItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject

class CheckoutActivity : AppCompatActivity() {
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var checkoutAdapter: CheckoutAdapter
    private lateinit var totalAmountTextView: TextView
    private lateinit var payNowButton: Button
    private lateinit var buttonBack: ImageButton
    private var cartItems: MutableList<CartItem> = mutableListOf()
    private lateinit var usernameTextView: TextView
    private lateinit var addressTextView: TextView
    private lateinit var subtotalAmountTextView: TextView
    private lateinit var deliveryFeeAmountTextView: TextView
    val baseUrl = GlobalVariable.BASE_URL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        buttonBack = findViewById(R.id.buttonBack)
        buttonBack.setOnClickListener { onBackPressed() }

        cartRecyclerView = findViewById(R.id.cartRecyclerView)
        totalAmountTextView = findViewById(R.id.totalAmountTextView)
        payNowButton = findViewById(R.id.payNowButton)
        usernameTextView = findViewById(R.id.userNameTextView)
        addressTextView = findViewById(R.id.userAddressTextView)
        subtotalAmountTextView = findViewById(R.id.subtotalAmountTextView)
        deliveryFeeAmountTextView = findViewById(R.id.deliveryFeeAmountTextView)

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val username = sharedPreferences.getString("customer_name", "User Name")
        val address = sharedPreferences.getString("customer_address", "User Address")
        val userId = sharedPreferences.getString("customer_id", null)

        usernameTextView.text = username
        addressTextView.text = address

        cartRecyclerView.layoutManager = LinearLayoutManager(this)
        checkoutAdapter = CheckoutAdapter(cartItems)
        cartRecyclerView.adapter = checkoutAdapter

        if (userId != null) {
            fetchCartItems(userId)
        }

        // Set up checkout button listener
        payNowButton.setOnClickListener {
            // Handle checkout logic here
        }

        deliveryFeeAmountTextView.text = String.format("$5.00")

        val subtotal = intent.getDoubleExtra("total_amount", 0.0)
        val total = subtotal + 5
        totalAmountTextView.text = String.format("$$total")

    }

    // Method to fetch cart items from the API
    private fun fetchCartItems(userId: String) {
        val url = "$baseUrl/cart/$userId"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url(url)
                    .get()
                    .build()

                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    responseBody?.let {
                        parseCartItems(it)

                    }

                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@CheckoutActivity, "Failed to fetch cart items", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("CheckoutActivity", "Error fetching cart items: ${e.message}")
                    Toast.makeText(this@CheckoutActivity, "Error fetching cart items", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Method to parse cart items from the JSON response
    // Method to parse cart items from the JSON response
    private suspend fun parseCartItems(responseBody: String) {
        try {
            // Parse the main JSON object
            val jsonObject = JSONObject(responseBody)
            val itemsArray = jsonObject.getJSONArray("items") // Get the items array

            cartItems.clear() // Clear the list to prevent duplication

            // Loop through each item in the array
            for (i in 0 until itemsArray.length()) {
                val itemObject = itemsArray.getJSONObject(i)

                // Extract product details from the item object
                val id = itemObject.getString("id")
                val productId = itemObject.getString("productId")
                val productName = itemObject.getString("productName")
                val quantity = itemObject.getInt("quantity")
                val price = itemObject.getDouble("price")
                val imageUrl = itemObject.getString("imagePath") // Full URL for the image

                // Create a new CartItem object
                val cartItem = CartItem(id, productId, productName, quantity, price, imageUrl)
                cartItems.add(cartItem) // Add the item to the list
            }

            // Update the UI on the main thread
            withContext(Dispatchers.Main) {
                checkoutAdapter.notifyDataSetChanged() // Notify adapter to refresh RecyclerView

                // Calculate and display the total amount
                val subtotal = calculateTotal()
                subtotalAmountTextView.text = String.format("$%.2f", subtotal)

                // Now that the subtotal is updated, update the total amount
            }

        } catch (e: Exception) {
            Log.e("CheckoutActivity", "Error parsing cart items: ${e.message}")
        }
    }


    // Method to calculate the total price of the cart
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
            supportFragmentManager.popBackStack()
        } else {
            finish()
        }
    }


}
