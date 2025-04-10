package com.example.deliveryapp

import android.os.Parcel
import android.os.Parcelable

data class Restaurant(var imageBase64:String="",var restaurantName:String="",var restaurantUrl:String=""):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt().toString(),
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(imageBase64)
        parcel.writeString(restaurantName)
        parcel.writeString(restaurantUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Restaurant> {
        override fun createFromParcel(parcel: Parcel): Restaurant {
            return Restaurant(parcel)
        }

        override fun newArray(size: Int): Array<Restaurant?> {
            return arrayOfNulls(size)
        }
    }
}