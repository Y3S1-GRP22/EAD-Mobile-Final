package com.example.ead.activities


import android.annotation.SuppressLint
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
import com.example.ead.adapters.CartAdapter1
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

class CartActivity1 : AppCompatActivity() {
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter1
    private lateinit var totalAmountTextView: TextView
    private lateinit var checkoutButton: Button
    private lateinit var buttonBack: ImageButton
    private lateinit var cartItems: MutableList<CartItem>
    val baseUrl = GlobalVariable.BASE_URL
    lateinit var cartId : String



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart1)

        // Initialize cartId here
        cartId = intent.getStringExtra("cartId").toString()
        var orderId = intent.getStringExtra("orderId")


        if (cartId == null) {
            Toast.makeText(this, "Cart ID is missing", Toast.LENGTH_SHORT).show()
            // Optionally finish the activity if the cart ID is critical
            finish()
            return
        }

        // Initialize views
        cartRecyclerView = findViewById(R.id.cartRecyclerView)
        totalAmountTextView = findViewById(R.id.totalAmountTextView)
        checkoutButton = findViewById(R.id.checkoutButton)
        buttonBack = findViewById(R.id.buttonBack)

        // Set up RecyclerView
        cartItems = mutableListOf()
        cartAdapter = CartAdapter1(cartItems, this, this)
        cartRecyclerView.layoutManager = LinearLayoutManager(this)
        cartRecyclerView.adapter = cartAdapter

        updateTotalAmount()

        // Set click listeners
        buttonBack.setOnClickListener { onBackPressed() }

        checkoutButton.setOnClickListener {
            // Pass cart ID and total amount to CheckoutActivity
//            val totalAmount = calculateTotal()
            val intent = Intent(this, CheckoutActivity1::class.java)
            val totalAmount = calculateTotal()
            intent.putExtra("total_amount", totalAmount)
            println("tol amount in car t1 "+ totalAmount)
            intent.putExtra("cart_id", cartId) // Pass the cart ID
            intent.putExtra("order_id", orderId) // Pass the cart ID

            startActivity(intent)
        }

        // Fetch cart details
        fetchCartDetails(cartId)
    }


    private fun fetchCartDetails(cartId : String) {
        println("called fetch cart details"+cartId)
        CoroutineScope(Dispatchers.IO).launch {
            val url = "$baseUrl/cart/cart/$cartId"
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            updateTotalAmount()

            try {
                val response: Response = client.newCall(request).execute()
                println(response)
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
                        println("cart activity1 success")
                    }
                } else {
                    Log.e("CartActivity1", "Error fetching cart details: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("CartActivity1", "Exception: ${e.message}")
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


    override fun onResume() {
        super.onResume()
        // Recalculate total when the activity is resumed (e.g., after a refresh)
        updateTotalAmount()
    }



}
