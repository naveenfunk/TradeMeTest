package nz.co.trademetest.feature.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(DiscoverUiState)
    val uiState: StateFlow<DiscoverUiState> = _uiState.asStateFlow()

    private val _effects = Channel<DiscoverEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    fun onIntent(intent: DiscoverIntent) {
        when (intent) {
            DiscoverIntent.CartClicked -> sendEffect(DiscoverEffect.ShowMessage(R.string.discover_cart_clicked))
            DiscoverIntent.SearchClicked -> sendEffect(DiscoverEffect.ShowMessage(R.string.discover_search_clicked))
        }
    }

    private fun sendEffect(effect: DiscoverEffect) {
        viewModelScope.launch {
            _effects.send(effect)
        }
    }
}
