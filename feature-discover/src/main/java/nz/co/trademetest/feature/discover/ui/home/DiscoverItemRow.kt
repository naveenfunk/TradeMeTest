package nz.co.trademetest.feature.discover.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import nz.co.trademetest.core.theme.TradeMeTestTheme
import nz.co.trademetest.feature.discover.R

private val RowModifier = Modifier.fillMaxWidth()
private val RowContentPadding = Modifier.padding(16.dp)
private val ThumbnailModifier = Modifier
    .size(96.dp)
    .clip(RoundedCornerShape(4.dp))

@Composable
internal fun DiscoverItemRow(
    item: DiscoverItemUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .then(RowModifier)
            .clickable(onClick = onClick)
            .then(RowContentPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = ThumbnailModifier,
        )
        Column(
            modifier = Modifier.padding(start = 12.dp),
        ) {
            Text(
                text = item.location,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = item.title,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = item.priceDisplay,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
            )
            if (item.buyNowPrice != null) {
                Row {
                    Text(
                        text = stringResource(R.string.discover_buy_now_label),
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        text = " ${item.buyNowPrice}",
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DiscoverItemRowPreview() {
    TradeMeTestTheme {
        DiscoverItemRow(
            item = DiscoverItemUi(
                id = "1",
                imageUrl = "https://example.com/image.jpg",
                location = "Auckland City",
                title = "Vintage leather armchair, great condition",
                priceDisplay = "$150",
                buyNowPrice = "$899",
                isClassified = false,
            ),
            onClick = {},
        )
    }
}
