package com.example.nusupper

import java.io.Serializable
import java.time.Month

data class JioDetails (
    val title:String,
    val location:String,
    val restaurant:String,
    val dd:Int,
    val mm:Int,
    val yyyy: Int,
    val hour:Int,
    val min: Int,
    val username:String
    ): Serializable
