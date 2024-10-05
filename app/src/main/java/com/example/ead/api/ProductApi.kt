package com.example.ead.api

import com.example.ead.models.Product
import retrofit2.Call
import retrofit2.http.GET

interface ProductApi {
    @GET("products")
    fun getProducts(): Call<List<Product>>
}
