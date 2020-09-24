package com.aprilianta.cartdummy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Product (
    var id : Int? = null,
    var name : String? = null,
    var price : Int? = null,
    var image : Int? = null,
    var selectedQuantity : Int? = null
) : Parcelable