package com.jcompanny.myshoppal.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User (
    val id:String ="",
    val firstname:String="",
    val lastName:String="",
    val email:String="",
    val image:String="",
    val mobile:Long=0,
    val gender:String="",
    val profileCompleted:Int=0
): Parcelable
