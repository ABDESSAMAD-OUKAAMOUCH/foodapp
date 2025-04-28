package com.example.deliveryapp

import java.security.Timestamp

data class DataOrder(
    var id: String = "",
    var userId: String? = null,
    var adminId:String?=null,
    var restaurantId: String? = null,// ← أضف هذا
    val name: String = "",
    var price: Double = 0.0,
    var phone: String = "",
    val imageBase64: String = "",
    val paymentType: String = "",
//    val timestamp: Timestamp? = null,
    var status:String="",
    var quantity: Int = 1,
    var location: Map<String, Any>? = null // ← أضف هذا
)