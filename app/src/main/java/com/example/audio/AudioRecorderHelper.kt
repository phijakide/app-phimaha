package com.example.audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Base64
import android.util.Log
import java.io.File

class AudioRecorderHelper(private val context: Context) {

    private var recorder: MediaRecorder? = null
    private var currentAudioFile: File? = null
    private var isRecording = false

    fun isCurrentlyRecording(): Boolean = isRecording

    fun startRecording(): Boolean {
        return try {
            val audioFile = File(context.cacheDir, "supplyflow_rec_${System.currentTimeMillis()}.m4a")
            currentAudioFile = audioFile

            val newRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            newRecorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)
                setOutputFile(audioFile.absolutePath)
                prepare()
                start()
            }

            recorder = newRecorder
            isRecording = true
            true
        } catch (e: Exception) {
            Log.e("AudioRecorderHelper", "Failed to start recording: ${e.message}", e)
            isRecording = false
            recorder?.release()
            recorder = null
            false
        }
    }

    fun stopRecording(): AudioRecordingResult? {
        if (!isRecording) return null

        return try {
            recorder?.apply {
                stop()
                release()
            }
            recorder = null
            isRecording = false

            val file = currentAudioFile
            if (file != null && file.exists() && file.length() > 0) {
                val bytes = file.readBytes()
                val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
                AudioRecordingResult(
                    filePath = file.absolutePath,
                    base64Data = base64,
                    fileSizeKb = file.length() / 1024,
                    mimeType = "audio/mp4"
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("AudioRecorderHelper", "Failed to stop recording: ${e.message}", e)
            recorder?.release()
            recorder = null
            isRecording = false
            null
        }
    }

    fun cancelRecording() {
        try {
            recorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            // ignore
        } finally {
            recorder = null
            isRecording = false
            currentAudioFile?.delete()
            currentAudioFile = null
        }
    }
}

data class AudioRecordingResult(
    val filePath: String,
    val base64Data: String,
    val fileSizeKb: Long,
    val mimeType: String
)
