package com.example.ead.activities

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ead.GlobalVariable
import com.example.ead.R
import com.example.ead.adapters.CommentAdapter
import com.example.ead.models.Comment
import com.example.ead.models.Product
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class CommentsActivity : AppCompatActivity() {

    private lateinit var commentRecyclerView: RecyclerView
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var commentsList: MutableList<Comment>
    val baseUrl = GlobalVariable.BASE_URL

    private val client = OkHttpClient() // OkHttp client instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        // Initialize RecyclerView
        commentRecyclerView = findViewById(R.id.commentRecyclerView)
        commentRecyclerView.layoutManager = LinearLayoutManager(this)

        // Fetch comments from API
        getCustomerId()?.let { fetchCommentsFromApi(it) }
    }

    private fun fetchCommentsFromApi(userId: String) {
        val url = "$baseUrl/comment/user/$userId"

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("CommentsActivity", "Error fetching comments: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this@CommentsActivity, "Failed to load comments", Toast.LENGTH_LONG).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.string()?.let { responseBody ->
                        val commentListType = object : TypeToken<List<Comment>>() {}.type
                        val comments = Gson().fromJson<List<Comment>>(responseBody, commentListType)

                        runOnUiThread {
                            commentsList = comments.toMutableList()
                            commentAdapter = CommentAdapter(this@CommentsActivity, commentsList)
                            commentRecyclerView.adapter = commentAdapter

                            // Fetch product details for each comment
                            commentsList.forEach { comment ->
                                fetchProductDetails(comment)
                            }
                        }
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@CommentsActivity, "Failed to load comments", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun fetchProductDetails(comment: Comment) {
        // Construct the URL for the product details API call
        val productUrl = "$baseUrl/products/${comment.productId}"

        val request = Request.Builder()
            .url(productUrl)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("CommentsActivity", "Error fetching product details: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    response.body?.string()?.let { responseBody ->
                        // Parse the product response
                        val product = Gson().fromJson(responseBody, Product::class.java)

                        Log.d("CommentsActivity success", product.toString())


                        // Update the comment with product name and image URL
                        comment.productName = product.name
                        comment.productImageUrl = product.imageUrl

                        // Update the RecyclerView on the main thread
                        runOnUiThread {
                            commentAdapter.notifyDataSetChanged() // Notify adapter of data change
                        }
                    }
                } else {
                    Log.e("CommentsActivity", "Failed to load product details for productId: ${comment.productId}")
                }
            }
        })
    }


    private fun getCustomerId(): String? {
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("customer_id", null)
    }
}
