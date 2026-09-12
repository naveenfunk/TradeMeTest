package nz.co.trademetest.feature.discover.ui.home

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import nz.co.trademetest.core.theme.TradeMeTestTheme
import nz.co.trademetest.feature.discover.DiscoverTopAppBar

@Composable
fun DiscoverScreen(
    modifier: Modifier = Modifier,
    viewModel: DiscoverViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(viewModel, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effects.collect { effect ->
                when (effect) {
                    is DiscoverEffect.ShowMessage ->
                        Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    DiscoverScreen(
        state = state,
        onIntent = remember(viewModel) { viewModel::onIntent },
        modifier = modifier,
    )
}

@Composable
fun DiscoverScreen(
    state: DiscoverUiState,
    onIntent: (DiscoverIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            DiscoverTopAppBar(
                onSearchClick = { onIntent(DiscoverIntent.SearchClicked) },
                onCartClick = { onIntent(DiscoverIntent.CartClicked) },
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding,
        ) {
            items(items = state.items, key = { it.id }) { item ->
                DiscoverItemRow(
                    item = item,
                    onClick = {
                        onIntent(
                            DiscoverIntent.ItemClicked(
                                item.id
                            )
                        )
                    },
                )
                HorizontalDivider()
            }
        }
    }
}

private val PreviewItems = listOf(
    DiscoverItemUi(
        id = "1",
        imageUrl = "https://example.com/image1.jpg",
        location = "Auckland City",
        title = "Vintage leather armchair, great condition",
        priceDisplay = "$150",
        buyNowPrice = "$899",
        isClassified = false,
    ),
    DiscoverItemUi(
        id = "2",
        imageUrl = "https://example.com/image2.jpg",
        location = "Wellington Central",
        title = "Espresso machine, barely used",
        priceDisplay = "$250",
        buyNowPrice = null,
        isClassified = true,
    ),
)

@Preview(showBackground = true)
@Composable
private fun DiscoverScreenPreviewLight() {
    TradeMeTestTheme(darkTheme = false) {
        DiscoverScreen(
            state = DiscoverUiState(items = PreviewItems),
            onIntent = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DiscoverScreenPreviewDark() {
    TradeMeTestTheme(darkTheme = true) {
        DiscoverScreen(
            state = DiscoverUiState(items = PreviewItems),
            onIntent = {},
        )
    }
}
