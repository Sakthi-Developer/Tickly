package com.sakthi.tickly

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    closeDialog: () -> Unit
) {
    val timerModeState = viewModel.timerMode.collectAsState()
    val focusManager = LocalFocusManager.current

    // Local states for inputs
    var pomodoro by remember {
        mutableStateOf(
            timerModeState.value[TimerMode.Pomodoro]?.toString() ?: "25"
        )
    }
    var shortBreak by remember {
        mutableStateOf(
            timerModeState.value[TimerMode.ShortBreak]?.toString() ?: "5"
        )
    }
    var longBreak by remember {
        mutableStateOf(
            timerModeState.value[TimerMode.LongBreak]?.toString() ?: "15"
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable { focusManager.clearFocus() }
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Title
            Text(
                text = "Timer Settings",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.align(Alignment.Start)
            )

            // Pomodoro Duration
            CustomInputField(
                label = "Pomodoro Time (mins)",
                value = pomodoro,
                onValueChange = { pomodoro = it },
                leadingIcon = {
                    Icon(
                        painterResource(R.drawable.baseline_access_time_24),
                        contentDescription = null,
                        tint = Color.Black
                    )
                }
            )

            // Short Break Duration
            CustomInputField(
                label = "Short Break (mins)",
                value = shortBreak,
                onValueChange = { shortBreak = it },
                leadingIcon = {
                    Icon(
                        painterResource(R.drawable.baseline_access_time_24),
                        contentDescription = null,
                        tint = Color.Black
                    )
                }
            )

            // Long Break Duration
            CustomInputField(
                label = "Long Break (mins)",
                value = longBreak,
                onValueChange = { longBreak = it },
                leadingIcon = {
                    Icon(
                        painterResource(R.drawable.baseline_access_time_24),
                        contentDescription = null,
                        tint = Color.Black
                    )
                }
            )

            // Save Button
            CustomButton(
                text = "Save Settings",
                onClick = {
                    focusManager.clearFocus()
                    viewModel.saveDuration(TimerMode.Pomodoro, pomodoro.toIntOrNull() ?: 25)
                    viewModel.saveDuration(TimerMode.ShortBreak, shortBreak.toIntOrNull() ?: 5)
                    viewModel.saveDuration(TimerMode.LongBreak, longBreak.toIntOrNull() ?: 15)
                    closeDialog()
                }
            )
        }
    }
}

@Composable
private fun CustomInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null
) {
    var isFocused by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { isFocused = it.isFocused },
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = Color.Black
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color.White,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = if (isFocused) Color.Black else Color.LightGray,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    leadingIcon?.invoke()
                    innerTextField()
                }
            }
        )
    }
}

@Composable
private fun CustomButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(12.dp)
            )
    )
    {
        TextButton(
            onClick = onClick,
            colors = ButtonDefaults.textButtonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}





