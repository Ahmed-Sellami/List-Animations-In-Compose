package com.example.listanimationsincompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.listanimationsincompose.model.ShoesArticle
import com.example.listanimationsincompose.ui.Particle
import com.example.listanimationsincompose.ui.ShoesCard
import com.example.listanimationsincompose.ui.theme.*

class MainActivity : ComponentActivity() {
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ListAnimationsInComposeTheme(true) {
                Home()
            }
        }
    }
}


val allShoesArticles = arrayOf(
    ShoesArticle(
        title = "Nike Air Max 270",
        price = 199.8f,
        width = "2X Wide",
        drawable = R.drawable.ic_shoes_1,
        color = Red
    ),
    ShoesArticle(
        title = "Nike Joyride Run V",
        price = 249.1f,
        width = "3X Wide",
        drawable = R.drawable.ic_shoes_2,
        color = Blue
    ),
    ShoesArticle(
        title = "Nike Space Hippie 04",
        price = 179.7f,
        width = "Extra Wide",
        drawable = R.drawable.ic_shoes_3,
        color = Purple
    )
)

@ExperimentalAnimationApi
@Composable
fun Home() {
    var isFired by remember { mutableStateOf(false) }
    var particleColor by remember { mutableStateOf(Color.White) }
    val colorsArray = arrayOf(Purple, Blue, Red)
    val shoesArticles = remember { mutableStateListOf<ShoesArticle>() }
    val isVisibleStates = remember {
        mutableStateMapOf<ShoesArticle, Boolean>()
            .apply {
                shoesArticles.map { shoesArticle ->
                    shoesArticle to false
                }.toMap().also {
                    putAll(it)
                }
            }
    }
    var addedArticle by remember { mutableStateOf(ShoesArticle()) }
    var id by remember { mutableStateOf(0) }
    Scaffold(
        topBar = {
            Box {
                Particle(
                    modifier = Modifier
                        .align(Alignment.BottomCenter),
                    isFired = isFired,
                    color = particleColor,
                    onCompleteAnim = {
                        isVisibleStates[addedArticle] = true
                        isFired = false
                    }
                )
                TopAppBar(
                    title = {
                        Text(text = "List Animations In Compose")
                    },
                    actions = {
                        IconButton(onClick = {
                            particleColor = colorsArray.random()
                            addedArticle =
                                allShoesArticles.first { it.color == particleColor }.copy(id = id)
                                    .also {
                                        id++
                                    }
                            shoesArticles.add(0, addedArticle)
                            isFired = true
                        }) {
                            Icon(Icons.Filled.AddCircle, contentDescription = null)
                        }
                    },
                    backgroundColor = MaterialTheme.colors.background
                )
            }
        }
    ) { innerPadding ->
        ShoesList(
            modifier = Modifier.padding(innerPadding),
            isVisibleStates = isVisibleStates,
            shoesArticles = shoesArticles
        )
    }
}

@ExperimentalAnimationApi
@Composable
fun ShoesList(
    modifier: Modifier,
    isVisibleStates: Map<ShoesArticle, Boolean>,
    shoesArticles: MutableList<ShoesArticle>
) {
    val lazyListState = rememberLazyListState()

    LazyColumn(
        state = lazyListState,
        modifier = modifier.padding(top = dimensionResource(id = R.dimen.list_top_padding))
    ) {
        items(shoesArticles.size) { index ->
            val shoesArticle = shoesArticles.getOrNull(index)
            if (shoesArticle != null) {
                key(shoesArticle) {
                    ShoesCard(shoesArticle = shoesArticle, isVisible = isVisibleStates[shoesArticle] == true)
                }
            }
        }
    }
}

@ExperimentalAnimationApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ListAnimationsInComposeTheme(true) {
        Home()
    }
}

@Composable
fun Main(isDarkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    ListAnimationsInComposeTheme(isDarkTheme) {
        // A surface container using the 'background' color from the theme
        Surface(color = MaterialTheme.colors.background) {
            content()
        }
    }
}