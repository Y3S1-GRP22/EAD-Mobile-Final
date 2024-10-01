package com.example.ead.models

data class Order(
    val orderNumber: String,
    val products: String,  // List of Product objects
    val totalAmount: String,
    val status: String
)
