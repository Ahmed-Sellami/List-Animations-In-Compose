package com.example.listanimationsincompose.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.listanimationsincompose.Main
import com.example.listanimationsincompose.R
import com.example.listanimationsincompose.model.ShoesArticle
import com.example.listanimationsincompose.ui.theme.*
import kotlin.math.hypot

var maxRadiusPx = 0f

@ExperimentalAnimationApi
@Composable
fun ShoesCard(shoesArticle: ShoesArticle, isVisible: Boolean) {
    val particleRadius: Float
    with(LocalDensity.current) {
        particleRadius = dimensionResource(id = R.dimen.particle_radius).toPx()
    }
    var radius by remember { mutableStateOf(particleRadius) }
    var visibilityAlpha by remember { mutableStateOf(0f) }

    Box(
        Modifier
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(
                    color = Color.Transparent
                )
                .onGloballyPositioned { coordinates ->
                    if (maxRadiusPx == 0f) {
                        maxRadiusPx = hypot(coordinates.size.width / 2f, coordinates.size.height / 2f)
                    }
                }
                .drawBehind {
                    drawCircle(
                        color = if (isVisible) shoesArticle.color else Color.Transparent,
                        radius = radius
                    )
                }
                .padding(dimensionResource(id = R.dimen.slot_padding))
                .align(Alignment.CenterStart)
                .fillMaxWidth(),
        ) {
            Text(
                shoesArticle.title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White,
                modifier = Modifier.alpha(visibilityAlpha)
            )
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .height(IntrinsicSize.Min)
                    .alpha(visibilityAlpha),
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
                .size(dimensionResource(id = R.dimen.image_size))
                .alpha(visibilityAlpha),
            painter = painterResource(id = shoesArticle.drawable),
            contentDescription = ""
        )
    }

    val animatedRadius = remember { Animatable(particleRadius) }
    val animatedAlpha = remember { Animatable(0f) }

    LaunchedEffect(isVisible) {
        if (isVisible) {
            animatedRadius.animateTo(maxRadiusPx, animationSpec = tween()) {
                radius = value
            }
            animatedAlpha.animateTo(1f, animationSpec = tween()) {
                visibilityAlpha = value
            }
        }
    }
}

@ExperimentalAnimationApi
@Preview
@Composable
fun ShoesCardPreview() {
    Main(true) {
        var isVisible by remember { mutableStateOf(false) }
        ShoesCard(
            ShoesArticle(
                title = "Nike Air Max 270",
                price = 199.8f,
                width = "2X",
                drawable = R.drawable.ic_shoes_1,
                color = Purple
            ),
            isVisible
        )
        Switch(checked = isVisible, onCheckedChange = { isVisible = !isVisible })
    }
}