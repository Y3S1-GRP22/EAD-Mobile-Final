package com.example.ead.api

import com.example.ead.models.Comment
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

// Interface defining API endpoints for managing comments
interface CommentApi {

    /**
     * Submits a new comment to the server.
     *
     * @param comment The Comment object containing the details of the comment to be added.
     * @return A Call object wrapping a Comment, which can be used to enqueue the request.
     *         The returned Comment object may contain the server's response, such as an ID or timestamp.
     */
    @POST("comment")
    fun addComment(@Body comment: Comment): Call<Comment>
}
