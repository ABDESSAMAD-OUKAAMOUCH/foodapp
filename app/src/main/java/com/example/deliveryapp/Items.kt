package com.example.deliveryapp

import java.io.Serializable

data class Item(
    val itemName: String = "",
    val price: Double = 0.0,
    val imageBase64: String = "",
    val description: String = ""
): Serializable