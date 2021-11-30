package com.example.notetask

import android.os.Parcel
import android.os.Parcelable

data class taskDetails(
    var title:String,
    var note:String,
    var date:String,
    var time:String,
    var format:String
    )
