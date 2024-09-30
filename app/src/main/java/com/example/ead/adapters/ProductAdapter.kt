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
import com.example.ead.R
import com.example.ead.activities.ProductDetailActivity
import com.example.ead.models.Product
import com.squareup.picasso.Picasso

class ProductAdapter(private val context: Context, private val productList: List<Product>) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {



    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_product_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        Log.d("productList", product.toString())

        holder.textViewName.text = product.name
        holder.textViewPrice.text = String.format("$%.2f", product.price)

        // Log the image URL to ensure it's correct
        Log.d("ProductAdapter", "Loading image: ${product.imageUrl}")
        Log.d("url path",product.imagePath)

        Picasso.get().load(product.imageUrl).into(holder.imageViewProduct)

        // Handle item click to open ProductDetailActivity
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ProductDetailActivity::class.java).apply {
                putExtra("productName", product.name)
                putExtra("productPrice", product.price.toDouble())
                println("product price is:"+ product.price.toDouble())
                putExtra("productImageUrl", product.imageUrl)
                putExtra("productCategory", product.categoryName)
                Log.d("catName",product.categoryName)
                putExtra("vendorName",product.vendorId)
                putExtra("description",product.description)
                putExtra("productId",product.id)
                Log.d("prod id",product.id.toString())
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
    }
}
