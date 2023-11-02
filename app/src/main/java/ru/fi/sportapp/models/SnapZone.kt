package ru.fi.sportapp.models

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp

data class SnapZone(val centerX: Float, val centerY: Float) {
    fun isWithinSnapThreshold(offset: Offset, threshold: Dp): Boolean {
        val dx = offset.x - centerX
        val dy = offset.y - centerY
        return dx * dx + dy * dy <= threshold.value * threshold.value
    }
}