package com.example.ead.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ead.GlobalVariable
import com.example.ead.R
import com.example.ead.adapters.OrderAdapter
import com.example.ead.fragments.HomeFragment
import com.example.ead.models.Order
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

val baseUrl = GlobalVariable.BASE_URL

class OrdersActivity : AppCompatActivity() {

    private lateinit var buttonBack: ImageButton
    private lateinit var orderRecyclerView: RecyclerView
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var orderList: MutableList<Order>
    private lateinit var filteredOrderList: MutableList<Order> // To store filtered orders
    private lateinit var spinnerOrderStatus: Spinner
    private lateinit var client: OkHttpClient
    private lateinit var buttonCart: ImageButton
    private lateinit var buttonUser: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        // Initialize views
        buttonBack = findViewById(R.id.buttonBack)
        orderRecyclerView = findViewById(R.id.orderRecyclerView)
        spinnerOrderStatus = findViewById(R.id.spinnerOrderStatus)
        buttonCart = findViewById(R.id.buttonCart)

        buttonCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }


        // Set click listener for the back button
        buttonBack.setOnClickListener {
            startActivity(Intent(this, HomeFragment::class.java))

        }

        buttonUser = findViewById(R.id.buttonUser)

        buttonUser.setOnClickListener {
            showUserOptions(buttonUser)
        }

        // Set up RecyclerView
        orderRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize OkHttpClient
        client = OkHttpClient()

        // Fetch orders
        fetchOrders()
        println("navigated to order activity")

        // Set up Spinner listener
        spinnerOrderStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (::orderList.isInitialized) { // Check if orderList is initialized
                    val selectedStatus = parent?.getItemAtPosition(position).toString()
                    filterOrders(selectedStatus) // Call filterOrders when initialized
                } else {
                    Log.e("OrdersActivity", "Order list is not initialized yet.")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    private fun showUserOptions(view: View) {
        val popupMenu = PopupMenu(this, view)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.menu_user_options, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.menu_home -> {
                    // Navigate to HomeFragment
                    val intent = Intent(this, Main::class.java)
                    startActivity(intent)
                    true
                }

                R.id.menu_logout -> {
                    // Clear shared preferences and logout
                    logoutUser()
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }


    // Method to clear shared preferences and log out
    private fun logoutUser() {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()  // Clears all the saved data
        editor.apply()  // Apply changes

        // Start the LoginRegisterActivity
        val intent = Intent(this, LoginRegisterActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun fetchOrders() {
        println("called fetch orders")
        orderList = mutableListOf()  // Initialize orderList before adding elements

        CoroutineScope(Dispatchers.IO).launch {
            println("Called url")
            try {
                val userId = getCustomerId()
                if (userId != null) {
                    // Fetch orders for the customer
                    val ordersUrl = "$baseUrl/order/customer/$userId"
                    val ordersResponse = makeGetRequest(ordersUrl)
                    val ordersJsonArray = JSONArray(ordersResponse)

                    // Loop through each order
                    for (i in 0 until ordersJsonArray.length()) {
                        val orderJson = ordersJsonArray.getJSONObject(i)
                        val cartId = orderJson.getString("cart")
                        val totalPrice = orderJson.getDouble("totalPrice")
                        val status = orderJson.getString("status")
                        val products = fetchCartItems(cartId)
                        val shippingAddress = orderJson.getString("shippingAddress")
                        val customerId = orderJson.getString("customerId")
                        val vendorId = orderJson.getString("vendorId")
                        val orderDate = orderJson.getString("orderDate")
                        val paymentStatus = orderJson.getString("paymentStatus")
                        val notes = orderJson.getString("notes")


                        // Create an Order object and add it to the list
                        orderList.add(
                            Order(
                                orderJson.getString("id"),
                                customerId,
                                products.joinToString(", "),
                                cartId,
                                totalPrice,
                                shippingAddress,
                                orderDate,
                                status,
                                paymentStatus,
                                notes
                            )
                        )
                    }

                    // Set up the adapter with the fetched order list
                    withContext(Dispatchers.Main) {
                        orderAdapter =
                            OrderAdapter(orderList, this@OrdersActivity) // Pass the context here
                        orderRecyclerView.adapter = orderAdapter
                        Log.i("OrdersActivity fetched", "User ID found")
                    }

                } else {
                    Log.e("OrdersActivity", "User ID not found.")
                }
            } catch (e: Exception) {
                Log.e("OrdersActivity", "Error fetching orders: ${e.message}")
            }
        }
    }

    // Filter orders based on selected status
    private fun filterOrders(status: String) {
        filteredOrderList = if (status == "All") {
            orderList // Show all orders if "All" is selected
        } else {
            orderList.filter { it.status == status }.toMutableList() // Filter by status
        }

        // Update RecyclerView with the filtered list
        orderAdapter = OrderAdapter(filteredOrderList, this)
        orderRecyclerView.adapter = orderAdapter
    }

    private fun fetchCartItems(cartId: String): List<String> {
        val products = mutableListOf<String>()
        val cartUrl = "$baseUrl/cart/cart/$cartId"

        try {
            // Fetch cart details
            val cartResponse = makeGetRequest(cartUrl)
            val cartJson = JSONObject(cartResponse)
            val itemsArray = cartJson.getJSONArray("items")

            // Loop through each item and get product names
            for (j in 0 until itemsArray.length()) {
                val itemJson = itemsArray.getJSONObject(j)
                val productName = itemJson.getString("productName")
                products.add(productName)
            }
        } catch (e: Exception) {
            Log.e("OrdersActivity", "Error fetching cart items: ${e.message}")
        }
        return products
    }

    private fun makeGetRequest(url: String): String {
        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            return response.body?.string() ?: ""
        }
    }

    override fun onBackPressed() {
        Log.d("TAG", "Debug message")
        // Check if the fragment manager has a back stack
        if (supportFragmentManager.backStackEntryCount > 0) {
            Log.d("TAG", "If Debug message")
            supportFragmentManager.popBackStack() // Go back to previous fragment
        } else {
            Log.d("TAG", "Else Debug message")
            finish() // Close activity if no fragments are in the stack
        }
    }

    private fun getCustomerId(): String? {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("customer_id", null)
    }
}
