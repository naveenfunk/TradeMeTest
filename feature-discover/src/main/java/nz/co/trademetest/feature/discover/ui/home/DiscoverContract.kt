package nz.co.trademetest.feature.discover.ui.home

import androidx.annotation.StringRes

data class DiscoverUiState(
    val items: List<DiscoverItemUi> = emptyList(),
)

sealed interface DiscoverIntent {
    data object CartClicked : DiscoverIntent
    data object SearchClicked : DiscoverIntent
    data class ItemClicked(val id: String) : DiscoverIntent
}

sealed interface DiscoverEffect {
    data class ShowMessage(@StringRes val message: Int) : DiscoverEffect
}
