package com.example.listanimationsincompose.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.PathNode
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.listanimationsincompose.Main
import com.example.listanimationsincompose.R
import com.example.listanimationsincompose.model.ShoesArticle
import com.example.listanimationsincompose.ui.theme.*
import java.lang.Math.round
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin

@ExperimentalAnimationApi
@Composable
fun ShoesCard(shoesArticle: ShoesArticle, onDelete: () -> Unit) {
    val offsetX = remember { Animatable(0f) }

    val particleRadiusDp = dimensionResource(id = R.dimen.particle_radius)
    val particleRadius: Float
    val imageSizeDp = dimensionResource(id = R.dimen.image_size)
    val imageSize: Float
    val explosionParticleRadius: Float
    val explosionRadius: Float
    with(LocalDensity.current) {
        particleRadius = particleRadiusDp.toPx()
        imageSize = imageSizeDp.toPx()
        explosionParticleRadius = dimensionResource(id = R.dimen.explosion_particle_radius).toPx()
        explosionRadius = dimensionResource(id = R.dimen.explosion_radius).toPx()
    }
    Box {
        Canvas(
            Modifier.height(imageSizeDp)
        )
        {
            val radius = size.height * 0.5f
            translate(
                (offsetX.value - radius * 3f - particleRadius).coerceAtMost(0f)
            ) {
                drawPath(
                    path = drawSideShape(radius = radius, particleRadius = particleRadius * 3 / 4f),
                    color = shoesArticle.color
                )
            }
            translate(offsetX.value - particleRadius) {
                drawCircle(color = shoesArticle.color, radius = particleRadius)
            }
        }
        Canvas(modifier = Modifier
            .height(imageSizeDp)
            .offset {
                IntOffset(offsetX.value.roundToInt(), 0)
            })
        {
            val numberOfExplosionParticles = 10
            val particlePathAngle = Math.PI * 2 / numberOfExplosionParticles
            var angle = 0.0
            repeat(numberOfExplosionParticles / 2 + 1) {
                val hTranslation = cos(angle) * explosionRadius
                val vTranslation = sin(angle) * explosionRadius
                translate(hTranslation.toFloat(), vTranslation.toFloat()) {
                    drawCircle(Red, explosionParticleRadius)
                }
                if (vTranslation != -vTranslation) {
                    translate(hTranslation.toFloat(), -vTranslation.toFloat()) {
                        drawCircle(Red, explosionParticleRadius)
                    }
                }
                angle += particlePathAngle
            }
        }

        Box(
            Modifier
                .padding(horizontal = 16.dp)
                .swipeToDelete(offsetX) { onDelete() }
        ) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        color = shoesArticle.color
                    )
                    .padding(dimensionResource(id = R.dimen.slot_padding))
                    .align(Alignment.CenterStart)
                    .fillMaxWidth(),
            ) {
                Text(
                    shoesArticle.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .height(IntrinsicSize.Min),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = "$ ${shoesArticle.price}", fontSize = 14.sp, color = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Divider(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(1.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                        Text(shoesArticle.width, fontSize = 14.sp, color = Color.White)
                    }
                }
            }
            Image(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(imageSizeDp),
                painter = painterResource(id = shoesArticle.drawable),
                contentDescription = ""
            )
        }
    }
}

@ExperimentalAnimationApi
@Preview
@Composable
fun ShoesCardPreview() {
    Main(true) {
        ShoesCard(
            ShoesArticle(
                title = "Nike Air Max 270",
                price = 199.8f,
                width = "2X",
                drawable = R.drawable.ic_shoes_1,
                color = Purple
            )
        ) {}
    }
}


fun drawSideShape(radius: Float, particleRadius: Float): Path {
    return Path().apply {
        reset()
        // Top arc
        arcTo(
            rect = Rect(
                left = -particleRadius,
                top = -(radius + particleRadius),
                right = radius * 6f + particleRadius,
                bottom = radius - particleRadius
            ),
            startAngleDegrees = 180.0f,
            sweepAngleDegrees = -90.0f,
            forceMoveTo = false
        )
        // Bottom arc
        arcTo(
            rect = Rect(
                left = -particleRadius,
                top = radius + particleRadius,
                right = radius * 6 + particleRadius,
                bottom = radius * 3 + particleRadius
            ),
            startAngleDegrees = 270.0f,
            sweepAngleDegrees = -90.0f,
            forceMoveTo = false
        )
        close()
    }
}