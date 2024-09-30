package com.example.ead.models

data class Comment(
    val userId: String,
    val productId: String,
    val vendorId: String,
    val rating: Int,
    val comments: String
)
