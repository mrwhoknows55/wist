package dev.avadhut.wist.ui.components.molecules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import dev.avadhut.wist.ui.components.atoms.WistButton
import dev.avadhut.wist.ui.components.atoms.WistButtonStyle
import dev.avadhut.wist.ui.theme.AlertRed
import dev.avadhut.wist.ui.theme.TextSecondary
import dev.avadhut.wist.ui.theme.WistDimensions

@Composable
fun LoadErrorWithRetry(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(WistDimensions.ScreenPaddingHorizontal),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Couldn't load content",
            style = MaterialTheme.typography.titleMedium,
            color = AlertRed,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(WistDimensions.SpacingMd))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(WistDimensions.SpacingXl))
        WistButton(
            text = "Retry",
            onClick = {
                println("[Wist] LoadErrorWithRetry: Retry tapped")
                onRetry()
            },
            style = WistButtonStyle.PRIMARY
        )
    }
}
