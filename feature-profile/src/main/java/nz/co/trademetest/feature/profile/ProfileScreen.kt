package nz.co.trademetest.feature.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import nz.co.trademetest.core.ui.TradeMeTopAppBar

/**
 * No Scaffold here: the app-level Scaffold (see MainActivity/TradeMeTestApp) owns
 * bottom/horizontal insets for all tabs, and passes down [contentPadding] for
 * whatever this screen needs to inset its own content by. This screen owns its own
 * top app bar, so only the body below it is inset by [contentPadding].
 */
@Composable
internal fun ProfileScreen(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
    Column(modifier = modifier.fillMaxSize()) {
        TradeMeTopAppBar(title = stringResource(R.string.profile_title))
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            Text(stringResource(R.string.profile_title))
        }
    }
}
