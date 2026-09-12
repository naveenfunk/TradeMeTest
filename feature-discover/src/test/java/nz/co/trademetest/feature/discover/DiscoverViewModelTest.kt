package nz.co.trademetest.feature.discover

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import nz.co.trademetest.feature.discover.domain.DiscoverItem
import nz.co.trademetest.feature.discover.domain.DiscoverRepository
import nz.co.trademetest.feature.discover.domain.GetDiscoverItemsUseCase
import nz.co.trademetest.feature.discover.ui.home.DiscoverEffect
import nz.co.trademetest.feature.discover.ui.home.DiscoverIntent
import nz.co.trademetest.feature.discover.ui.home.DiscoverItemUiMapper
import nz.co.trademetest.feature.discover.ui.home.DiscoverUiState
import nz.co.trademetest.feature.discover.ui.home.DiscoverViewModel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
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

    private fun createViewModel(repository: DiscoverRepository): DiscoverViewModel =
        DiscoverViewModel(
            getDiscoverItems = GetDiscoverItemsUseCase(repository),
            mapper = DiscoverItemUiMapper(),
        )

    private fun createViewModel(items: List<DiscoverItem> = emptyList()): DiscoverViewModel {
        val repository = object : DiscoverRepository {
            override fun getDiscoverItems(): Flow<List<DiscoverItem>> = flowOf(items)
        }
        return createViewModel(repository)
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
    fun `ItemClicked intent emits a NavigateToDetail effect for the item's id`() = runTest {
        val viewModel = createViewModel()

        viewModel.effects.test {
            viewModel.onIntent(DiscoverIntent.ItemClicked("1"))

            val effect = awaitItem()
            assertEquals(DiscoverEffect.NavigateToDetail("1"), effect)
        }
    }

    @Test
    fun `uiState starts in a loading state`() = runTest {
        val viewModel = createViewModel()

        viewModel.uiState.test {
            assertEquals(DiscoverUiState.Loading, awaitItem())
        }
    }

    @Test
    fun `uiState maps domain items from the repository`() = runTest {
        val domainItem = DiscoverItem(
            id = "1",
            imageUrl = "https://example.com/image.jpg",
            location = "Auckland City",
            title = "Vintage leather armchair",
            priceDisplay = "$150",
            buyNowPrice = null,
            isClassified = false,
        )
        val viewModel = createViewModel(items = listOf(domainItem))

        viewModel.uiState.test {
            // stateIn(Lazily) emits the seed loading value first; the mapped
            // emission only lands once the upstream flow is drained.
            assertEquals(DiscoverUiState.Loading, awaitItem())

            advanceUntilIdle()

            assertEquals(
                DiscoverUiState(
                    items = DiscoverItemUiMapper().map(listOf(domainItem)),
                    isLoading = false,
                ),
                awaitItem(),
            )
        }
    }

    @Test
    fun `uiState surfaces a server error message for an unrecognised failure`() = runTest {
        val failingRepository = object : DiscoverRepository {
            override fun getDiscoverItems(): Flow<List<DiscoverItem>> = flow {
                throw IllegalStateException("boom")
            }
        }
        val viewModel = createViewModel(failingRepository)

        viewModel.uiState.test {
            assertEquals(DiscoverUiState.Loading, awaitItem())

            advanceUntilIdle()

            val state = awaitItem()
            assertTrue(state.items.isEmpty())
            assertEquals(false, state.isLoading)
            assertNotNull(state.errorMessage)
            assertEquals(R.string.discover_error_server, state.errorMessage)
        }
    }

    @Test
    fun `uiState surfaces an offline error message for connectivity failures`() = runTest {
        val failingRepository = object : DiscoverRepository {
            override fun getDiscoverItems(): Flow<List<DiscoverItem>> = flow {
                throw java.net.UnknownHostException("api.trademe.co.nz")
            }
        }
        val viewModel = createViewModel(failingRepository)

        viewModel.uiState.test {
            assertEquals(DiscoverUiState.Loading, awaitItem())
            advanceUntilIdle()
            assertEquals(R.string.discover_error_offline, awaitItem().errorMessage)
        }
    }

    @Test
    fun `uiState surfaces a credentials error message when credentials are missing`() = runTest {
        val failingRepository = object : DiscoverRepository {
            override fun getDiscoverItems(): Flow<List<DiscoverItem>> = flow {
                throw nz.co.trademetest.core.network.auth.MissingCredentialsException()
            }
        }
        val viewModel = createViewModel(failingRepository)

        viewModel.uiState.test {
            assertEquals(DiscoverUiState.Loading, awaitItem())
            advanceUntilIdle()
            assertEquals(R.string.discover_error_credentials, awaitItem().errorMessage)
        }
    }

    @Test
    fun `uiState marks an empty result as isEmpty rather than an error`() = runTest {
        val viewModel = createViewModel(items = emptyList())

        viewModel.uiState.test {
            assertEquals(DiscoverUiState.Loading, awaitItem())
            advanceUntilIdle()

            val state = awaitItem()
            assertTrue(state.isEmpty)
            assertTrue(state.items.isEmpty())
            assertEquals(null, state.errorMessage)
        }
    }

    @Test
    fun `RetryClicked after a failure re-fetches and reaches content`() = runTest {
        var callCount = 0
        val domainItem = DiscoverItem(
            id = "1",
            imageUrl = "https://example.com/image.jpg",
            location = "Auckland City",
            title = "Vintage leather armchair",
            priceDisplay = "$150",
            buyNowPrice = null,
            isClassified = false,
        )
        val repository = object : DiscoverRepository {
            override fun getDiscoverItems(): Flow<List<DiscoverItem>> = flow {
                callCount++
                if (callCount == 1) {
                    throw IllegalStateException("boom")
                }
                emit(listOf(domainItem))
            }
        }
        val viewModel = createViewModel(repository)

        viewModel.uiState.test {
            assertEquals(DiscoverUiState.Loading, awaitItem())
            advanceUntilIdle()
            assertEquals(R.string.discover_error_server, awaitItem().errorMessage)

            viewModel.onIntent(DiscoverIntent.RetryClicked)

            assertEquals(DiscoverUiState.Loading, awaitItem())
            advanceUntilIdle()

            val state = awaitItem()
            assertEquals(null, state.errorMessage)
            assertEquals(1, state.items.size)
        }
        assertEquals(2, callCount)
    }

    @Test
    fun `uiState keeps loaded items after all collectors unsubscribe and resubscribe`() = runTest {
        val domainItem = DiscoverItem(
            id = "1",
            imageUrl = "https://example.com/image.jpg",
            location = "Auckland City",
            title = "Vintage leather armchair",
            priceDisplay = "$150",
            buyNowPrice = null,
            isClassified = false,
        )
        // An async repository so a restarted upstream would produce a real gap,
        // unlike a synchronous flowOf which would mask a WhileSubscribed regression.
        val repository = object : DiscoverRepository {
            override fun getDiscoverItems(): Flow<List<DiscoverItem>> = flow {
                delay(1)
                emit(listOf(domainItem))
            }
        }
        val viewModel = createViewModel(repository)

        // First collection: drain to the loaded state, then unsubscribe.
        viewModel.uiState.test {
            assertEquals(DiscoverUiState.Loading, awaitItem())
            advanceUntilIdle()
            assertEquals(false, awaitItem().isLoading)
        }

        // Simulate being on another tab for well beyond the old 5s stopTimeout.
        advanceTimeBy(10_000)
        advanceUntilIdle()

        // Re-subscribing must immediately see the cached loaded state, not a
        // freshly-restarted loading state.
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(false, state.isLoading)
            assertEquals(1, state.items.size)
            expectNoEvents()
        }
    }
}
