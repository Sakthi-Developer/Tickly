package com.sakthi.tickly

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.sakthi.tickly.ui.theme.TicklyTheme
import java.util.Locale

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "timer_preferences")


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = MainViewModel(DataStoreManager(this))
        enableEdgeToEdge()
        setContent {

            val timerValue by viewModel.timerValue.collectAsState()
            TicklyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val minutes = timerValue / 60
                    val seconds = timerValue % 60


                    Box {

                        Image(
                            painter = painterResource(id = R.drawable.bg),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {

                            OptionsSelector(viewModel)

                            Text(
                                text = String.format(
                                    Locale.ROOT,
                                    "%02d:%02d",
                                    minutes,
                                    seconds
                                ),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 100.sp,
                                fontStyle = FontStyle.Italic,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(top = 24.dp, bottom = 24.dp)
                            )


                            Row(
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {

                                var isTimerStarted = viewModel.isTimerStarted.collectAsState()

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (isTimerStarted.value) Color.Transparent else Color.White)
                                        .border(1.dp, Color.White, CircleShape)
                                        .clickable {
                                            if (isTimerStarted.value) {
                                                viewModel.pauseTimer()
                                                viewModel.changeTimerStartedState()
                                            } else {
                                                viewModel.startTimer()
                                                viewModel.changeTimerStartedState()
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isTimerStarted.value) "Pause" else "Start",
                                        fontWeight = FontWeight.Bold,
                                        color = if (isTimerStarted.value) Color.White else Color.Black,
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.padding(
                                            top = 8.dp,
                                            bottom = 8.dp,
                                            start = 16.dp,
                                            end = 16.dp
                                        )
                                    )
                                }



                                Spacer(modifier = Modifier.size(16.dp))

                                Icon(
                                    painter = painterResource(R.drawable.baseline_restart_alt_24),
                                    contentDescription = "Restart",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(35.dp)
                                        .clickable {
                                            viewModel.restartTimer()
                                        }
                                )
                            }

                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OptionsSelector(viewModel: MainViewModel) {
    var selectedOption = viewModel.selectedTimerMode.collectAsState().value

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Absolute.Center
    ) {
        CustomLabel(
            label = "Pomodoro",
            isEnabled = selectedOption == TimerMode.Pomodoro,
        ) {
            viewModel.setSelectedTimerMode(TimerMode.Pomodoro)
        }
        CustomLabel(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
            label = "Long Break",
            isEnabled = selectedOption == TimerMode.LongBreak,
        ) {
            viewModel.setSelectedTimerMode(TimerMode.LongBreak)
        }
        CustomLabel(
            label = "Short Break",
            isEnabled = selectedOption == TimerMode.ShortBreak,
        ) {
            viewModel.setSelectedTimerMode(TimerMode.ShortBreak)
        }
    }
}


@Composable
fun CustomLabel(
    modifier: Modifier = Modifier,
    label: String,
    isEnabled: Boolean,
    onClick: () -> Unit
) {

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isEnabled) Color.White else Color.Transparent)
            .border(1.dp, Color.White, CircleShape)
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isEnabled) Color.Black else Color.White,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 6.dp, bottom = 6.dp, start = 12.dp, end = 12.dp)
        )
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TicklyTheme {
    }
}