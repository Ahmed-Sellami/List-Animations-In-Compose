package com.example.listanimationsincompose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.listanimationsincompose.model.ShoesArticle
import com.example.listanimationsincompose.ui.ShoesCard
import com.example.listanimationsincompose.ui.theme.*

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalAnimationApi::class)
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
    val shoesArticles = remember { mutableStateListOf(*allShoesArticles) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "List Animations In Compose")
                },
                actions = {
                    IconButton(onClick = { shoesArticles.addAll(allShoesArticles) }) {
                        Icon(Icons.Filled.AddCircle, contentDescription = null)
                    }
                },
                backgroundColor = MaterialTheme.colors.background
            )
        }
    ) { innerPadding ->
        ShoesList(
            modifier = Modifier.padding(innerPadding),
            shoesArticles = shoesArticles,
            onDelete = { shoesArticles.remove(it) }
        )
    }
}

@ExperimentalAnimationApi
@Composable
fun ShoesList(
    modifier: Modifier,
    shoesArticles: MutableList<ShoesArticle>,
    onDelete: (shoesArticle: ShoesArticle) -> Unit
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
                    ShoesCard(shoesArticle = shoesArticle) {
                        onDelete(shoesArticle)
                    }
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