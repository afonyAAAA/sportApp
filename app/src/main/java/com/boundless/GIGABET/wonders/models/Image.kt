package com.boundless.GIGABET.wonders.models

import android.graphics.Bitmap
import androidx.compose.runtime.Stable

@Stable
data class Image(
    val pathName : String = "",
    val image : Bitmap? = null,
    val visible : Boolean = false
)