package com.example.e_commerce.model

data class Profile(
    val name: String? =  "",
    val phone: String = "",
    val address: String? = "",
    val role: String? = "user"
)
