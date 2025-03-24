package com.example.Bountees

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.Response
import retrofit2.http.Query

interface MyApi {
    @GET("comments")
//    fun getComments(): Call<List<Comments>>
    fun getComments(): Response<List<Comments>>
}