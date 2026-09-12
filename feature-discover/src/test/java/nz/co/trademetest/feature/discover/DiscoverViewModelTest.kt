package nz.co.trademetest.feature.discover

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import nz.co.trademetest.feature.discover.domain.DiscoverItem
import nz.co.trademetest.feature.discover.data.DiscoverRepository
import nz.co.trademetest.feature.discover.domain.GetDiscoverItemsUseCase
import nz.co.trademetest.feature.discover.ui.home.DiscoverEffect
import nz.co.trademetest.feature.discover.ui.home.DiscoverIntent
import nz.co.trademetest.feature.discover.ui.home.DiscoverItemUiMapper
import nz.co.trademetest.feature.discover.ui.home.DiscoverUiState
import nz.co.trademetest.feature.discover.ui.home.DiscoverViewModel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DiscoverViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(items: List<DiscoverItem> = emptyList()): DiscoverViewModel {
        val repository = object : DiscoverRepository {
            override fun getDiscoverItems(): Flow<List<DiscoverItem>> = flowOf(items)
        }
        return DiscoverViewModel(
            getDiscoverItems = GetDiscoverItemsUseCase(repository),
            mapper = DiscoverItemUiMapper(),
        )
    }

    @Test
    fun `CartClicked intent emits a ShowMessage effect for the cart`() = runTest {
        val viewModel = createViewModel()

        viewModel.effects.test {
            viewModel.onIntent(DiscoverIntent.CartClicked)

            val effect = awaitItem()
            assertEquals(DiscoverEffect.ShowMessage(R.string.discover_cart_clicked), effect)
        }
    }

    @Test
    fun `SearchClicked intent emits a ShowMessage effect for search`() = runTest {
        val viewModel = createViewModel()

        viewModel.effects.test {
            viewModel.onIntent(DiscoverIntent.SearchClicked)

            val effect = awaitItem()
            assertEquals(DiscoverEffect.ShowMessage(R.string.discover_search_clicked), effect)
        }
    }

    @Test
    fun `ItemClicked intent emits a ShowMessage effect for the item`() = runTest {
        val viewModel = createViewModel()

        viewModel.effects.test {
            viewModel.onIntent(DiscoverIntent.ItemClicked("1"))

            val effect = awaitItem()
            assertEquals(DiscoverEffect.ShowMessage(R.string.discover_item_clicked), effect)
        }
    }

    @Test
    fun `uiState maps domain items from the repository`() = runTest {
        val domainItem = DiscoverItem(
            id = "1",
            imageUrl = "https://example.com/image.jpg",
            location = "Auckland City",
            title = "Vintage leather armchair",
            priceDisplayCents = 15000,
            buyNowPriceCents = null,
            isClassified = false,
        )
        val viewModel = createViewModel(items = listOf(domainItem))

        viewModel.uiState.test {
            // stateIn(WhileSubscribed) emits the initialValue first; the mapped
            // emission only lands once the upstream flow is drained.
            assertEquals(DiscoverUiState(), awaitItem())

            advanceUntilIdle()

            assertEquals(
                DiscoverUiState(items = DiscoverItemUiMapper().map(listOf(domainItem))),
                awaitItem(),
            )
        }
    }
}
