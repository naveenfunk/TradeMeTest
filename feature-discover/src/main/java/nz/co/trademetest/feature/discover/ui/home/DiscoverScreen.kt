package nz.co.trademetest.feature.discover.ui.home

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import nz.co.trademetest.core.theme.TradeMeTestTheme
import nz.co.trademetest.feature.discover.DiscoverTopAppBar

@Composable
internal fun DiscoverScreen(
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
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

                    is DiscoverEffect.NavigateToDetail -> onNavigateToDetail(effect.id)
                }
            }
        }
    }

    DiscoverScreen(
        state = state,
        onIntent = remember(viewModel) { viewModel::onIntent },
        modifier = modifier,
        contentPadding = contentPadding,
    )
}

/**
 * No Scaffold here: the app-level Scaffold (see MainActivity/TradeMeTestApp) owns
 * bottom/horizontal insets for all tabs. This screen owns its own top bar and
 * applies [contentPadding] to its scrollable content only, so the last list item
 * clears the bottom navigation bar/rail without double-padding the top bar.
 */
@Composable
internal fun DiscoverScreen(
    state: DiscoverUiState,
    onIntent: (DiscoverIntent) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    listState: LazyListState = rememberLazyListState(),
) {
    Column(modifier = modifier.fillMaxSize()) {
        DiscoverTopAppBar(
            onSearchClick = { onIntent(DiscoverIntent.SearchClicked) },
            onCartClick = { onIntent(DiscoverIntent.CartClicked) },
        )
        when {
            state.isLoading -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }

            state.errorMessage != null -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(state.errorMessage),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp),
                )
            }

            else -> LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = contentPadding,
            ) {
                items(
                    items = state.items,
                    key = { it.id },
                    contentType = { "discover_item" },
                ) { item ->
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
            state = DiscoverUiState(items = PreviewItems, isLoading = false),
            onIntent = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DiscoverScreenPreviewDark() {
    TradeMeTestTheme(darkTheme = true) {
        DiscoverScreen(
            state = DiscoverUiState(items = PreviewItems, isLoading = false),
            onIntent = {},
        )
    }
}
