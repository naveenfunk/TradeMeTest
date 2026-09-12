package nz.co.trademetest.feature.discover.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import nz.co.trademetest.feature.discover.R
import nz.co.trademetest.feature.discover.domain.DiscoverError
import nz.co.trademetest.feature.discover.domain.GetDiscoverItemsUseCase
import nz.co.trademetest.feature.discover.domain.toDiscoverError
import javax.inject.Inject

@HiltViewModel
internal class DiscoverViewModel @Inject internal constructor(
    private val getDiscoverItems: GetDiscoverItemsUseCase,
    private val mapper: DiscoverItemUiMapper,
) : ViewModel() {

    private val refreshTrigger = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<DiscoverUiState> = refreshTrigger
        .flatMapLatest {
            getDiscoverItems()
                .map { items ->
                    DiscoverUiState(
                        items = mapper.map(items),
                        isLoading = false,
                        isEmpty = items.isEmpty(),
                    )
                }
                .onStart { emit(DiscoverUiState.Loading) }
                .catch { throwable ->
                    emit(
                        DiscoverUiState(
                            isLoading = false,
                            errorMessage = throwable.toDiscoverError().toMessageRes(),
                        ),
                    )
                }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, DiscoverUiState.Loading)

    private val _effects = Channel<DiscoverEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    fun onIntent(intent: DiscoverIntent) {
        when (intent) {
            DiscoverIntent.CartClicked -> sendEffect(DiscoverEffect.ShowMessage(R.string.discover_cart_clicked))
            DiscoverIntent.SearchClicked -> sendEffect(DiscoverEffect.ShowMessage(R.string.discover_search_clicked))
            DiscoverIntent.RetryClicked -> refreshTrigger.update { it + 1 }
            is DiscoverIntent.ItemClicked -> sendEffect(DiscoverEffect.NavigateToDetail(intent.id))
        }
    }

    private fun sendEffect(effect: DiscoverEffect) {
        viewModelScope.launch {
            _effects.send(effect)
        }
    }
}

private fun DiscoverError.toMessageRes(): Int = when (this) {
    DiscoverError.Offline -> R.string.discover_error_offline
    DiscoverError.MissingCredentials -> R.string.discover_error_credentials
    DiscoverError.Server -> R.string.discover_error_server
}
