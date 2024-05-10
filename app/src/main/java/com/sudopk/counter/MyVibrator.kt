package com.sudopk.counter

import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.annotation.RequiresApi


private val TAG = MyVibrator::class.simpleName

/** Length should be same as `timings` array length in [createVibrateEffect] function. */
@RequiresApi(Build.VERSION_CODES.O) private val VIBRATION_AMPLITUDES = intArrayOf(
  VibrationEffect.DEFAULT_AMPLITUDE,
  VibrationEffect.DEFAULT_AMPLITUDE,
  VibrationEffect.DEFAULT_AMPLITUDE,
  VibrationEffect.DEFAULT_AMPLITUDE,
)

interface MyVibrator {
  fun vibrate(milliSeconds: Long)
}

object NoVibrator : MyVibrator {
  override fun vibrate(milliSeconds: Long) {
    Log.d(TAG, "No vibrator")
  }
}

class PreSVibrator(private val vibrator: Vibrator) : MyVibrator {
  override fun vibrate(milliSeconds: Long) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      vibrator.vibrate(createVibrateEffect(milliSeconds))
    } else {
      @Suppress("DEPRECATION")
      vibrator.vibrate(milliSeconds)
    }
  }
}

@RequiresApi(Build.VERSION_CODES.S)
class SOnwardsVibrator(private val vibratorManager: VibratorManager) : MyVibrator {
  override fun vibrate(milliSeconds: Long) {
    vibratorManager.vibrate(CombinedVibration.createParallel(createVibrateEffect(milliSeconds)))
  }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun createVibrateEffect(milliSeconds: Long): VibrationEffect {
  // Not sure if it is a bug, but if array size is less than 4, vibration doesn't work.
  // For the same reason, VibrationEffect.createOneShot method also doesn't work; which internally
  // calls VibrationEffect.createWaveform method with size 1 timings array.
  val timings = longArrayOf(0, 0, 0, milliSeconds)
  return VibrationEffect.createWaveform(timings, VIBRATION_AMPLITUDES, /*repeat=*/-1)
}