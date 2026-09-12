package nz.co.trademetest.feature.watchlist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource

/**
 * No Scaffold here: the app-level Scaffold (see MainActivity/TradeMeTestApp) owns
 * bottom/horizontal insets for all tabs, and passes down [contentPadding] for
 * whatever this screen needs to inset its own content by.
 */
@Composable
internal fun WatchlistScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        Text(stringResource(R.string.watchlist_title))
    }
}
