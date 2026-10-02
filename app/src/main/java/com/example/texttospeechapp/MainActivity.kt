package com.example.texttospeechapp

import android.app.Activity
import android.os.Bundle
import android.os.Environment
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import com.example.texttospeechapp.databinding.ActivityMainBinding
import java.io.File
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initTts()

        binding.speakButton.setOnClickListener {
            speakText()
        }

        binding.saveButton.setOnClickListener {
            saveTextAsMp3()
        }
    }

    private fun initTts() {
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale("vi"))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    updateStatus("TTS tiếng Việt không hỗ trợ trên thiết bị này")
                } else {
                    isTtsReady = true
                    updateStatus("TTS đã sẵn sàng")
                }
            } else {
                updateStatus("Khởi tạo TTS thất bại")
            }
        }
    }

    private fun speakText() {
        val text = binding.inputText.text?.toString()?.trim().orEmpty()
        if (text.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập văn bản", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isTtsReady) {
            Toast.makeText(this, "TTS chưa sẵn sàng", Toast.LENGTH_SHORT).show()
            return
        }

        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tts_utterance")
        updateStatus("Đang phát giọng nói...")
    }

    private fun saveTextAsMp3() {
        val text = binding.inputText.text?.toString()?.trim().orEmpty()
        if (text.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập văn bản", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isTtsReady) {
            Toast.makeText(this, "TTS chưa sẵn sàng", Toast.LENGTH_SHORT).show()
            return
        }

        val musicDir = getExternalFilesDir(Environment.DIRECTORY_MUSIC)
        if (musicDir == null) {
            updateStatus("Không thể tạo thư mục lưu âm thanh")
            return
        }

        if (!musicDir.exists()) {
            musicDir.mkdirs()
        }

        val timestamp = System.currentTimeMillis()
        val wavFile = File(musicDir, "speech_$timestamp.wav")
        val mp3File = File(musicDir, "speech_$timestamp.mp3")

        val result = tts?.synthesizeToFile(text, null, wavFile, "save_audio")
        if (result == TextToSpeech.SUCCESS) {
            updateStatus("Đang chuyển WAV sang MP3...")
            convertWavToMp3(wavFile, mp3File)
        } else {
            updateStatus("Không thể tạo file âm thanh")
        }
    }

    private fun convertWavToMp3(wavFile: File, mp3File: File) {
        val command = "-y -i ${wavFile.absolutePath} -vn -ar 44100 -ac 2 -b:a 192k ${mp3File.absolutePath}"
        val session = FFmpegKit.execute(command)
        val returnCode = session.returnCode

        if (ReturnCode.isSuccess(returnCode)) {
            updateStatus("Đã lưu file MP3: ${mp3File.absolutePath}")
            Toast.makeText(this, "Lưu thành công: ${mp3File.name}", Toast.LENGTH_LONG).show()
        } else {
            updateStatus("Lưu file MP3 thất bại. Xem log để debug.")
            Log.e("FFmpeg", "Command failed: ${session.failStackTrace}")
        }

        if (wavFile.exists()) {
            wavFile.delete()
        }
    }

    private fun updateStatus(message: String) {
        binding.statusText.text = message
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}
