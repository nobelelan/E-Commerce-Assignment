package com.example.e_commerce.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Product(
    val name: String? = "",
    val type: String? = "",
    val url: String? = "",
    val price: String? = "",
    val description: String? = "",
    val rating: String? = ""
): Parcelable
