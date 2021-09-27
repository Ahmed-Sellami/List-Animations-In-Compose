package com.example.listanimationsincompose.ui

import android.util.Log
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toOffset
import androidx.compose.ui.zIndex
import com.example.listanimationsincompose.Main
import com.example.listanimationsincompose.R
import com.example.listanimationsincompose.model.Particle
import com.example.listanimationsincompose.model.ShoesArticle
import com.example.listanimationsincompose.model.SlideState
import com.example.listanimationsincompose.ui.theme.*
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

private val particlesRadii = mutableListOf<Float>()
private var itemHeight = 0
private var particleRadius = 0f
private var slotItemDifference = 0f

@ExperimentalAnimationApi
@Composable
fun ShoesCard(
    shoesArticle: ShoesArticle,
    slideState: SlideState,
    shoesArticles: MutableList<ShoesArticle>,
    updateSlidedState: (shoesArticle: ShoesArticle, slideState: SlideState) -> Unit,
    updateItemPosition: (currentIndex: Int, destinationIndex: Int) -> Unit
) {
    val itemHeightDp = dimensionResource(id = R.dimen.image_size)
    val slotPaddingDp = dimensionResource(id = R.dimen.slot_padding)
    with(LocalDensity.current) {
        itemHeight = itemHeightDp.toPx().toInt()
        particleRadius = dimensionResource(id = R.dimen.particle_radius).toPx()
        if (particlesRadii.isEmpty())
            particlesRadii.addAll(arrayOf(6.dp.toPx(), 10.dp.toPx(), 14.dp.toPx()))
        slotItemDifference = 18.dp.toPx()
    }
    val verticalTranslation by animateIntAsState(
        targetValue = when (slideState) {
            SlideState.UP -> -itemHeight
            SlideState.DOWN -> itemHeight
            else -> 0
        },
    )
    val isDragged = remember { mutableStateOf(false) }
    val zIndex = if (isDragged.value) 1.0f else 0.0f
    val rotation = if (isDragged.value) -5.0f else 0.0f
    val elevation = if (isDragged.value) 8.dp else 0.dp

    val currentIndex = remember { mutableStateOf(0) }
    val destinationIndex = remember { mutableStateOf(0) }

    val isPlaced = remember { mutableStateOf(false) }
    val leftParticlesRotation = remember { Animatable((Math.PI / 4).toFloat()) }
    val rightParticlesRotation = remember { Animatable((Math.PI * 3 / 4).toFloat()) }
    LaunchedEffect(isPlaced.value) {
        if (isPlaced.value) {
            launch {
                leftParticlesRotation.animateTo(
                    targetValue = Math.PI.toFloat(),
                    animationSpec = tween(durationMillis = 400)
                )
                leftParticlesRotation.snapTo((Math.PI / 4).toFloat())
            }
            launch {
                rightParticlesRotation.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 400)
                )
                rightParticlesRotation.snapTo((Math.PI * 3 / 4).toFloat())
                if (currentIndex.value != destinationIndex.value) {
                    updateItemPosition(currentIndex.value, destinationIndex.value)
                }
                isPlaced.value = false
            }
        }
    }

    val leftParticles =
        createParticles(leftParticlesRotation.value.toDouble(), shoesArticle.color, isLeft = true)
    val rightParticles =
        createParticles(rightParticlesRotation.value.toDouble(), shoesArticle.color, isLeft = false)

    Box(
        Modifier
            .padding(horizontal = 16.dp)
            .dragToReorder(
                shoesArticle,
                shoesArticles,
                itemHeight,
                updateSlidedState,
                { isDragged.value = true },
                { cIndex, dIndex ->
                    isDragged.value = false
                    isPlaced.value = true
                    currentIndex.value = cIndex
                    destinationIndex.value = dIndex
                }
            )
            .offset { IntOffset(0, verticalTranslation) }
            .zIndex(zIndex)
            .rotate(rotation)
    ) {
        Canvas(modifier = Modifier) {
            leftParticles.forEach {
                drawCircle(it.color, it.radius, center = IntOffset(it.x, it.y).toOffset())
            }
        }
        Canvas(modifier = Modifier.align(Alignment.TopEnd)) {
            rightParticles.forEach {
                drawCircle(it.color, it.radius, center = IntOffset(it.x, it.y).toOffset())
            }
        }
        Column(
            modifier = Modifier
                .shadow(elevation, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(
                    color = shoesArticle.color
                )
                .padding(slotPaddingDp)
                .align(Alignment.CenterStart)
                .fillMaxWidth()

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

private fun createParticles(rotation: Double, color: Color, isLeft: Boolean): List<Particle> {
    val particles = mutableListOf<Particle>()
    for (i in 0 until particlesRadii.size) {
        val currentParticleRadius = particleRadius * (i + 1) / particlesRadii.size
        val verticalOffset =
            (itemHeight.toFloat() - particlesRadii[i] - slotItemDifference + currentParticleRadius).toInt()
        val horizontalOffset = currentParticleRadius.toInt()
        particles.add(
            Particle(
                color = color.copy(alpha = (i + 1) / (particlesRadii.size).toFloat()),
                x = (particlesRadii[i] * cos(rotation)).toInt() + if (isLeft) horizontalOffset else -horizontalOffset,
                y = (particlesRadii[i] * sin(rotation)).toInt() + verticalOffset,
                radius = currentParticleRadius
            )
        )
    }
    return particles
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
            ),
            SlideState.NONE,
            mutableListOf(),
            { _, _ -> },
            { _, _ -> }
        )
    }
}