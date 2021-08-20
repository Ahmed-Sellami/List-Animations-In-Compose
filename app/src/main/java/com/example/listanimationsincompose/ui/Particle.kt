package com.example.listanimationsincompose.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Switch
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.listanimationsincompose.ui.theme.Purple
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.ui.platform.LocalDensity
import com.example.listanimationsincompose.Main
import com.example.listanimationsincompose.R

@Composable
fun Particle(modifier: Modifier, isFired: Boolean, color: Color, onCompleteAnim: () -> Unit) {
    var radius: Float
    var topPadding: Float
    var imageSize: Float
    with(LocalDensity.current) {
        radius = dimensionResource(id = R.dimen.particle_radius).toPx()
        topPadding = dimensionResource(id = R.dimen.list_top_padding).toPx()
        imageSize = dimensionResource(id = R.dimen.image_size).toPx()
    }
    var topTranslation by remember { mutableStateOf(-radius) }

    Canvas(modifier) {
        translate(top = topTranslation) {
            drawCircle(
                color = color,
                radius = radius
            )
        }
    }
    val animatedTopTranslation = remember { Animatable(-radius) }
    LaunchedEffect(isFired) {
        if (isFired) {
            animatedTopTranslation.animateTo(
                targetValue = topPadding + imageSize / 2,
                animationSpec = spring(
                    stiffness = Spring.StiffnessLow
                )
            ) {
                topTranslation = value
            }
            animatedTopTranslation.snapTo(-radius)
            topTranslation = -radius
            onCompleteAnim()
        }
    }
}

@Preview(widthDp = 200, heightDp = 200)
@Composable
fun ParticlePreview() {
    Main(true) {
        var isVisible by remember { mutableStateOf(false) }
        Box(Modifier.fillMaxSize()) {
            Particle(
                modifier = Modifier.align(Alignment.Center),
                isFired = isVisible,
                color = Purple
            ) { }
        }
        Switch(checked = isVisible, onCheckedChange = { isVisible = !isVisible })
    }
}