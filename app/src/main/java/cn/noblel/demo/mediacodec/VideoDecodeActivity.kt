package cn.noblel.demo.mediacodec

import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.appcompat.app.AppCompatActivity
import cn.noblel.demo.R

class VideoDecodeActivity : AppCompatActivity(), SurfaceHolder.Callback {

    private var mSurfaceView: SurfaceView? = null
    private var mCodecThread: MediaCodecThread? = null
    private var mVideoPath = "https://upos-sz-mirrorhw.bilivideo.com/upgcxcode/87/69/201056987/201056987-1-16.mp4?e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfq9rVEuxTEnE8L5F6VnEsSTx0vkX8fqJeYTj_lta53NCM=&uipk=5&nbs=1&deadline=1599753840&gen=playurl&os=hwbv&oi=1700219086&trid=54c66554209e4ec5b0299c7de9fd7f6fh&platform=html5&upsig=fcbb72c5b979bf711b51eae5c22098f5&uparams=e,uipk,nbs,deadline,gen,os,oi,trid,platform&mid=0&logo=80000000"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_decode)
        mSurfaceView = findViewById(R.id.surface)
        mSurfaceView!!.holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceView!!.holder?.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {

    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        if (mCodecThread == null) {
            mCodecThread = MediaCodecThread(holder!!.surface, mVideoPath)
            mCodecThread!!.start()
        }
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        mCodecThread?.interrupt()
        mCodecThread?.join()
    }
}