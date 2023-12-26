package com.boundless.GIGABET.wonders.models

import android.graphics.Bitmap

data class Image(
    val pathName : String = "",
    val image : Bitmap? = null,
    val visible : Boolean = false
)