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
import kotlinx.coroutines.plus
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
                var previousNumberOfItems: Int
                var listOffset = 0
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
                            previousNumberOfItems = numberOfSlidedItems
                            numberOfSlidedItems = if (offsetY.value > 0) {
                                val numberOfItems = (offsetY.value / itemHeight).toInt()
                                if (numberOfItems != 0) {
                                    updateSlidedState(
                                        shoesArticles[shoesArticles.indexOf(shoesArticle) + numberOfItems],
                                        SlideState.UP
                                    )
                                }
                                if (previousNumberOfItems > numberOfItems) {
                                    updateSlidedState(
                                        shoesArticles[shoesArticles.indexOf(shoesArticle) + previousNumberOfItems],
                                        SlideState.NONE
                                    )
                                }
                                listOffset = numberOfItems
                                numberOfItems
                            } else {
                                val numberOfItems = (-offsetY.value / itemHeight).toInt()
                                if (numberOfItems != 0) {
                                    updateSlidedState(
                                        shoesArticles[shoesArticles.indexOf(shoesArticle) - numberOfItems],
                                        SlideState.DOWN
                                    )
                                }
                                if (previousNumberOfItems > numberOfItems) {
                                    updateSlidedState(
                                        shoesArticles[shoesArticles.indexOf(shoesArticle) - previousNumberOfItems],
                                        SlideState.NONE
                                    )
                                }
                                listOffset = -numberOfItems
                                numberOfItems
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
                    updateItemPosition(shoesArticle, currentIndex + listOffset)
                }
            }
        }
    }
        .offset {
            // Use the animating offset value here.
            IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt())
        }
}