package com.example.rxmvp

import io.reactivex.Single
import retrofit2.http.GET

interface MyApi {
    @GET("/posts")
    fun getPosts(): Single<List<Post>>
}