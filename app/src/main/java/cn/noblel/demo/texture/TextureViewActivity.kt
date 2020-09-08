package cn.noblel.demo.texture

import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.opengl.Matrix
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface
import android.view.TextureView
import androidx.appcompat.app.AppCompatActivity
import cn.noblel.demo.R
import cn.noblel.demo.gl.egl.EglCore
import cn.noblel.demo.gl.egl.EglCore.Companion.FLAG_RECORDABLE
import cn.noblel.demo.gl.egl.WindowSurface
import cn.noblel.demo.gl.shape.Image

/**
 * @author noblel
 * @date 2020/9/8
 */
private const val TAG: String = "TextureViewActivity"

class TextureViewActivity : AppCompatActivity(), TextureView.SurfaceTextureListener {
    private var mEglCore: EglCore? = null
    private var mDrawHandler: Handler? = null
    private var mDisplaySurface: WindowSurface? = null
    private var mProducer: SurfaceTexture? = null
    private var mTextures = 0
    private var mImage: Image? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shader)
        val textureView = findViewById<TextureView>(R.id.tv_test)
        textureView.surfaceTextureListener = this
        val drawThread = HandlerThread("drawThread")
        drawThread.start()
        mDrawHandler = Handler(drawThread.looper)
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        //主线程上回调
        Log.d(TAG, "onSurfaceTextureAvailable on " + Thread.currentThread().name + " thread.")
        runOnDrawThread {
            //创建EGL环境
            mEglCore = EglCore(null, FLAG_RECORDABLE)
            mDisplaySurface = WindowSurface(mEglCore!!, Surface(surface), false)
            //把context绑定在当前线程以及draw和read的surface
            mDisplaySurface!!.makeCurrent()
            //创建生产者
            mTextures = initTexture()
            mProducer = SurfaceTexture(mTextures)
            val mvp = FloatArray(16)
            Matrix.setIdentityM(mvp, 0)
            mImage?.draw(mvp)
            //交换缓冲区
            mDisplaySurface!!.swapBuffers()
        }
    }

    private fun initTexture(): Int {
        //加载纹理
        mImage = Image(this, R.drawable.sample)
        return mImage!!.imageTexture
    }

    private fun runOnDrawThread(runnable: Runnable) {
        if (mDrawHandler != null) {
            mDrawHandler!!.post(runnable)
        }
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        Log.d(TAG, "onSurfaceTextureSizeChanged on " + Thread.currentThread().name + " thread.")
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        Log.d(TAG, "onSurfaceTextureDestroyed on " + Thread.currentThread().name + " thread.")
        runOnDrawThread(Runnable {
            GLES20.glDeleteTextures(1, intArrayOf(mTextures), 0)
            mProducer!!.release()
            mDisplaySurface?.release()
            mDrawHandler!!.looper.quitSafely()
            mDrawHandler = null
        })
        return false
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
        Log.d(TAG, "onSurfaceTextureUpdated on " + Thread.currentThread().name + " thread.")
    }
}

