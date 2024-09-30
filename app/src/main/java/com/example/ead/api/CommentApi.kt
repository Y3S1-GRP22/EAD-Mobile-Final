package com.example.ead.api
import com.example.ead.models.Comment
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface CommentApi {
    @POST("comment")
    fun addComment(@Body comment: Comment): Call<Comment>
}
