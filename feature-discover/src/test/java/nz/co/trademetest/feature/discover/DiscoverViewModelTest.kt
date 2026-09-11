package nz.co.trademetest.feature.discover

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
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

    @Test
    fun `CartClicked intent emits a ShowMessage effect for the cart`() = runTest {
        val viewModel = DiscoverViewModel()

        viewModel.effects.test {
            viewModel.onIntent(DiscoverIntent.CartClicked)

            val effect = awaitItem()
            assertEquals(DiscoverEffect.ShowMessage(R.string.discover_cart_clicked), effect)
        }
    }

    @Test
    fun `SearchClicked intent emits a ShowMessage effect for search`() = runTest {
        val viewModel = DiscoverViewModel()

        viewModel.effects.test {
            viewModel.onIntent(DiscoverIntent.SearchClicked)

            val effect = awaitItem()
            assertEquals(DiscoverEffect.ShowMessage(R.string.discover_search_clicked), effect)
        }
    }
}
