package com.example.ead.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ead.GlobalVariable
import com.example.ead.R
import com.example.ead.activities.CartActivity1
import com.example.ead.activities.OrdersActivity
import com.example.ead.models.Order
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

val baseUrl = GlobalVariable.BASE_URL

class OrderAdapter(private var orderList: List<Order>, private val context: Context) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    // ViewHolder class that holds the views for each list item
    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val orderNumberTextView: TextView = view.findViewById(R.id.orderNumberTextView)
        val productsTextView: TextView = view.findViewById(R.id.productsTextView)
        val totalAmountTextView: TextView = view.findViewById(R.id.totalAmountTextView)
        val statusTextView: TextView = view.findViewById(R.id.statusTextView)
        val editButton: ImageView = view.findViewById(R.id.editButtonOrder)
        val deleteButton: ImageView = view.findViewById(R.id.deleteIconOrder)
        val orderDateTextView: TextView = view.findViewById(R.id.orderDateTextView)
    }

    // Inflating the layout for each item in the RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_item, parent, false)
        return OrderViewHolder(view)
    }

    // Binding data to the ViewHolder for each item
    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]
        holder.orderNumberTextView.text = "Order # : " + order.id
        holder.productsTextView.text = order.cart
        holder.totalAmountTextView.text = "Total Amount : $" + order.totalPrice.toString()
        holder.statusTextView.text = "Status : " + order.status

        // Parse and format order date
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val parsedDate = ZonedDateTime.parse(order.orderDate).format(dateFormatter)
        holder.orderDateTextView.text = "Order Date : $parsedDate"

        // Set status text color based on the status
        when (order.status) {
            "Processing" -> holder.statusTextView.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.g_orange_yellow
                )
            )

            "Cancelled" -> holder.statusTextView.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.red
                )
            )

            "Dispatched" -> holder.statusTextView.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.dispatched
                )
            )

            "Completed" -> holder.statusTextView.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.g_green
                )
            )

            else -> holder.statusTextView.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.black
                )
            ) // Default
        }

        // Hide edit and delete buttons if the status is "Dispatched" or "Cancelled"
        if (order.status == "Dispatched" || order.status == "Cancelled" || order.status == "Completed") {
            holder.editButton.visibility = View.GONE
            holder.deleteButton.visibility = View.GONE
        } else {
            holder.editButton.visibility = View.VISIBLE
            holder.deleteButton.visibility = View.VISIBLE
        }

        // Handle delete button click
        holder.deleteButton.setOnClickListener {
            order.id?.let { orderId ->
                updateOrderStatus(orderId)
            }
        }

        // Handle edit button click
        holder.editButton.setOnClickListener {
            val intent = Intent(context, CartActivity1::class.java)
            intent.putExtra("cartId", order.vendorId)
            intent.putExtra("orderId", order.id)
            context.startActivity(intent)
        }
    }

    // Return the total number of items
    override fun getItemCount(): Int {
        return orderList.size
    }

    private fun updateOrderStatus(orderId: String) {
        // Coroutine for performing the network request on a background thread
        CoroutineScope(Dispatchers.IO).launch {
            val url = "$baseUrl/order/cancel/$orderId"
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .put(RequestBody.create(null, ByteArray(0)))  // Empty body
                .build()

            val response = client.newCall(request).execute()

            if (response.isSuccessful) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Order cancelled successfully", Toast.LENGTH_SHORT)
                        .show()
                    updateOrderList(orderList)
                    val refresh = Intent(context, OrdersActivity::class.java)
                    context.startActivity(refresh) // Pass the context explicitly
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Failed to cancel order", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun updateOrderList(newOrderList: List<Order>) {
        orderList = newOrderList
        notifyDataSetChanged()
    }
}
