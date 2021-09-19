package com.example.listanimationsincompose.ui

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.horizontalDrag
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

fun Modifier.swipeToDelete(
    offsetX: Animatable<Float, AnimationVector1D>,
    maximumWidth: Float,
    onDeleted: () -> Unit
): Modifier = composed {
    pointerInput(Unit) {
        // Used to calculate a settling position of a fling animation.
        val decay = splineBasedDecay<Float>(this)
        // Wrap in a coroutine scope to use suspend functions for touch events and animation.
        coroutineScope {
            while (true) {
                // Wait for a touch down event.
                val pointerId = awaitPointerEventScope { awaitFirstDown().id }
                // Interrupt any ongoing animation of other items.
                offsetX.stop()
                // Prepare for drag events and record velocity of a fling.
                val velocityTracker = VelocityTracker()
                // Wait for drag events.
                awaitPointerEventScope {
                    horizontalDrag(pointerId) { change ->
                        if (change.positionChange().x > 0 || offsetX.value > 0f) {
                            val horizontalDragOffset = offsetX.value + change.positionChange().x
                            launch {
                                offsetX.snapTo(horizontalDragOffset)
                            }
                            // Record the velocity of the drag.
                            velocityTracker.addPosition(change.uptimeMillis, change.position)
                            // Consume the gesture event, not passed to external
                            change.consumePositionChange()
                        }
                    }
                }
                // Dragging finished. Calculate the velocity of the fling.
                var velocity = velocityTracker.calculateVelocity().x
                // Calculate the eventual position where the fling should settle
                // based on the current offset value and velocity
                val targetOffsetX = decay.calculateTargetValue(offsetX.value, velocity)
                // Set the upper and lower bounds so that the animation stops when it
                // reaches the edge.
                offsetX.updateBounds(
                    lowerBound = 0f,
                    upperBound = size.width.toFloat()
                )
                launch {
                    //  Slide back the element if the settling position does not go beyond
                    //  the size of the element. Remove the element if it does.
                    if (targetOffsetX.absoluteValue <= maximumWidth) {
                        // Not enough velocity; Slide back.
                        offsetX.animateTo(targetValue = 0f, initialVelocity = velocity)
                    } else {
                        if (velocity >= 0f) {
                            // If the velocity is low, we create a fake velocity to make the animation look smoother
                            if (velocity <= 500f) {
                                velocity = 2000f
                            }
                            // Enough velocity to slide away the element to the edge.
                            offsetX.animateDecay(velocity, decay)
                            // The element was swiped away.
                            onDeleted()
                        }
                    }
                }
            }
        }
    }
        .offset {
            // Use the animating offset value here.
            IntOffset(offsetX.value.roundToInt(), 0)
        }
}