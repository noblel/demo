package cn.noblel.demo.gl

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import cn.noblel.demo.utils.GyroscopeObserver

/**
 * @author noblel
 * @date 2020/9/8
 */
class MyGLSurfaceView(context: Context) : GLSurfaceView(context) {
    private val mTouchScaleFactor = 180.0f / 320
    private var mPreviousX = 0f
    private var mPreviousY = 0f
    private val mRenderer: MyGLRenderer
    private val mGyroscopeObserver: GyroscopeObserver
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mGyroscopeObserver.unSubscribe()
    }

    override fun onTouchEvent(e: MotionEvent): Boolean {
        val x = e.x
        val y = e.y
        when (e.action) {
            MotionEvent.ACTION_MOVE -> {
                var dx = x - mPreviousX
                var dy = y - mPreviousY
                if (y > height * 1.0f / 2) {
                    dx *= -1
                }
                if (x < width * 1.0f / 2) {
                    dy *= -1
                }
                val angle = mRenderer.angle + (dx + dy) * mTouchScaleFactor
                mRenderer.angle = angle
                requestRender()
            }
            MotionEvent.ACTION_DOWN -> {
            }
        }
        mPreviousX = x
        mPreviousY = y
        return true
    }

    init {
        setEGLContextClientVersion(2)
        mRenderer = MyGLRenderer(context)
        setRenderer(mRenderer)
        renderMode = RENDERMODE_WHEN_DIRTY
        mGyroscopeObserver = GyroscopeObserver(context)
        mGyroscopeObserver.subscribe(object:GyroscopeObserver.GyroDataChangeListener {
            override fun onCoordinateUpdate(x: Float, y: Float, z: Float) {
                mRenderer.setAngle(x, y, z)
                requestRender()
            }
        })
    }
}