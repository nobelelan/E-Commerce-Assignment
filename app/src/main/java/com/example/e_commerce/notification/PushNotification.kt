package com.example.e_commerce.notification

import com.example.e_commerce.notification.NotificationData

data class PushNotification(
    val data: NotificationData,
    val to: String
)
