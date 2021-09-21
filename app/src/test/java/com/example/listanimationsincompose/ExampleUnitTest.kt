package com.example.listanimationsincompose

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun sliding_isCorrect() {
        val offsetY = 10f
        val itemHeight = 100f
        val offsetToSlide = 10f
        val previousNumberOfItems = 1
        val numberOfItemsInOffset = (offsetY / itemHeight).toInt()
        val numberOfItemsPlusOffset = ((offsetY + offsetToSlide) / itemHeight).toInt()
        val numberOfItemsMinusOffset = ((offsetY - offsetToSlide - 1) / itemHeight).toInt()
        val numberOfItems = if (offsetY - offsetToSlide - 1 < 0) {
            0
        } else if (numberOfItemsPlusOffset > numberOfItemsInOffset && numberOfItemsMinusOffset == numberOfItemsInOffset) {
            numberOfItemsPlusOffset
        } else if(numberOfItemsMinusOffset < numberOfItemsInOffset && numberOfItemsPlusOffset == numberOfItemsInOffset) {
            numberOfItemsInOffset
        } else {
            previousNumberOfItems
        }
        assertEquals(0, numberOfItems)
    }
}