package dev.avadhut.wist.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import dev.avadhut.wist.client.WistApiClient
import dev.avadhut.wist.ui.components.atoms.AppLogoText
import dev.avadhut.wist.ui.components.atoms.WistButton
import dev.avadhut.wist.ui.components.atoms.WistButtonStyle
import dev.avadhut.wist.ui.components.atoms.WistTextField
import dev.avadhut.wist.ui.theme.AccentPrimary
import dev.avadhut.wist.ui.theme.AlertRed
import dev.avadhut.wist.ui.theme.BackgroundPrimary
import dev.avadhut.wist.ui.theme.TextPrimary
import dev.avadhut.wist.ui.theme.TextSecondary
import dev.avadhut.wist.ui.theme.WistDimensions
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    apiClient: WistApiClient,
    onLoginSuccess: () -> Unit,
    onNavigateToSignup: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary)
            .padding(horizontal = WistDimensions.ScreenPaddingHorizontal)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppLogoText()

            Spacer(modifier = Modifier.height(WistDimensions.SpacingXxxl))

            Text(
                text = "Welcome back",
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(WistDimensions.SpacingXxl))

            WistTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = "Email"
            )

            Spacer(modifier = Modifier.height(WistDimensions.SpacingLg))

            WistTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = "Password",
                isPassword = true
            )

            if (error != null) {
                Spacer(modifier = Modifier.height(WistDimensions.SpacingMd))
                Text(
                    text = error!!,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AlertRed
                )
            }

            Spacer(modifier = Modifier.height(WistDimensions.SpacingXl))

            if (isLoading) {
                CircularProgressIndicator(color = AccentPrimary)
            } else {
                WistButton(
                    text = "Log in",
                    onClick = {
                        error = null
                        isLoading = true
                        scope.launch {
                            apiClient.auth.login(email.trim(), password).onSuccess { response ->
                                apiClient.setToken(response.token)
                                onLoginSuccess()
                            }.onFailure { e ->
                                error = e.message ?: "Login failed"
                            }
                            isLoading = false
                        }
                    },
                    style = WistButtonStyle.PRIMARY,
                    fillMaxWidth = true,
                    enabled = email.isNotBlank() && password.isNotBlank()
                )
            }

            Spacer(modifier = Modifier.height(WistDimensions.SpacingXl))

            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = TextSecondary)) {
                        append("Don't have an account? ")
                    }
                    withStyle(SpanStyle(color = AccentPrimary)) {
                        append("Sign up")
                    }
                },
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable { onNavigateToSignup() }
            )
        }
    }
}
