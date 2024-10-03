package com.example.ead.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.ead.GlobalVariable
import com.example.ead.R
import com.example.ead.activities.CartActivity
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
    }

    // Inflating the layout for each item in the RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_item, parent, false)
        return OrderViewHolder(view)
    }

    // Binding data to the ViewHolder for each item
    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]
        holder.orderNumberTextView.text = "Order # : " + order.id
        holder.productsTextView.text = order.cart
        holder.totalAmountTextView.text = "Total Amount : $" + order.totalPrice.toString()
        holder.statusTextView.text = "Status : " + order.status

        // Hide edit and delete buttons if the status is "Dispatched"
        if (order.status == "Dispatched") {
            holder.editButton.visibility = View.GONE
            holder.deleteButton.visibility = View.GONE
        }else if (order.status == "Cancelled") {
            holder.editButton.visibility = View.GONE
            holder.deleteButton.visibility = View.GONE
        }else {
            holder.editButton.visibility = View.VISIBLE
            holder.deleteButton.visibility = View.VISIBLE
        }

        holder.deleteButton.setOnClickListener {
            order.id?.let { orderId ->
                updateOrderStatus(orderId)  // Pass the position of the clicked item
            }
        }

        holder.editButton.setOnClickListener{
            val intent = Intent(context, CartActivity1::class.java)
            intent.putExtra("cartId", order.vendorId)
            println(order)
            println("order.cart is"+ order.vendorId)
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
                    Toast.makeText(context, "Order cancelled successfully", Toast.LENGTH_SHORT).show()
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



