package com.example.listanimationsincompose.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.listanimationsincompose.Main
import com.example.listanimationsincompose.ui.theme.Purple

fun drawFunnel(upperRadius: Float, lowerRadius: Float, width: Float): Path {
    return Path().apply {
        // Top arc
        arcTo(
            rect = Rect(
                left = -lowerRadius,
                top = -upperRadius - lowerRadius,
                right = width * 2 - lowerRadius,
                bottom = upperRadius - lowerRadius
            ),
            startAngleDegrees = 180.0f,
            sweepAngleDegrees = -90.0f,
            forceMoveTo = false
        )
        // Bottom arc
        arcTo(
            rect = Rect(
                left = -lowerRadius,
                top = upperRadius + lowerRadius,
                right = width * 2 - lowerRadius,
                bottom = upperRadius * 3 + lowerRadius
            ),
            startAngleDegrees = 270.0f,
            sweepAngleDegrees = -90.0f,
            forceMoveTo = false
        )
        close()
    }
}

@ExperimentalAnimationApi
@Preview
@Composable
fun FunnelPreview() {
    Main(true) {
        Canvas(modifier = Modifier.size(200.dp)) {
            drawPath(
                path = drawFunnel(size.height / 2, size.height / 10, size.width),
                color = Purple
            )
        }
    }
}