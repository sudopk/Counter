package com.sudopk.counter

import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.booleanResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.getSystemService
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.sudopk.counter.ui.theme.CounterTheme
import com.sudopk.kandroid.notFoundByTag
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


private val TAG = CounterMainActivity::class.simpleName

private const val COUNT_IN_A_ROUND = 108

private val TIME_FORMATTER = SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH)

class CounterMainActivity : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: MyVibrator = NoVibrator
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(/*context=*/this)

        setContent {
            CounterTheme(window) {
                Surface {
                    val coroutine = rememberCoroutineScope()

                    val settingsDefaultValues = SettingsDefaultValues(
                        booleanResource(R.bool.short_audio_default),
                        booleanResource(R.bool.short_vibrate_default),
                        booleanResource(R.bool.long_vibrate_default),
                    )

                    CounterApp(sharedPreferences, onShowSettings = {
                        supportFragmentManager.notFoundByTag(SettingsDialogFragment.TAG) { tag ->
                            SettingsDialogFragment().show(supportFragmentManager, tag)
                        }
                    }) {
                        coroutine.onNewCount(it, settingsDefaultValues)
                    }
                }
            }
        }
    }

    private fun CoroutineScope.onNewCount(
        count: Int,
        settingsDefaultValues: SettingsDefaultValues
    ) {
        this.launch {
            if (sharedPreferences.getBoolean(
                    "short_audio",
                    settingsDefaultValues.shortAudioDefault
                )
            ) {
                while (mediaPlayer?.isPlaying == true) {
                    delay(50)
                }
                mediaPlayer?.start() ?: Log.d(TAG, "MediaPlayer not ready")
            }

            if (count > 0 && count % COUNT_IN_A_ROUND == 0) {
                if (sharedPreferences.getBoolean(
                        "long_vibrate",
                        settingsDefaultValues.shortVibrateDefault
                    )
                ) {
                    vibrator.vibrate(2000)
                }
            } else {
                if (sharedPreferences.getBoolean(
                        "short_vibrate",
                        settingsDefaultValues.longVibrateDefault
                    )
                ) {
                    vibrator.vibrate(30)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch(Dispatchers.Default) {
            vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                SOnwardsVibrator(getSystemService()!!)
            } else {
                PreSVibrator(getSystemService()!!)
            }
            mediaPlayer = MediaPlayer.create(applicationContext, R.raw.button_click)
        }
    }

    override fun onPause() {
        super.onPause()
        lifecycleScope.launch {
            mediaPlayer?.release()
            mediaPlayer = null

            vibrator = NoVibrator
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounterApp(
    preferences: SharedPreferences,
    onShowSettings: () -> Unit,
    onCounterChange: (count: Int) -> Unit,
) {
    val count = rememberSaveable { mutableIntStateOf(0) }
    val startTime = rememberSaveable {
        mutableStateOf(Calendar.getInstance())
    }
    if (count.intValue == 1) {
        startTime.value = Calendar.getInstance()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
//                windowInsets = WindowInsets(left = 0, top = 0, right = 0, bottom = 0),
                actions = {
                    IconButton(onClick = onShowSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                })
        },
        bottomBar = { CounterBottomBar(count, onCounterChange) },
    ) { padding ->
        Box(Modifier.padding(padding)) {
            val lastClickTimeMs = remember { mutableLongStateOf(System.currentTimeMillis()) }
            val defaultMinClickInterval = integerResource(R.integer.min_click_interval_ms)
            TextButton(
                onClick = {
                    // getInt can't be used because EditTextPreference returns String.
                    val minClickInterval =
                        preferences.getString("min_click_interval_ms", null)?.toIntOrNull()
                            ?: defaultMinClickInterval
                    if (System.currentTimeMillis() < lastClickTimeMs.longValue + minClickInterval) {
                        Log.d(TAG, "Ignoring double click")
                    } else {
                        lastClickTimeMs.longValue = System.currentTimeMillis()
                        count.intValue++
                        onCounterChange(count.intValue)
                    }
                },
                modifier = Modifier.fillMaxSize(),
            ) {
                val currentRoundCount = count.intValue % COUNT_IN_A_ROUND
                Text(currentRoundCount.toString(), style = MaterialTheme.typography.displayLarge)
            }
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    "Rounds: ${count.intValue / COUNT_IN_A_ROUND}",
                    style = MaterialTheme.typography.titleLarge
                )
                if (count.intValue != 0) {
                    Text(
                        "Started at: ${TIME_FORMATTER.format(startTime.value.time)}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun CounterBottomBar(count: MutableState<Int>, onCounterChange: (count: Int) -> Unit) {
    Row(
        Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        OutlinedButton(onClick = {
            count.value--
            onCounterChange(count.value)
        }, Modifier.weight(4f)) {
            val contentDescription = "Subtract"
            Icon(
                painterResource(R.drawable.ic_baseline_remove_circle_outline_24),
                contentDescription
            )
            Spacer(Modifier.width(4.dp))
            Text(contentDescription)
        }
        Spacer(Modifier.weight(1f))
        OutlinedButton(onClick = {
            if (count.value != 0) {
                count.value = 0
                onCounterChange(count.value)
            }
        }, Modifier.weight(4f)) {
            Row {
                val contentDescription = "Reset"
                Icon(painterResource(R.drawable.ic_baseline_restore_24), contentDescription)
                Spacer(Modifier.width(4.dp))
                Text(contentDescription)
            }
        }
    }
}

private data class SettingsDefaultValues(
    val shortAudioDefault: Boolean,
    val shortVibrateDefault: Boolean,
    val longVibrateDefault: Boolean,
)
