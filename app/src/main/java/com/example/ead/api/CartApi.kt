package com.example.ead.api

import com.example.ead.models.CartItem
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// Interface defining API endpoints for managing cart operations
interface CartApi {

    /**
     * Adds an item to the user's cart.
     *
     * @param userId The ID of the user whose cart the item will be added to.
     * @param item The CartItem object containing details of the item to be added.
     * @return A Call object wrapping a CartItem, which can be used to enqueue the request.
     */
    @POST("cart/{userId}/items")
    fun addToCart(@Path("userId") userId: String?, @Body item: CartItem?): Call<CartItem?>?

    /**
     * Retrieves the cart details for a specific user.
     *
     * @param userId The ID of the user whose cart details are to be fetched.
     * @return A Call object wrapping a List of CartItem, which can be used to enqueue the request.
     */
    @GET("cart/{userId}")
    fun getCartDetails(@Path("userId") userId: String): Call<List<CartItem>>
}
