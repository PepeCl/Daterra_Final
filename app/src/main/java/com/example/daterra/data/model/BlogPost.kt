package com.example.daterra.model

data class BlogPost(
    val id: Int,
    val category: String,
    val title: String,
    val excerpt: String,
    val readTime: String,
    val url: String,
)