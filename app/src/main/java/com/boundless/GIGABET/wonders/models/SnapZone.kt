package com.boundless.GIGABET.wonders.models

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp

@Stable
data class SnapZone(val offset: Offset, val position: Position) {
    fun isWithinSnapThreshold(offset: Offset, threshold: Dp): Boolean {
        val dx = offset.x - this.offset.x
        val dy = offset.y - this.offset.y
        return dx * dx + dy * dy <= threshold.value * threshold.value
    }
}