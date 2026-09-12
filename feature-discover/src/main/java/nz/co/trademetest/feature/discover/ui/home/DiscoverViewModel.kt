package nz.co.trademetest.feature.discover.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import nz.co.trademetest.feature.discover.R
import nz.co.trademetest.feature.discover.domain.GetDiscoverItemsUseCase
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject internal constructor(
    getDiscoverItems: GetDiscoverItemsUseCase,
    mapper: DiscoverItemUiMapper,
) : ViewModel() {

    val uiState: StateFlow<DiscoverUiState> = getDiscoverItems()
        .map { items -> DiscoverUiState(items = mapper.map(items)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DiscoverUiState())

    private val _effects = Channel<DiscoverEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    fun onIntent(intent: DiscoverIntent) {
        when (intent) {
            DiscoverIntent.CartClicked -> sendEffect(DiscoverEffect.ShowMessage(R.string.discover_cart_clicked))
            DiscoverIntent.SearchClicked -> sendEffect(DiscoverEffect.ShowMessage(R.string.discover_search_clicked))
            is DiscoverIntent.ItemClicked -> sendEffect(DiscoverEffect.ShowMessage(R.string.discover_item_clicked))
        }
    }

    private fun sendEffect(effect: DiscoverEffect) {
        viewModelScope.launch {
            _effects.send(effect)
        }
    }
}
