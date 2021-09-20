package com.example.listanimationsincompose.ui

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.unit.IntOffset
import com.example.listanimationsincompose.model.ShoesArticle
import com.example.listanimationsincompose.model.SlideState
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

fun Modifier.dragToReorder(
    shoesArticle: ShoesArticle,
    shoesArticles: MutableList<ShoesArticle>,
    itemHeight: Int,
    updateSlidedState: (shoesArticle: ShoesArticle, slideState: SlideState) -> Unit,
    updateItemPosition: (shoesArticle: ShoesArticle, destinationIndex: Int) -> Unit
): Modifier = composed {
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    pointerInput(Unit) {
        // Wrap in a coroutine scope to use suspend functions for touch events and animation.
        coroutineScope {
            while (true) {
                // Wait for a touch down event.
                val pointerId = awaitPointerEventScope { awaitFirstDown().id }
                // Interrupt any ongoing animation of other items.
                offsetX.stop()
                offsetY.stop()

                var numberOfSlidedItems = 0
                // Wait for drag events.
                awaitPointerEventScope {
                    drag(pointerId) { change ->
                        val horizontalDragOffset = offsetX.value + change.positionChange().x
                        launch {
                            offsetX.snapTo(horizontalDragOffset)
                        }
                        val verticalDragOffset = offsetY.value + change.positionChange().y
                        launch {
                            offsetY.snapTo(verticalDragOffset)
                            numberOfSlidedItems = if (offsetY.value > 0) {
                                slideItemsFromBottomToTop(offsetY.value, itemHeight) { listOffset ->
                                    updateSlidedState(
                                        shoesArticles[shoesArticles.indexOf(shoesArticle) + listOffset],
                                        SlideState.UP
                                    )
                                }
                            } else {
                                -slideItemsFromTopToBottom(offsetY.value, itemHeight) { listOffset ->
                                    updateSlidedState(
                                        shoesArticles[shoesArticles.indexOf(shoesArticle) - listOffset],
                                        SlideState.DOWN
                                    )
                                }
                            }
                        }
                        // Consume the gesture event, not passed to external
                        change.consumePositionChange()
                    }
                }
                if (numberOfSlidedItems == 0) {
                    launch {
                        offsetX.animateTo(0f)
                    }
                    launch {
                        offsetY.animateTo(0f)
                    }
                } else {
                    val currentIndex = shoesArticles.indexOf(shoesArticle)
                    updateItemPosition(shoesArticle, currentIndex + numberOfSlidedItems)
                }
            }
        }
    }
        .offset {
            // Use the animating offset value here.
            IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt())
        }
}

private fun slideItemsFromBottomToTop(
    offsetY: Float,
    itemHeight: Int,
    slide: (listOffset: Int) -> Unit
): Int {
    var listOffset = 1
    var mutableOffsetY = offsetY
    while (mutableOffsetY - itemHeight >= 0) {
        slide(listOffset)
        listOffset++
        mutableOffsetY -= itemHeight
    }
    return listOffset - 1
}

private fun slideItemsFromTopToBottom(
    offsetY: Float,
    itemHeight: Int,
    slide: (listOffset: Int) -> Unit
): Int {
    var listOffset = 1
    var mutableOffsetY = offsetY
    while (mutableOffsetY + itemHeight <= 0) {
        slide(listOffset)
        listOffset++
        mutableOffsetY += itemHeight
    }
    return listOffset - 1
}