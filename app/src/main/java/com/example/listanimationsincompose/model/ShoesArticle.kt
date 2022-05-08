package com.example.listanimationsincompose.model

import androidx.compose.ui.graphics.Color

data class ShoesArticle(
    var id: Int = 0,
    var title: String = "",
    var price: Float = 0f,
    var width: String = "",
    var drawable: Int = 0,
    var color: Color = Color.Transparent
) {
    companion object {
        var ID = 0
    }
}