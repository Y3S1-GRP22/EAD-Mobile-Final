package com.example.ead.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ead.R
import com.example.ead.adapters.OrderAdapter
import com.example.ead.models.Order

class OrdersActivity : AppCompatActivity() {

    private lateinit var buttonBack: ImageButton
    private lateinit var orderRecyclerView: RecyclerView
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var orderList: MutableList<Order>
    private lateinit var spinnerOrderStatus: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        // Initialize views
        buttonBack = findViewById(R.id.buttonBack)
        orderRecyclerView = findViewById(R.id.orderRecyclerView)
        spinnerOrderStatus = findViewById(R.id.spinnerOrderStatus)

        // Set click listener for the back button
        buttonBack.setOnClickListener { onBackPressed() }

        // Set up RecyclerView
        orderRecyclerView.layoutManager = LinearLayoutManager(this)

        // Create a list of sample orders
//        orderList = mutableListOf(
//            Order("ORD001", "Product A, Product B", "$50", "Pending"),
//            Order("ORD002", "Product C, Product D", "$75", "Completed"),
//            Order("ORD003", "Product E", "$25", "Canceled"),
//            Order("ORD004", "Product F", "$30", "Dispatched")
//        )

        // Set up the adapter with the full order list
        orderAdapter = OrderAdapter(orderList)
        orderRecyclerView.adapter = orderAdapter

        // Add listener to the spinner to filter the orders based on status
        spinnerOrderStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedStatus = parent.getItemAtPosition(position).toString()
                filterOrdersByStatus(selectedStatus)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }

    private fun filterOrdersByStatus(status: String) {
        val filteredOrders = if (status == "All") {
            orderList // Show all orders if 'All' is selected
        } else {
            orderList.filter { it.status == status }
        }

        // Update the adapter with the filtered list
        orderAdapter.updateOrderList(filteredOrders.toMutableList())
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
}