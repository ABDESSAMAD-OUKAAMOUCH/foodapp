package com.example.deliveryapp

import android.os.Parcel
import android.os.Parcelable

class Categories1(var image:Int,var name:String,var price:String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(image)
        parcel.writeString(name)
        parcel.writeString(price)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Categories1> {
        override fun createFromParcel(parcel: Parcel): Categories1 {
            return Categories1(parcel)
        }

        override fun newArray(size: Int): Array<Categories1?> {
            return arrayOfNulls(size)
        }
    }
}