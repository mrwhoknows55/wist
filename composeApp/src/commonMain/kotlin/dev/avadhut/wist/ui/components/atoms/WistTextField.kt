package dev.avadhut.wist.ui.components.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import dev.avadhut.wist.ui.theme.BackgroundCard
import dev.avadhut.wist.ui.theme.TextDisabled
import dev.avadhut.wist.ui.theme.TextPrimary
import dev.avadhut.wist.ui.theme.WistDimensions

@Composable
fun WistTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    singleLine: Boolean = true
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left accent border
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(WistDimensions.InputHeight)
                .background(Color.White)
        )

        Spacer(modifier = Modifier.width(WistDimensions.SpacingSm))

        // Text Input
        Box(
            modifier = Modifier
                .weight(1f)
                .height(WistDimensions.InputHeight)
                .clip(RoundedCornerShape(WistDimensions.InputRadius))
                .background(BackgroundCard)
                .padding(horizontal = WistDimensions.SpacingLg),
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = TextPrimary
                ),
                singleLine = singleLine,
                cursorBrush = SolidColor(TextPrimary),
                visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextDisabled
                        )
                    }
                    innerTextField()
                }
            )
        }
    }
}
