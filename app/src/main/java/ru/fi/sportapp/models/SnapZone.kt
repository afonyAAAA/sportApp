package ru.fi.sportapp.models

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp

data class SnapZone(val offset: Offset, val position: Position) {
    fun isWithinSnapThreshold(offset: Offset, threshold: Dp): Boolean {
        val dx = offset.x - this.offset.x
        val dy = offset.y - this.offset.y
        return dx * dx + dy * dy <= threshold.value * threshold.value
    }
}