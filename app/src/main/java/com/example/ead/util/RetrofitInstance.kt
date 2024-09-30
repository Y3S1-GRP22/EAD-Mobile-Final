package com.example.ead.util

import com.example.ead.GlobalVariable
import com.example.ead.api.CartApi
import com.example.ead.api.CommentApi
import com.example.ead.api.CustomerApi
import com.example.ead.api.ProductApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val baseUrl = GlobalVariable.BASE_URL

    private val BASE_URL = "$baseUrl/"
    private val loggingInterceptor = LoggingInterceptor()

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor) // Add the logging interceptor
        .build()

    val Customerapi: CustomerApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CustomerApi::class.java)
    }


    val Productapi: ProductApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProductApi::class.java)
    }

    val Cartapi: CartApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client) // Use the custom OkHttpClient
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CartApi::class.java)
    }


}
