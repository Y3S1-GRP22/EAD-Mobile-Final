package com.example.ead.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ead.R
import com.example.ead.models.Order

class OrderAdapter(private var orderList: List<Order>) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    // ViewHolder class that holds the views for each list item
    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val orderNumberTextView: TextView = view.findViewById(R.id.orderNumberTextView)
        val productsTextView: TextView = view.findViewById(R.id.productsTextView)
        val totalAmountTextView: TextView = view.findViewById(R.id.totalAmountTextView)
        val statusTextView: TextView = view.findViewById(R.id.statusTextView)
        val editButton: ImageView = view.findViewById(R.id.editButton)
        val deleteButton: ImageView = view.findViewById(R.id.deleteIcon)
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
        holder.orderNumberTextView.text = order.orderNumber
        holder.productsTextView.text = order.products
        holder.totalAmountTextView.text = order.totalAmount
        holder.statusTextView.text = order.status

        // Hide edit and delete buttons if the status is "Dispatched"
        if (order.status == "Dispatched") {
            holder.editButton.visibility = View.GONE
            holder.deleteButton.visibility = View.GONE
        } else {
            holder.editButton.visibility = View.VISIBLE
            holder.deleteButton.visibility = View.VISIBLE
        }
    }

    // Return the total number of items
    override fun getItemCount(): Int {
        return orderList.size
    }

    // Method to update the order list and notify adapter
    fun updateOrderList(newOrderList: MutableList<Order>) {
        orderList = newOrderList
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }
}
