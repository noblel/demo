package cn.noblel.demo.ffmpeg

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cn.noblel.demo.R
import cn.noblel.ffmpeg.FFmpegHelper

/**
 * @author noblel
 * @date 2020/9/8
 */
class FFmpegActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "FFmpegActivity"

        init {
            System.loadLibrary("ffmpeg")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ffmpeg)
        val tvInfo = findViewById<TextView>(R.id.tv_info)
        val config = FFmpegHelper.getConfig()
        tvInfo.text = config
        Log.e(TAG, "config: $config")
    }
}