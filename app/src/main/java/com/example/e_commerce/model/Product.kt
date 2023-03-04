package com.example.e_commerce.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Product(
    val name: String? = "",
    val url: String? = "",
    val price: String? = "",
    val details: String? = "",
    val rating: String? = ""
): Parcelable
