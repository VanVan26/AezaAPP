package com.shefivan.aezaapp.presentation.auth

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.shefivan.aezaapp.R
import com.shefivan.aezaapp.presentation.ui.components.AezaTextField

private val Background = Color(0xFFF4F4F4)
private val TextPrimary = Color(0xFF333333)
private val TextSecondary = Color(0xFF999999)

@Composable
fun AuthScreen(
    onNavigateToHome: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                AuthViewModel.UiEvent.NavigateToHome -> onNavigateToHome()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(color = TextPrimary)
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(R.drawable.logo_letter_a),
                        contentDescription = null,
                        modifier = Modifier.size(52.dp),
                    )
                    Image(
                        painter = painterResource(R.drawable.logo_letter_e),
                        contentDescription = null,
                        modifier = Modifier.size(52.dp),
                    )
                    Image(
                        painter = painterResource(R.drawable.logo_letter_z),
                        contentDescription = null,
                        modifier = Modifier.size(52.dp),
                    )
                    Image(
                        painter = painterResource(R.drawable.logo_letter_a),
                        contentDescription = null,
                        modifier = Modifier.size(52.dp),
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Введите API-ключ для входа",
                    fontSize = 14.sp,
                    color = TextSecondary,
                )
                Spacer(modifier = Modifier.height(32.dp))
                AezaTextField(
                    value = uiState.apiKey,
                    onValueChange = { viewModel.processCommand(AuthViewModel.Command.ApiKeyChanged(it)) },
                    label = "API-ключ",
                    visualTransformation = if (uiState.showApiKey) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(onDone = { viewModel.processCommand(AuthViewModel.Command.Submit) }),
                    trailingIcon = {
                        IconButton(onClick = { viewModel.processCommand(AuthViewModel.Command.ToggleShowApiKey) }) {
                            Icon(
                                imageVector = if (uiState.showApiKey) {
                                    Icons.Filled.VisibilityOff
                                } else {
                                    Icons.Filled.Visibility
                                },
                                contentDescription = null,
                                tint = TextSecondary,
                            )
                        }
                    },
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Получить API-ключ ",
                    fontSize = 14.sp,
                    color = TextPrimary,
                    modifier = Modifier
                        .align(Alignment.End)
                        .clickable {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                "https://my.aeza.net/settings/apikeys".toUri(),
                            )
                            context.startActivity(intent)
                        }
                        .padding(vertical = 4.dp),
                )
                Spacer(modifier = Modifier.height(16.dp))
                val error = uiState.error
                if (error != null) {
                    Text(
                        text = error,
                        fontSize = 13.sp,
                        color = Color(0xFFD32F2F),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                    )
                }
                Button(
                    onClick = { viewModel.processCommand(AuthViewModel.Command.Submit) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TextPrimary,
                        contentColor = Color.White,
                    ),
                ) {
                    Text(
                        text = "Войти",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}
