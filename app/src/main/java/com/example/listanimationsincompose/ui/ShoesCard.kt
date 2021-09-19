package com.example.listanimationsincompose.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.listanimationsincompose.Main
import com.example.listanimationsincompose.R
import com.example.listanimationsincompose.model.ShoesArticle
import com.example.listanimationsincompose.ui.theme.*
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@ExperimentalAnimationApi
@Composable
fun ShoesCard(shoesArticle: ShoesArticle, onDeleted: () -> Unit) {
    val particleRadiusDp = dimensionResource(id = R.dimen.particle_radius)
    val particleRadius: Float
    val itemHeightDp = dimensionResource(id = R.dimen.image_size)
    val itemHeight: Float
    val explosionParticleRadius: Float
    val explosionRadius: Float
    with(LocalDensity.current) {
        particleRadius = particleRadiusDp.toPx()
        itemHeight = itemHeightDp.toPx()
        explosionParticleRadius = dimensionResource(id = R.dimen.explosion_particle_radius).toPx()
        explosionRadius = dimensionResource(id = R.dimen.explosion_radius).toPx()
    }
    val screenWidth: Int
    with(LocalConfiguration.current) {
        screenWidth = this.screenWidthDp
    }
    val radius = itemHeight * 0.5f
    val funnelWidth = radius * 3
    val sideShapeWidth = funnelWidth + particleRadius * 2

    val offsetX = remember { Animatable(0f) }

    val explosionPercentage = remember { mutableStateOf(0f) }

    val funnelInitialTranslation = -funnelWidth - particleRadius
    val funnelTranslation = remember { mutableStateOf(funnelInitialTranslation) }
    funnelTranslation.value = (offsetX.value + funnelInitialTranslation).negateIfPositive {
        explosionPercentage.value = (offsetX.value + funnelInitialTranslation) / screenWidth
    }

    Box {
        Canvas(
            Modifier.height(itemHeightDp)
        ) {
            translate(funnelTranslation.value) {
                drawPath(
                    path = drawFunnel(
                        upperRadius = radius,
                        lowerRadius = particleRadius * 3 / 4f,
                        width = funnelWidth
                    ),
                    color = shoesArticle.color
                )
            }
            translate(offsetX.value - particleRadius) {
                drawCircle(color = shoesArticle.color, radius = particleRadius)
            }
        }
        Canvas(modifier = Modifier
            .height(itemHeightDp)
            .offset {
                IntOffset(
                    (offsetX.value.roundToInt() - 2 * particleRadius.toInt()).coerceAtMost(
                        funnelWidth.toInt()
                    ), 0
                )
            })
        {
            val numberOfExplosionParticles = 10
            val particleAngle = Math.PI * 2 / numberOfExplosionParticles
            var angle = 0.0
            repeat(numberOfExplosionParticles / 2 + 1) {
                val hTranslation = (cos(angle).toFloat() * explosionRadius) * explosionPercentage.value
                val vTranslation = (sin(angle).toFloat() * explosionRadius) * explosionPercentage.value

                translate(hTranslation, vTranslation) {
                    drawCircle(
                        color = shoesArticle.color,
                        radius = explosionParticleRadius,
                        alpha = explosionPercentage.value / 2
                    )
                }
                if (angle != 0.0 && angle != Math.PI) {
                    translate(hTranslation, -vTranslation) {
                        drawCircle(
                            color = shoesArticle.color,
                            radius = explosionParticleRadius,
                            alpha = explosionPercentage.value / 2
                        )
                    }
                }
                angle += particleAngle
            }
        }

        Box(
            Modifier
                .padding(horizontal = 16.dp)
                .swipeToDelete(offsetX, maximumWidth = sideShapeWidth) {
                    onDeleted()
                }
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
                    .size(itemHeightDp),
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

private fun Float.negateIfPositive(onPositive: () -> Unit): Float {
    return if (this > 0) {
        onPositive()
        -this
    } else this
}