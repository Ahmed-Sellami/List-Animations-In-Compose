package com.example.listanimationsincompose.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateIntAsState
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
import com.example.listanimationsincompose.model.SlideState
import com.example.listanimationsincompose.ui.theme.*

@ExperimentalAnimationApi
@Composable
fun ShoesCard(
    shoesArticle: ShoesArticle,
    slideState: SlideState,
    shoesArticles: MutableList<ShoesArticle>,
    updateSlidedState: (shoesArticle: ShoesArticle, slideState: SlideState) -> Unit,
    updateItemPosition: (shoesArticle: ShoesArticle, destinationIndex: Int) -> Unit
) {
    val itemHeightDp = dimensionResource(id = R.dimen.image_size)
    val itemHeight: Int
    with(LocalDensity.current) {
        itemHeight = itemHeightDp.toPx().toInt()
    }
    val verticalTranslation by animateIntAsState(
        targetValue = when (slideState) {
            SlideState.UP -> -itemHeight
            SlideState.DOWN -> itemHeight
            else -> 0
        },
    )
    Box(
        Modifier
            .padding(horizontal = 16.dp)
            .dragToReorder(shoesArticle, shoesArticles, itemHeight, updateSlidedState, updateItemPosition)
            .offset { IntOffset(0, verticalTranslation) }
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