package cn.noblel.demo.mediacodec

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import cn.noblel.demo.R
import cn.noblel.demo.base.BaseActivity

class VideoDecodeActivity : BaseActivity(), SurfaceHolder.Callback {
    companion object {
        const val REQUEST_PERMISSION_OK = 0x1
    }

    private var mSurfaceView: SurfaceView? = null
    private var mVideoCodecWorker: VideoMediaCodecWorker? = null
    private var mAudioCodecWorker: AudioMediaCodecWorker? = null
    private var mVideoPath = "/sdcard/DCIM/Camera/671160acf7f62f9b95a9decb1a970c1a.mp4"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_decode)
        mSurfaceView = findViewById(R.id.surface)
        mSurfaceView!!.holder?.addCallback(this)
    }

    override fun onResume() {
        super.onResume()
        checkPermission()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_OK) {
            if (grantResults.size > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,  "存储权限已开通", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,  "存储权限被拒绝", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(READ_EXTERNAL_STORAGE), REQUEST_PERMISSION_OK)
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {

    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        if (mVideoCodecWorker == null) {
            mVideoCodecWorker = VideoMediaCodecWorker(holder!!.surface, mVideoPath)
            mVideoCodecWorker!!.start()
        }
        if (mAudioCodecWorker == null) {
            mAudioCodecWorker = AudioMediaCodecWorker(mVideoPath)
            mAudioCodecWorker!!.start()
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        mVideoCodecWorker?.interrupt()
        mVideoCodecWorker?.join()
        mAudioCodecWorker?.interrupt()
        mAudioCodecWorker?.join()
    }
}