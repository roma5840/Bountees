package com.example.Bountees.Dashboard

import android.os.Parcel
import android.os.Parcelable


//data class DataClassDashboard(var title: String, var image: Int, var price: Int, var currency: String)
data class DataClassDashboard(var title: String, var image: String, var price: Int, var currency: String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        //parcel.readString()!! for var "price:Int
        parcel.readInt()!!,
        //
        parcel.readString()!!

    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(image)
        //parcel.writeString(price) for var price:int
        parcel.writeInt(price)
        //
        parcel.writeString(currency)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DataClassDashboard> {
        override fun createFromParcel(parcel: Parcel): DataClassDashboard {
            return DataClassDashboard(parcel)
        }

        override fun newArray(size: Int): Array<DataClassDashboard?> {
            return arrayOfNulls(size)
        }
    }
}
