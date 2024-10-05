package com.example.ead.api

import com.example.ead.models.Customer
import com.example.ead.models.LoginRequest
import com.example.ead.models.LoginResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CustomerApi {
    @POST("customer/register")
    suspend fun registerCustomer(@Body customer: Customer): Response<Customer>

    @POST("customer/login")

    suspend fun loginCustomer(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @PUT("customer/update/{id}")
    fun updateCustomer(
        @Path("id") id: String?,
        @Body customer: Customer
    ): Call<CustomerUpdateResponse>

    @DELETE("customer/delete/{email}")
    fun deactivateCustomer(@Path("email") email: String): Call<CustomerDeleteResponse>

}

data class CustomerUpdateResponse(
    val id: String,
    val email: String,
    val fullName: String,
    val mobileNumber: String,
    val address: String,
    val isActive: Boolean
)

data class CustomerDeleteResponse(
    val email: String,
    val status: String
)




