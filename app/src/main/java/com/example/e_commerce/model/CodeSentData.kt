package com.example.e_commerce.model

import android.os.Parcelable
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CodeSentData(
    val verificationId: String,
    val phone: String,
    val token: ForceResendingToken
): Parcelable
