package com.example.crudfirebase

import android.os.Parcel
import android.os.Parcelable

data class Luchador(
    var id: Int = 0,
    var nombre: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(nombre)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Luchador> {
        override fun createFromParcel(parcel: Parcel): Luchador = Luchador(parcel)
        override fun newArray(size: Int): Array<Luchador?> = arrayOfNulls(size)
    }

    override fun toString(): String = nombre
}