package com.example.deliveryapp

data class DataOrder(
    var id: String = "",
    val name: String = "",
    val price: String = "",
    val imageBase64: String = "",
    val paymentType: String = ""
)
