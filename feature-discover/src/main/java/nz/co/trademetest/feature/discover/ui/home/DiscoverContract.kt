package nz.co.trademetest.feature.discover.ui.home

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

@Immutable
internal data class DiscoverUiState(
    val items: List<DiscoverItemUi> = emptyList(),
    val isLoading: Boolean = false,
    val isEmpty: Boolean = false,
    @StringRes val errorMessage: Int? = null,
) {
    companion object {
        /** Seed value for [kotlinx.coroutines.flow.stateIn]: nothing fetched yet. */
        val Loading = DiscoverUiState(isLoading = true)
    }
}

internal sealed interface DiscoverIntent {
    data object CartClicked : DiscoverIntent
    data object SearchClicked : DiscoverIntent
    data object RetryClicked : DiscoverIntent
    data class ItemClicked(val id: String, val title: String) : DiscoverIntent
}

internal sealed interface DiscoverEffect {
    data class ShowMessage(@StringRes val message: Int, val arg: String? = null) : DiscoverEffect
}
