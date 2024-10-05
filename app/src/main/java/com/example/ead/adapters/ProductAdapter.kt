package com.example.ead.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.example.ead.GlobalVariable
import com.example.ead.R
import com.example.ead.activities.ProductDetailActivity
import com.example.ead.models.Product
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import kotlin.concurrent.thread

class ProductAdapter(private val context: Context, private val productList: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private val client = OkHttpClient()
    val baseUrl = GlobalVariable.BASE_URL

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.adapter_product_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        Log.d("productList", product.toString())

        holder.textViewName.text = product.name
        holder.textViewPrice.text = String.format("$%.2f", product.price)
        holder.textViewVendor.text = product.vendorId

        // Load the product image
        Log.d("ProductAdapter", "Loading image: ${product.imageUrl}")
        Picasso.get().load(product.imageUrl).into(holder.imageViewProduct)

        // Fetch and set the product rating
        fetchProductRating(holder, product.id)

        // Handle item click to open ProductDetailActivity
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ProductDetailActivity::class.java).apply {
                putExtra("productName", product.name)
                putExtra("productPrice", product.price.toDouble())
                putExtra("productImageUrl", product.imageUrl)
                putExtra("productCategory", product.categoryName)
                putExtra("vendorName", product.vendorId)
                putExtra("description", product.description)
                putExtra("productId", product.id)
            }

            // Start new activity
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewName: TextView = itemView.findViewById(R.id.textViewProductName)
        val textViewPrice: TextView = itemView.findViewById(R.id.textViewProductPrice)
        val imageViewProduct: ImageView = itemView.findViewById(R.id.imageViewProduct)
        val textViewVendor: TextView = itemView.findViewById(R.id.textViewProductVendor)
        val textViewRating: TextView = itemView.findViewById(R.id.textViewProductRating)
    }

    private fun fetchProductRating(holder: ProductViewHolder, productId: String) {
        val url = "$baseUrl/comment/product/$productId/rating"
        val request = Request.Builder().url(url).build()

        thread {
            try {
                val response: Response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val rating = response.body?.string()?.toDouble()
                    // Update UI on the main thread
                    (context as? androidx.appcompat.app.AppCompatActivity)?.runOnUiThread {
                        holder.textViewRating.text = ("Rating: " + (rating?.toString() ?: "N/A"))
                    }
                } else {
                    Log.e("ProductAdapter", "Failed to fetch rating: ${response.message}")
                    // Update UI on the main thread
                    (context as? androidx.appcompat.app.AppCompatActivity)?.runOnUiThread {
                        holder.textViewRating.text = "Rating: N/A"
                    }
                }
            } catch (e: IOException) {
                Log.e("ProductAdapter", "Error fetching rating: ${e.message}")
                // Update UI on the main thread
                (context as? androidx.appcompat.app.AppCompatActivity)?.runOnUiThread {
                    holder.textViewRating.text = "Rating: N/A"
                }
            }
        }
    }


}
