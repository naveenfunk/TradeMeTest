package nz.co.trademetest.feature.discover

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview

/**
 * Stateless Browse top app bar: title on the left, Cart and Search actions on the right.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverTopAppBar(
    onSearchClick: () -> Unit,
    onCartClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = { Text(stringResource(R.string.discover_browse_title)) },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    painter = painterResource(R.drawable.discover_ic_search),
                    contentDescription = stringResource(R.string.discover_search_content_description),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
            IconButton(onClick = onCartClick) {
                Icon(
                    painter = painterResource(R.drawable.discover_ic_cart),
                    contentDescription = stringResource(R.string.discover_cart_content_description),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
private fun DiscoverTopAppBarPreview() {
    DiscoverTopAppBar(
        onSearchClick = {},
        onCartClick = {},
    )
}
