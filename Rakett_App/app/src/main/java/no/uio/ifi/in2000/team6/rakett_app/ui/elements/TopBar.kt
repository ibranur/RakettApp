package no.uio.ifi.in2000.team6.rakett_app.ui.elements

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    title: String,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
    emptyContent: @Composable (() -> Unit)? = null,
    isEmpty: Boolean = false
) {
    if (!isEmpty) {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.primary
            ),
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium
                )
            },
            actions = actions,
            modifier = modifier
        )
    } else if (emptyContent != null) {
        emptyContent()
    }
}

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenTitlePreview() {
    Scaffold(
        topBar = { TopBar(title = "Test", isEmpty = false) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding))
        {
            Text(text = "Preview of Screen Title")
        }
    }

}