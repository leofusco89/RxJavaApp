package com.example.rxmvp

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class Post(
    var userId: Int,
    var id: Int?,
    var title: String,
    var body: String
)