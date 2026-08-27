package com.anas.kegelflow.util

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log

class AudioHapticHelper(private val context: Context) {

    private var toneGenerator: ToneGenerator? = null

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 80)
        } catch (e: Exception) {
            Log.e("AudioHapticHelper", "Failed to initialize ToneGenerator", e)
        }
    }

    fun playStageTransitionSound(stageName: String) {
        try {
            toneGenerator?.let { tone ->
                val toneType = when (stageName) {
                    "CONTRACT" -> ToneGenerator.TONE_PROP_BEEP2
                    "RELAX" -> ToneGenerator.TONE_PROP_BEEP
                    "FINISHED" -> ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD
                    else -> ToneGenerator.TONE_PROP_BEEP
                }
                tone.startTone(toneType, 200)
            }
        } catch (e: Exception) {
            Log.e("AudioHapticHelper", "Error playing sound", e)
        }
    }

    fun triggerVibration(patternType: String = "NORMAL") {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager =
                    context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val duration = if (patternType == "HEAVY") 400L else 200L
                    val effect = VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE)
                    vibrator.vibrate(effect)
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(200)
                }
            }
        } catch (e: Exception) {
            Log.e("AudioHapticHelper", "Error triggering vibration", e)
        }
    }

    fun release() {
        try {
            toneGenerator?.release()
            toneGenerator = null
        } catch (e: Exception) {
            Log.e("AudioHapticHelper", "Error releasing ToneGenerator", e)
        }
    }
}
