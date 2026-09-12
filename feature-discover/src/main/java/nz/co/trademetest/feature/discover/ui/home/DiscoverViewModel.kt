package nz.co.trademetest.feature.discover.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import nz.co.trademetest.feature.discover.R
import nz.co.trademetest.feature.discover.domain.GetDiscoverItemsUseCase
import javax.inject.Inject

@HiltViewModel
internal class DiscoverViewModel @Inject internal constructor(
    getDiscoverItems: GetDiscoverItemsUseCase,
    mapper: DiscoverItemUiMapper,
) : ViewModel() {

    val uiState: StateFlow<DiscoverUiState> = getDiscoverItems()
        .map { items -> DiscoverUiState(items = mapper.map(items), isLoading = false) }
        .catch { emit(DiscoverUiState(isLoading = false, errorMessage = R.string.discover_load_error)) }
        // Lazily, not WhileSubscribed: this ViewModel is scoped to the DiscoverHome
        // NavBackStackEntry via hiltViewModel(), and the entry is saved (not
        // cleared) across tab switches by navigate { saveState = true } +
        // restoreState = true in TradeMeTestApp. So once loaded it should never
        // re-run the upstream fetch just because Discover was briefly off-screen
        // while another tab was shown. It IS re-created (and will re-fetch, correctly)
        // if the entry is genuinely popped rather than saved -- which today means the
        // user re-tapped the already-selected Discover tab to return to its root.
        .stateIn(viewModelScope, SharingStarted.Lazily, DiscoverUiState.Loading)

    private val _effects = Channel<DiscoverEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    fun onIntent(intent: DiscoverIntent) {
        when (intent) {
            DiscoverIntent.CartClicked -> sendEffect(DiscoverEffect.ShowMessage(R.string.discover_cart_clicked))
            DiscoverIntent.SearchClicked -> sendEffect(DiscoverEffect.ShowMessage(R.string.discover_search_clicked))
            is DiscoverIntent.ItemClicked -> sendEffect(DiscoverEffect.NavigateToDetail(intent.id))
        }
    }

    private fun sendEffect(effect: DiscoverEffect) {
        viewModelScope.launch {
            _effects.send(effect)
        }
    }
}
