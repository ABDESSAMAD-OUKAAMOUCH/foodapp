package com.example.deliveryapp

import android.os.Parcel
import android.os.Parcelable

data class Restaurant(
    var adminId: String = "",
    var id: String = "",
    var imageBase64: String = "",
    var restaurantName: String = "",
    var restaurantUrl: String = ""
)