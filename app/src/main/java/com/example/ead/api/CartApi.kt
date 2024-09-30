package com.example.ead.api

import com.example.ead.models.CartItem
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface CartApi {
    @POST("cart/{userId}/items")
    fun addToCart(@Path("userId") userId: String?, @Body item: CartItem?): Call<CartItem?>?

    @GET("cart/{userId}")
    fun getCartDetails(@Path("userId") userId: String): Call<List<CartItem>>



}

