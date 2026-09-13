package nz.co.trademetest.core.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import nz.co.trademetest.core.theme.TradeMeTestTheme

/**
 * Stateless top app bar shared by every tab.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradeMeTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        title = {
            Text(
                title,
                fontWeight = FontWeight.Bold,
            )
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
private fun TradeMeTopAppBarPreview() {
    TradeMeTestTheme {
        TradeMeTopAppBar(title = "Watchlist")
    }
}

@Preview(showBackground = true)
@Composable
private fun TradeMeTopAppBarWithActionsPreview() {
    TradeMeTestTheme {
        TradeMeTopAppBar(
            title = "Browse",
            actions = {
                IconButton(onClick = {}) {
                    Text("S")
                }
                IconButton(onClick = {}) {
                    Text("C")
                }
            },
        )
    }
}
