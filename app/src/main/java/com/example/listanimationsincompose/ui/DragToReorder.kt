package com.example.listanimationsincompose.ui

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
import kotlin.math.sign

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

                val offsetToSlide = itemHeight / 5
                var numberOfItems = 0
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
                            previousNumberOfItems = numberOfItems
                            val offsetSign = offsetY.value.sign.toInt()
                            numberOfItems = calculateNumberOfSlidedItems(
                                offsetY.value * offsetSign,
                                itemHeight,
                                offsetToSlide,
                                previousNumberOfItems
                            )
                            if (numberOfItems != 0) {
                                updateSlidedState(
                                    shoesArticles[shoesArticles.indexOf(shoesArticle) + numberOfItems * offsetSign],
                                    if (offsetSign == 1) SlideState.UP else SlideState.DOWN
                                )
                            }
                            if (previousNumberOfItems > numberOfItems) {
                                updateSlidedState(
                                    shoesArticles[shoesArticles.indexOf(shoesArticle) + previousNumberOfItems * offsetSign],
                                    SlideState.NONE
                                )
                            }
                            listOffset = numberOfItems * offsetSign
                        }
                        // Consume the gesture event, not passed to external
                        change.consumePositionChange()
                    }
                }
                if (numberOfItems == 0) {
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

private fun calculateNumberOfSlidedItems(
    offsetY: Float,
    itemHeight: Int,
    offsetToSlide: Int,
    previousNumberOfItems: Int
): Int {
    val numberOfItemsInOffset = (offsetY / itemHeight).toInt()
    val numberOfItemsPlusOffset = ((offsetY + offsetToSlide) / itemHeight).toInt()
    val numberOfItemsMinusOffset = ((offsetY - offsetToSlide - 1) / itemHeight).toInt()
    return if (offsetY - offsetToSlide - 1 < 0) {
        0
    } else if (numberOfItemsPlusOffset > numberOfItemsInOffset && numberOfItemsMinusOffset == numberOfItemsInOffset) {
        numberOfItemsPlusOffset
    } else if (numberOfItemsMinusOffset < numberOfItemsInOffset && numberOfItemsPlusOffset == numberOfItemsInOffset) {
        numberOfItemsInOffset
    } else {
        previousNumberOfItems
    }
}