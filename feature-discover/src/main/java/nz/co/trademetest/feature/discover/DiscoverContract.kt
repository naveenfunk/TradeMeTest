package nz.co.trademetest.feature.discover

import androidx.annotation.StringRes

/**
 * The Discover screen currently renders only static chrome, so it holds no state yet.
 * Real fields (items, loading, error) land with the data layer.
 */
data object DiscoverUiState

sealed interface DiscoverIntent {
    data object CartClicked : DiscoverIntent
    data object SearchClicked : DiscoverIntent
}

sealed interface DiscoverEffect {
    data class ShowMessage(@StringRes val message: Int) : DiscoverEffect
}
